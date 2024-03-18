package dev.aurelium.auraskills.common.message;

public interface MessageKey {

    String getPath();

    static MessageKey of(String path) {
        return () -> path;
    }

}
