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

import util.HibernateUtil;

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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Deserialize the raw JSON
            Message raw = new Gson().fromJson(messageJson, Message.class);

            // Resolve IDs to real entities
            Message message = new Message();
            message.setContent(raw.getContent());
            message.setSentAt(new Timestamp(System.currentTimeMillis()));
            message.setSender(session.get(User.class, raw.getSender().getId()));
            message.setChat(session.get(Chat.class, raw.getChat().getId()));
            if (raw.getReplyTo() != null && raw.getReplyTo().getId() != null) {
                message.setReplyTo(session.get(Message.class, raw.getReplyTo().getId()));
            }

            session.persist(message);
            tx.commit();

            // Broadcast to other clients
            broadcast(new Gson().toJson(message));
        } catch (Exception e) {
            e.printStackTrace();
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
