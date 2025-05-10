package HTTPhandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ChatDao;
import entity.Chat;
import util.JwtUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class ChatHttpHandler implements HttpHandler {

    private final ChatDao chatDao = new ChatDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
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

            // Read the request body
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            Chat chat = new Gson().fromJson(reader, Chat.class);

            // Generate a timestamp for the chat creation
            chat.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Save the chat to the database
            try {
                chatDao.saveChat(chat);

                // Respond with success
                String response = "Chat created successfully!";
                exchange.sendResponseHeaders(201, response.getBytes().length); // 201 Created
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                String response = "Failed to create chat";
                exchange.sendResponseHeaders(500, response.getBytes().length); // 500 Internal Server Error
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } else {
            // If not POST, return 405 Method Not Allowed
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}
