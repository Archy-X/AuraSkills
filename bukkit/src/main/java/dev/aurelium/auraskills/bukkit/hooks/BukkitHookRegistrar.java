package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.hooks.HookRegistrar;
import org.bukkit.event.Listener;

public class BukkitHookRegistrar extends HookRegistrar {

    private final AuraSkills plugin;

    public BukkitHookRegistrar(AuraSkills plugin, HookManager manager) {
        super(plugin, manager);
        this.plugin = plugin;
    }

    @Override
    public void registerEvents(Hook hook) {
        if (hook instanceof Listener) {
            plugin.getServer().getPluginManager().registerEvents((Listener) hook, plugin);
        }
    }

    @Override
    public boolean isPluginEnabled(String name) {
        return plugin.getServer().getPluginManager().isPluginEnabled(name);
    }

}
