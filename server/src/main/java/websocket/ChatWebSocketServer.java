package websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.ChatMessage;
import model.ChatResponse;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import util.JsonUtil;
import validator.MessageValidator;
import validator.ValidationResult;

/**
 * WebSocket server responsible for handling real-time chat communication.
 *
 * <p>Clients connect using a URI pattern: <code>/chat/{roomId}</code>.
 * Each connection is mapped to a chat room, and messages are validated,
 * processed, and responded to in JSON format.</p>
 *
 * <p>This server supports:</p>
 * <ul>
 *   <li>Room-based connections</li>
 *   <li>JSON message parsing</li>
 *   <li>Message validation</li>
 *   <li>Structured success/error responses</li>
 * </ul>
 */
public class ChatWebSocketServer extends WebSocketServer {

    /**
     * Maps each WebSocket connection to its associated chat room ID.
     */
    private final Map<WebSocket, String> roomMapping;

    /**
     * Creates a new ChatWebSocketServer bound to the given port.
     *
     * @param port the port number the WebSocket server listens on
     */
    public ChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        this.roomMapping = new ConcurrentHashMap<>();
    }

    /**
     * Called when a new WebSocket connection is opened.
     *
     * <p>Extracts the room ID from the request URI and associates
     * the connection with that room. Invalid paths are rejected.</p>
     *
     * @param conn      the WebSocket connection
     * @param handshake the opening handshake from the client
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String uri = handshake.getResourceDescriptor();
        String roomId = extractRoomId(uri);

        if (roomId != null) {
            roomMapping.put(conn, roomId);
            System.out.println("New connection to room: " + roomId);
        } else {
            System.out.println("Invalid connection from: " + conn.getRemoteSocketAddress());
            conn.close(1003, "Invalid room path");
        }
    }

    /**
     * Called when a WebSocket connection is closed.
     *
     * <p>Removes the connection from the room mapping.</p>
     *
     * @param conn   the WebSocket connection
     * @param code   the closure code
     * @param reason the reason for closing
     * @param remote whether the closure was initiated remotely
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String roomId = roomMapping.remove(conn);;
        System.out.println("Disconnected from room: " + roomId);
    }

    /**
     * Called when a message is received from a client.
     *
     * <p>The message is parsed from JSON into a {@link ChatMessage},
     * validated, and wrapped in a {@link ChatResponse}. Errors are
     * returned in structured JSON format.</p>
     *
     * @param conn    the WebSocket connection
     * @param message the raw JSON message sent by the client
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        try {
            ChatMessage chatMessage = JsonUtil.fromJson(message, ChatMessage.class);

            ValidationResult validationResult = MessageValidator.validate(chatMessage);

            if (validationResult.isValid()) {
                ChatResponse response = new ChatResponse("SUCCESS", chatMessage);
                String responseJson = JsonUtil.toJson(response);
                conn.send(responseJson);
            } else {
                ChatResponse response = new ChatResponse("ERROR", validationResult.getMessage());
                String responseJson = JsonUtil.toJson(response);
                conn.send(responseJson);
            }
        } catch (JsonProcessingException e) {
            ChatResponse response = new ChatResponse("ERROR", "Invalid JSON format" +  e.getMessage());
            String responseJson = null;
            try {
                responseJson = JsonUtil.toJson(response);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            conn.send(responseJson);
        } catch (Exception e) {
            ChatResponse response = new ChatResponse("ERROR", "Server error" + e.getMessage());
            String responseJson = null;
            try {
                responseJson = JsonUtil.toJson(response);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            conn.send(responseJson);
        }
    }

    /**
     * Called when an error occurs on the WebSocket connection.
     *
     * @param conn the WebSocket connection where the error occurred
     * @param ex   the exception thrown
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Websocket error" + ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * Called once the WebSocket server has successfully started.
     *
     * <p>Configures connection timeout behavior and logs startup status.</p>
     */
    @Override
    public void onStart() {
        System.out.println("Websocket server started successfully on port " + getPort());
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    /**
     * Extracts the room ID from the WebSocket request URI.
     *
     * <p>Expected format: <code>/chat/{roomId}</code>.</p>
     *
     * @param uri the request URI from the client handshake
     * @return the extracted room ID, or {@code null} if invalid
     */
    private String extractRoomId(String uri) {
        if (uri != null && uri.startsWith("/chat/")) {
            String [] parts = uri.split("/");
            if (parts.length >= 3) {
                return parts[2];
            }
        }
        return null;
    }
}
