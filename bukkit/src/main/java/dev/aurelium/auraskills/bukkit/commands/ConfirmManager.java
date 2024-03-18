package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.CommandIssuer;
import dev.aurelium.auraskills.bukkit.AuraSkills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ConfirmManager {

    private final AuraSkills plugin;
    private final Map<UUID, String> confirmMap = new HashMap<>();

    public ConfirmManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public boolean requiresConfirmation(CommandIssuer issuer, String arg) {
        return requiresConfirmation(issuer, arg, false);
    }

    public boolean requiresConfirmation(CommandIssuer issuer, String arg, boolean requireConsole) {
        if (!issuer.isPlayer() && !requireConsole) {
            return false;
        }
        UUID uuid = issuer.getUniqueId();
        String existing = confirmMap.get(uuid);
        // Player has already typed the command before
        if (existing != null && existing.equals(arg)) {
            return false;
        }
        confirmMap.put(uuid, arg);
        // Schedule map removal
        plugin.getScheduler().scheduleSync(() -> {
            if (arg.equals(confirmMap.getOrDefault(uuid, ""))) {
                confirmMap.remove(uuid);
            }
        }, 30, TimeUnit.SECONDS);
        return true;
    }

    public void remove(CommandIssuer issuer) {
        confirmMap.remove(issuer.getUniqueId());
    }

}
