package com.archyx.aureliumskills.configuration;

import org.bukkit.Bukkit;

public class ASLogger {

    public static void logWarn(LogType type, String message) {
        Bukkit.getLogger().warning("[AureliumSkills] " + type.name() + " " + message);
    }

    public static void logError(LogType type, String message) {
        Bukkit.getLogger().severe("[AureliumSkills] " + type.name() + " " + message);
    }

}
