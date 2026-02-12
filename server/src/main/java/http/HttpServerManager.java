package http;

import jakarta.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Manages the embedded HTTP server for the chat application.
 *
 * <p>This class configures and starts a Jetty server used for exposing
 * HTTP endpoints such as health checks. It initializes the servlet context,
 * registers servlets, and controls the server lifecycle.</p>
 *
 * <p>Currently supported endpoints:</p>
 * <ul>
 *   <li><code>/health</code> - Health check endpoint</li>
 * </ul>
 */
public class HttpServerManager {

    /**
     * Embedded Jetty server instance.
     */
    private final Server server;

    /**
     * Creates and configures an HTTP server on the given port.
     *
     * <p>Initializes the Jetty server, sets the root context path,
     * and registers available servlets.</p>
     *
     * @param port the port number the HTTP server listens on
     */
    public HttpServerManager(int port) {
        this.server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new HealthServlet()), "/health");
    }

    /**
     * Starts the HTTP server.
     *
     * @throws Exception if the server fails to start
     */
    public void start() throws Exception {
        server.start();
        System.out.println("Http server started on port " +
                server.getURI().getPort());
    }

    /**
     * Stops the HTTP server gracefully.
     *
     * @throws Exception if the server fails to stop
     */
    public void stop() throws Exception {
        server.stop();
    }

    /**
     * Blocks the current thread until the server is stopped.
     *
     * <p>This keeps the application alive while the HTTP server is running.</p>
     *
     * @throws InterruptedException if the thread is interrupted
     */
    public void join() throws InterruptedException {
        server.join();
    }
}
