package dev.aurelium.auraskills.bukkit.antiafk;

import java.util.HashMap;
import java.util.Map;

public class CheckData {

    private final Map<String, Object> cache = new HashMap<>();

    public <T> T getCache(String key, Class<T> type, T def) {
        Object val = cache.get(key);
        if (val != null) {
            return type.cast(val);
        } else {
            return def;
        }
    }

    public void setCache(String key, Object value) {
        cache.put(key, value);
    }

    public int getCount() {
        return getCache("count", Integer.class, 0);
    }

    public void incrementCount() {
        setCache("count", getCache("count", Integer.class, 0) + 1);
        setCache("log_count", getCache("log_count", Integer.class, 0) + 1);
    }

    public void resetCount() {
        setCache("count", 0);
        setCache("log_count", 0);
    }

    public int getLogCount() {
        return getCache("log_count", Integer.class, 0);
    }

    public void resetLogCount() {
        setCache("log_count", 0);
    }

}
