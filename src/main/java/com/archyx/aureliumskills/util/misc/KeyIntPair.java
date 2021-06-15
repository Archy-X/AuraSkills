package com.archyx.aureliumskills.util.misc;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyIntPair that = (KeyIntPair) o;
        return value == that.value && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
