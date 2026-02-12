import com.sun.net.httpserver.HttpServer;
import http.HttpServerManager;
import websocket.ChatWebSocketServer;

/**
 * Entry point for the Chat Server application.
 *
 * <p>This class starts two servers:</p>
 * <ul>
 *   <li>A WebSocket server for real-time chat communication.</li>
 *   <li>An HTTP server for health checks and auxiliary endpoints.</li>
 * </ul>
 *
 * <p>After startup, the servers are available at:</p>
 * <ul>
 *   <li>WebSocket: ws://localhost:8080/chat/{roomId}</li>
 *   <li>Health Check: http://localhost:8081/health</li>
 * </ul>
 *
 * <p>The application blocks on the HTTP server thread to keep the program running.</p>
 */
public class ChatServerMain {

    /**
     * Port number used by the WebSocket chat server.
     */
    private static final int WEBSOCKET_PORT = 8080;

    /**
     * Port number used by the HTTP health server.
     */
    private static final int HTTP_PORT = 8081;

    /**
     * Main entry point of the Chat Server.
     *
     * <p>Initializes and starts the WebSocket server and HTTP server.
     * The WebSocket server handles chat communication, while the HTTP
     * server provides health and management endpoints.</p>
     *
     * @param args command-line arguments (not currently used)
     */
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
