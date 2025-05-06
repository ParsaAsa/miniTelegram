package websocket;

import entity.Message;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat")
public class ChatEndpoint {

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String messageJson, Session senderSession) {
        try {
            Message message = gson.fromJson(messageJson, Message.class);

            // Optionally save to DB (via DAO)
            // new MessageDao().saveMessage(message);

            // Broadcast to all sessions
            String json = gson.toJson(message);
            for (Session session : sessions) {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(json);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error on session " + session.getId());
        throwable.printStackTrace();
    }
}
