import com.sun.net.httpserver.HttpServer;
import http.HttpServerManager;
import websocket.ChatWebSocketServer;

public class ChatServerMain {
    private static final int WEBSOCKET_PORT = 8080;
    private static final int HTTP_PORT = 8081;

    public static void main(String[] args) {
        try {
            ChatWebSocketServer webSocketServer = new ChatWebSocketServer(WEBSOCKET_PORT);
            webSocketServer.start();
            System.out.println("Websocket server started at port " + WEBSOCKET_PORT);

            HttpServerManager httpServer = new HttpServerManager(HTTP_PORT);
            httpServer.start();

            System.out.println("Chat server is running.");
            System.out.println("WebSocket: ws://localhost:" + WEBSOCKET_PORT + "/chat/{roomId");
            System.out.println("Health: http://localhost:" + HTTP_PORT + "/health");

            httpServer.join();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
