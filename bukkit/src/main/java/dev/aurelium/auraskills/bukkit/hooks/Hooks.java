package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.hooks.mythicmobs.MythicMobsHook;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.HookType;

public enum Hooks implements HookType {

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
    NEXO(NexoHook.class, "Nexo");

    private final Class<? extends Hook> hookClass;
    private final String pluginName;

    Hooks(Class<? extends Hook> hookClass, String pluginName) {
        this.hookClass = hookClass;
        this.pluginName = pluginName;
    }

    @Override
    public Class<? extends Hook> getHookClass() {
        return hookClass;
    }

    @Override
    public String getPluginName() {
        return pluginName;
    }

}
