package http;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check servlet for the chat application.
 *
 * <p>Provides a simple HTTP endpoint to verify that the service is running.
 * A GET request returns a JSON object containing the service status, timestamp,
 * and service name.</p>
 *
 * <p>Example response:</p>
 * <pre>
 * {
 *   "status": "UP",
 *   "timestamp": "2026-02-11T12:00:00Z",
 *   "service": "Websocket chat server"
 * }
 * </pre>
 */
public class HealthServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for the health endpoint.
     *
     * <p>Responds with HTTP 200 and a JSON payload indicating the service health.</p>
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws IOException if writing the response fails
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.Instant.now().toString());
        health.put("service", "Websocket chat server");

        resp.getWriter().write(JsonUtil.toJson(health));
    }
}
