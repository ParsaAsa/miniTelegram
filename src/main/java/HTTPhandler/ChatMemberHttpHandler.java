package HTTPhandler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ChatMemberDao;
import entity.*;

import org.hibernate.Session;
import util.HibernateUtil;
import util.JwtUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class ChatMemberHttpHandler implements HttpHandler {

    private final ChatMemberDao chatMemberDao = new ChatMemberDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String token = exchange.getRequestHeaders().getFirst("Authorization");

            if (token == null || token.isEmpty()) {
                exchange.sendResponseHeaders(401, -1); // Unauthorized
                return;
            }

            String username = JwtUtil.validateToken(token);
            if (username == null) {
                exchange.sendResponseHeaders(401, -1); // Unauthorized
                return;
            }

            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            Long userId = json.get("userId").getAsLong();
            Long chatId = json.get("chatId").getAsLong();
            String roleStr = json.get("role").getAsString();

            ChatRole role = ChatRole.valueOf(roleStr.toUpperCase());

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                User user = session.get(User.class, userId);
                Chat chat = session.get(Chat.class, chatId);

                if (user == null || chat == null) {
                    String response = "Invalid user or chat ID";
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                ChatMember chatMember = new ChatMember(user, chat, new Timestamp(System.currentTimeMillis()), role);
                if (chatMemberDao.chatMemberExists(chatMember.getChat().getId(), chatMember.getUser().getId())) {
                    String response = "Chat member already exists.";
                    exchange.sendResponseHeaders(409, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }
                chatMemberDao.save(chatMember);

                String response = "ChatMember created successfully!";
                exchange.sendResponseHeaders(201, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                String response = "Failed to create ChatMember";
                exchange.sendResponseHeaders(500, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
