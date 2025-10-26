
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ProxyServer {

    private final int port;
    private final String origin;

    public ProxyServer(int port, String origin) {
        this.port = port;
        this.origin = origin;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> handleRequest(exchange));
        server.start();
        System.err.println("Proxy running on port " + port + " -> " + origin);
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().toString();
        String url = origin + path;
        System.out.println("-> Forward to: " + url);

        if (CacheManager.contains(url)) {
            String cached = CacheManager.get(url);
            exchange.getResponseHeaders().add("X-Cache", "HIT");
            exchange.getResponseHeaders().add("Context-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, cached.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(cached.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int status = conn.getResponseCode();
        String responseBody;
        try (java.io.InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream()) {
            responseBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        CacheManager.put(url, responseBody);

        exchange.getResponseHeaders().add("X-Cache", "MISS");
        exchange.getResponseHeaders().add("Context-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, responseBody.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBody.getBytes(StandardCharsets.UTF_8));
        }

        conn.disconnect();
    }
}
