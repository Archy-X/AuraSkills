package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.hooks.mythicmobs.MythicMobsHook;
import dev.aurelium.auraskills.common.hooks.Hook;

public enum Hooks {

    DECENT_HOLOGRAMS(DecentHologramsHook.class, "DecentHolograms"),
    HOLOGRAPHIC_DISPLAYS(HolographicDisplaysHook.class, "HolographicDisplays"),
    LUCK_PERMS(BukkitLuckPermsHook.class, "LuckPerms"),
    PLACEHOLDER_API(PlaceholderApiHook.class, "PlaceholderAPI"),
    PROTOCOL_LIB(ProtocolLibHook.class, "ProtocolLib"),
    SLIMEFUN(SlimefunHook.class, "Slimefun"),
    TOWNY(TownyHook.class, "Towny"),
    VAULT(VaultHook.class, "Vault"),
    WORLD_GUARD(WorldGuardHook.class, "WorldGuard"),
    MYTHIC_MOBS(MythicMobsHook.class, "MythicMobs"),
    ORAXEN(OraxenHook.class, "Oraxen");

    private final Class<? extends Hook> hookClass;
    private final String pluginName;

    Hooks(Class<? extends Hook> hookClass, String pluginName) {
        this.hookClass = hookClass;
        this.pluginName = pluginName;
    }

    public Class<? extends Hook> getHookClass() {
        return hookClass;
    }

    public String getPluginName() {
        return pluginName;
    }

}
