package dev.aurelium.auraskills.test.env;

import dev.aurelium.auraskills.common.message.PlatformLogger;

public class TestPlatformLogger implements PlatformLogger {

    @Override
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void warn(String message) {
        System.out.println("[WARN] " + message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        System.out.println("[WARN] " + message);
        throwable.printStackTrace();
    }

    @Override
    public void severe(String message) {
        System.out.println("[SEVERE] " + message);
    }

    @Override
    public void severe(String message, Throwable throwable) {
        System.out.println("[SEVERE] " + message);
        throwable.printStackTrace();
    }

    @Override
    public void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }
}
