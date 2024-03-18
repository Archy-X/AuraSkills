package dev.aurelium.auraskills.common.config;

import java.util.ArrayList;
import java.util.List;

public record OptionValue(Object value) {

    public int asInt() {
        return (int) value;
    }

    public double asDouble() {
        if (!(value instanceof Integer)) {
            return (double) value;
        } else {
            return ((Integer) value).doubleValue();
        }
    }

    public boolean asBoolean() {
        return (boolean) value;
    }

    public String asString() {
        if (value instanceof String) {
            return (String) value;
        } else {
            return String.valueOf(value);
        }
    }

    public List<String> asList() {
        List<String> stringList = new ArrayList<>();
        if (value instanceof List<?>) {
            for (Object obj : (List<?>) value) {
                if (obj instanceof String) {
                    stringList.add((String) obj);
                }
            }
        }
        return stringList;
    }

}
