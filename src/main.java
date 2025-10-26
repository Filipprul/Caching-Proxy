
public class main {

    public static void main(String[] args) throws Exception {
        int port = 3000;
        String origin = null;
        boolean clearCache = false;

        //CLI Parameter
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--port" ->
                    port = Integer.parseInt(args[++i]);
                case "--origin" ->
                    origin = args[++i];
                case "--clear-cache" ->
                    clearCache = true;
            }
        }

        if (clearCache) {
            CacheManager.clear();
            System.out.println("Cache cleared.");
            return;
        }

        if (origin == null) {
            System.out.println("Error: --origin <URL> missing.");
            return;
        }

        ProxyServer server = new ProxyServer(port, origin);
        server.start();
    }
}
