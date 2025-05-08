package HTTPhandler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import util.JwtUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UserHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/user/signup")) {
                handleSignup(exchange);
            } else if (path.equals("/user/login")) {
                handleLogin(exchange);
            } else {
                exchange.sendResponseHeaders(404, -1); // Not Found
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    private void handleSignup(HttpExchange exchange) throws IOException {
        // Read the request body
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        User user = new Gson().fromJson(reader, User.class);

        // Save the user to the database using Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Check if the username already exists
            if (isUsernameTaken(session, user.getUsername())) {
                String response = "Username is already taken!";
                exchange.sendResponseHeaders(400, response.getBytes().length); // 400 Bad Request
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            // Save the new user
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
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        // Read the request body
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        User loginUser = new Gson().fromJson(reader, User.class);

        // Validate the user credentials
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = getUserByUsername(session, loginUser.getUsername());

            if (user == null || !user.getPassword().equals(loginUser.getPassword())) {
                String response = "Invalid username or password!";
                exchange.sendResponseHeaders(401, response.getBytes().length); // 401 Unauthorized
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            // Generate JWT token
            String token = generateJwtToken(user);

            // Respond with the JWT token
            String response = "{\"token\":\"" + token + "\"}";
            exchange.sendResponseHeaders(200, response.getBytes().length); // 200 OK
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String response = "Failed to login";
            exchange.sendResponseHeaders(500, response.getBytes().length); // 500 Internal Server Error
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private boolean isUsernameTaken(Session session, String username) {
        User user = session.createQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
        return user != null;
    }

    private User getUserByUsername(Session session, String username) {
        return session.createQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
    }

    private String generateJwtToken(User user) {
        return JwtUtil.generateToken(user.getUsername());
    }
}
