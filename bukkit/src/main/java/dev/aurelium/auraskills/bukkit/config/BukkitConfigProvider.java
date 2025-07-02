package dev.aurelium.auraskills.bukkit.config;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.BukkitHookRegistrar;
import dev.aurelium.auraskills.bukkit.hooks.Hooks;
import dev.aurelium.auraskills.common.config.ConfigProvider;
import dev.aurelium.auraskills.common.config.Option;
import org.bukkit.ChatColor;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Locale;
import java.util.Map;

public class BukkitConfigProvider extends ConfigProvider {

    private final AuraSkills plugin;

    public BukkitConfigProvider(AuraSkills plugin, Map<Option, Object> overrides) {
        super(plugin, overrides);
        this.plugin = plugin;
    }

    @Override
    public void registerHooks(ConfigurationNode config) {
        BukkitHookRegistrar hookRegistrar = new BukkitHookRegistrar(plugin, plugin.getHookManager());
        hookRegistrar.registerHooks(config.node("hooks"), Hooks.values());
    }

    @Override
    public Object parseColor(String value) {
        return ChatColor.valueOf(value.toUpperCase(Locale.ROOT));
    }

}
