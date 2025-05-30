import com.google.gson.Gson;
import dao.ChatMemberDao;
import dao.MessageDao;
import dto.MessageDTO;
import entity.Message;
import entity.User;
import util.JwtUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static util.HibernateUtil.sessionFactory;

public class ChatWebSocketServer extends WebSocketServer {

    private static final Gson gson = new Gson();
    private final ChatMemberDao chatMemberDao = new ChatMemberDao();
    private final MessageDao    messageDao    = new MessageDao();

    private static final ConcurrentHashMap<Long, Set<WebSocket>> sessionsPerChat = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<WebSocket, Long> userBySocket = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<WebSocket, Long> chatBySocket = new ConcurrentHashMap<>();

    public ChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server listening on port " + getPort());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake hs) {
        // JWT Auth
        String auth = hs.getFieldValue("Authorization");
        if (auth == null) {
            conn.close(1008, "Missing or invalid Authorization header");
            return;
        }
        String username = JwtUtil.validateToken(auth);
        if (username == null) {
            conn.close(1008, "Unauthorized");
            return;
        }

        // Load User
        Long userId;
        try (var session = sessionFactory.openSession()) {
            User user = session.createQuery(
                            "FROM User u WHERE u.username = :uname", User.class)
                    .setParameter("uname", username)
                    .getSingleResult();
            userId = user.getId();
            // update lastOnline
            session.beginTransaction();
            user.setLastOnline(new java.util.Date());
            session.merge(user);
            session.getTransaction().commit();
        } catch (Exception ex) {
            conn.close(1008, "User lookup failed");
            return;
        }

        // extract chatId from "/ws/chat/{chatId}"
        String path = hs.getResourceDescriptor();
        Long chatId;
        try {
            chatId = Long.valueOf(path.substring(path.lastIndexOf('/') + 1));
        } catch (NumberFormatException e) {
            conn.close(1008, "Invalid chatId");
            return;
        }

        // membership check
        if (!chatMemberDao.isMember(chatId, userId)) {
            conn.close(1008, "Forbidden: not a chat member");
            return;
        }

        // register
        userBySocket.put(conn, userId);
        chatBySocket.put(conn, chatId);
        sessionsPerChat
                .computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet())
                .add(conn);

        System.out.printf("User %d joined chat %d%n", userId, chatId);
    }

    @Override
    public void onMessage(WebSocket conn, String json) {
        Long userId = userBySocket.get(conn);
        Long chatId = chatBySocket.get(conn);
        if (userId == null || chatId == null) {
            conn.send("Error: not authenticated or not in a chat");
            return;
        }

        MessageDTO dto = gson.fromJson(json, MessageDTO.class);

        // persist via DAO
        Message saved = messageDao.saveMessage(
                userId,
                chatId,
                dto.content,
                dto.replyToId,
                dto.mediaUrls
        );

        // build outbound DTO
        MessageDTO out = new MessageDTO();
        out.messageId = saved.getId();
        out.senderId  = userId;
        out.chatId    = chatId;
        out.content   = saved.getContent();
        out.replyToId = saved.getReplyTo() == null ? null : saved.getReplyTo().getId();
        out.mediaUrls = dto.mediaUrls;
        out.sentAt    = saved.getSentAt().getTime();

        String outJson = gson.toJson(out);

        // broadcast
        sessionsPerChat.getOrDefault(chatId, Set.of()).forEach(peer -> {
            if (peer.isOpen()) peer.send(outJson);
        });
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Long chatId = chatBySocket.remove(conn);
        userBySocket.remove(conn);
        if (chatId != null) {
            var set = sessionsPerChat.get(chatId);
            if (set != null) set.remove(conn);
        }
        System.out.printf("Connection closed: code=%d reason=%s%n", code, reason);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        new ChatWebSocketServer(8080).start();
    }
}
