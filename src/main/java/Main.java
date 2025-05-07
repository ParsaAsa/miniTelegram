import HTTPhandler.ChatMemberHttpHandler;
import HTTPhandler.UserHttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import HTTPhandler.ChatHttpHandler;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8090; // WebSocket server port
        ChatWebSocketServer server = new ChatWebSocketServer(port);
        server.start();
        System.out.println("WebSocket server started on port " + port);
        // Create and start the HTTP server
        HttpServer server2 = HttpServer.create(new InetSocketAddress(8080), 0);

        // Register your handler
        server2.createContext("/user", new UserHttpHandler());

        //Register the ChatHttpHandler for /chat endpoint
        server2.createContext("/chat", new ChatHttpHandler());

        //Register the ChatMemberHttpHandler for /chat endpoint
        server2.createContext("/chatmember", new ChatMemberHttpHandler());

        // Start the server
        server2.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
