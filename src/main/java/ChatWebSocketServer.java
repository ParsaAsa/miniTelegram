import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import dao.MessageDao;
import entity.*;
import com.google.gson.Gson;
import java.sql.Timestamp;
import java.net.InetSocketAddress;
import org.hibernate.Session;
import org.hibernate.Transaction;
import dto.IncomingMessageDTO;

import static util.HibernateUtil.sessionFactory;

public class ChatWebSocketServer extends WebSocketServer {

    private final MessageDao messageDao = new MessageDao();

    // Constructor that accepts the port
    public ChatWebSocketServer(int port) {
        // Passing InetSocketAddress to the super constructor
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String messageJson) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // Parse incoming JSON
            IncomingMessageDTO dto = new Gson().fromJson(messageJson, IncomingMessageDTO.class);

            // Load required entities
            User sender = session.get(User.class, dto.senderId);
            Chat chat = session.get(Chat.class, dto.chatId);
            Message replyTo = dto.replyToId != null ? session.get(Message.class, dto.replyToId) : null;

            // Create and persist message
            Message message = new Message(sender, chat, dto.content, new Timestamp(System.currentTimeMillis()), replyTo);
            session.persist(message);

            // Handle media list (if any)
            if (dto.mediaUrls != null) {
                for (String url : dto.mediaUrls) {
                    Media media = new Media(url, message);
                    session.persist(media);
                }
            }

            tx.commit();

            // Broadcast or respond as needed
            conn.send("Message received and saved.");

        } catch (Exception e) {
            e.printStackTrace();
            conn.send("Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started");
    }

    public static void main(String[] args) {
        int port = 8080; // Port for the WebSocket server
        ChatWebSocketServer server = new ChatWebSocketServer(port);
        server.start();
        System.out.println("WebSocket server listening on port: " + port);
    }
}
