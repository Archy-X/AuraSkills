package dev.aurelium.auraskills.bukkit.logging;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.PlatformLogger;

public class BukkitLogger implements PlatformLogger {

    private final AuraSkills plugin;

    public BukkitLogger(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public void info(String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void warn(String message) {
        plugin.getLogger().warning(message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        plugin.getLogger().warning(message);
        throwable.printStackTrace();
    }

    @Override
    public void severe(String message) {
        plugin.getLogger().severe(message);
    }

    @Override
    public void severe(String message, Throwable throwable) {
        plugin.getLogger().severe(message);
        throwable.printStackTrace();
    }

    @Override
    public void debug(String message) {
        plugin.getLogger().info("[DEBUG] " + message);
    }
}
