import HTTPhandler.ChatMemberHttpHandler;
import HTTPhandler.UserHttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import HTTPhandler.ChatHttpHandler;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        // Create and start the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Register your handler
        server.createContext("/user", new UserHttpHandler());

        //Register the ChatHttpHandler for /chat endpoint
        server.createContext("/chat", new ChatHttpHandler());

        //Register the ChatMemberHttpHandler for /chat endpoint
        server.createContext("/chatmember", new ChatMemberHttpHandler());

        // Start the server
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
