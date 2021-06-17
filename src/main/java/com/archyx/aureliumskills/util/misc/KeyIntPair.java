package com.archyx.aureliumskills.util.misc;

public class KeyIntPair {

    private final String key;
    private final int value;

    public KeyIntPair(String key, int value) {
        this.key = key;
        this.value =value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

}
