package dev.aurelium.auraskills.common.message;

public interface PlatformLogger {

    void info(String message);

    void warn(String message);

    void warn(String message, Throwable throwable);

    void severe(String message);

    void severe(String message, Throwable throwable);

    void debug(String message);

}
