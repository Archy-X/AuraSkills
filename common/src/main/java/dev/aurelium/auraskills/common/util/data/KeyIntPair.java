package dev.aurelium.auraskills.common.util.data;

public class KeyIntPair {

    private final String key;
    private int value;

    public KeyIntPair(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
