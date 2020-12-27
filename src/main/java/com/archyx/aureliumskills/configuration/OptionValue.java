package com.archyx.aureliumskills.configuration;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class OptionValue {

    private Object value;

    public OptionValue(Object value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public int asInt() {
        return (int) value;
    }

    public double asDouble() {
        if (!(value instanceof Integer)) {
            return (double) value;
        }
        else {
            return ((Integer) value).doubleValue();
        }
    }

    public boolean asBoolean() {
        return (boolean) value;
    }

    public String asString() {
        if (value instanceof String) {
            return (String) value;
        }
        else {
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

    public ChatColor asColor() {
        if (value instanceof ChatColor) {
            return (ChatColor) value;
        }
        else {
            return ChatColor.valueOf(asString());
        }
    }
}
