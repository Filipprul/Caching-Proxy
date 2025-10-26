
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class CacheManager {

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static String get(String key) {
        return cache.get(key);
    }

    public static void put(String key, String value) {
        cache.put(key, value);
    }

    public static void clear() {
        cache.clear();
    }

    public static boolean contains(String key) {
        return cache.containsKey(key);
    }
}
