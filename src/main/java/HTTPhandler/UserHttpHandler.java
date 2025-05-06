package HTTPhandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UserHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Read the request body
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            User user = new Gson().fromJson(reader, User.class);

            // Save the user to the database using Hibernate
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();

                // Save the user
                session.save(user);

                // Commit the transaction
                transaction.commit();

                // Respond with success
                String response = "User created successfully!";
                exchange.sendResponseHeaders(201, response.getBytes().length); // 201 Created
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                String response = "Failed to create user";
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
