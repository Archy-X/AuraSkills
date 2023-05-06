package dev.auramc.auraskills.common.message;

public interface MessageKey {

    String getPath();

    static MessageKey of(String path) {
        return () -> path;
    }

}
