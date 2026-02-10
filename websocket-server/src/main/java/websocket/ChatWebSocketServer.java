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

public class ChatWebSocketServer extends WebSocketServer {
    private final Map<WebSocket, String> roomMapping;

    public ChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        this.roomMapping = new ConcurrentHashMap<>();
    }

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

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String roomId = roomMapping.remove(conn);;
        System.out.println("Disconnected from room: " + roomId);
    }

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

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Websocket error" + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Websocket server started successfully on port " + getPort());
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

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
