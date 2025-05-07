package HTTPhandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.MessageDao;
import entity.Message;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class MessageHttpHandler implements HttpHandler {

    private final MessageDao messageDao = new MessageDao();

    @Override
    public void handle(HttpExchange exchange) {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Message message = new Gson().fromJson(reader, Message.class);
                message.setSentAt(new Timestamp(System.currentTimeMillis()));

                messageDao.saveMessage(message);

                String response = "Message saved successfully!";
                exchange.sendResponseHeaders(201, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                String response = "Failed to save message";
                try {
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception inner) {
                    inner.printStackTrace();
                }
            }
        } else {
            try {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
