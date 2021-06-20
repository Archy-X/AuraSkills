package com.archyx.aureliumskills.lang;

import java.util.Objects;

public class CustomMessageKey implements MessageKey {

    private final String path;

    public CustomMessageKey(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }

}
