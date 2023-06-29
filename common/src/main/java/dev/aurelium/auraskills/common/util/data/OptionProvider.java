package dev.aurelium.auraskills.common.util.data;

import java.util.ArrayList;
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

    public int getInt(String key) {
        return (int) optionMap.get(key);
    }

    public double getDouble(String key) {
        return (double) optionMap.get(key);
    }

    public String getString(String key) {
        return (String) optionMap.get(key);
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

}
