package dev.aurelium.skills.common.message;

import java.util.Objects;

public class MessageKey {

    private final String path;

    public MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageKey that = (MessageKey) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public static MessageKey of(String path) {
        return new MessageKey(path);
    }

}
