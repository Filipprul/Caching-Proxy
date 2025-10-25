
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


public class ProxyServer {
    private final int port;
    private final String origin;

    public ProxyServer(int port, String origin){
        this.port = port;
        this.origin = origin;
    }

    public void start()throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> handleRquest(exchange));
        server.start();
        System.err.println("Proxy läuft auf Port " + port + " -> " + origin);
    }

    private static void handleRquest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().toString();
        String response = "Proxy alive! Path: " + path;
        exchange.getResponseHeaders().add("Context-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()){
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}