package dev.aurelium.auraskills.common.util.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionProvider {

    private final Map<String, Object> optionMap;

    public OptionProvider(Map<String, Object> optionMap) {
        this.optionMap = optionMap;
    }

    public Object getOption(String key) {
        return optionMap.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) optionMap.get(key);
    }

    public boolean getBoolean(String key, boolean def) {
        return (boolean) optionMap.getOrDefault(key, def);
    }

    public int getInt(String key) {
        return (int) optionMap.get(key);
    }

    public int getInt(String key, int def) {
        return (int) optionMap.getOrDefault(key, def);
    }

    public double getDouble(String key) {
        Object o = optionMap.get(key);
        if (o instanceof Integer i) {
            return i.doubleValue();
        }
        return (double) o;
    }

    public double getDouble(String key, double def) {
        Object o = optionMap.getOrDefault(key, def);
        if (o instanceof Integer i) {
            return i.doubleValue();
        }
        return (double) o;
    }

    public String getString(String key) {
        return (String) optionMap.get(key);
    }

    public String getString(String key, String def) {
        return (String) optionMap.getOrDefault(key, def);
    }

    public List<String> getStringList(String key) {
        List<String> list = new ArrayList<>();
        Object o = optionMap.get(key);
        if (o instanceof List<?>) {
            for (Object object : (List<?>) o) {
                if (object instanceof String) {
                    list.add((String) object);
                }
            }
        }
        return list;
    }

    public Map<String, Object> getMap(String key) {
        Map<String, Object> map = new HashMap<>();
        Object o = optionMap.get(key);
        if (o instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
                map.put((String) entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

}
