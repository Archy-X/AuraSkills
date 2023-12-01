package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.hooks.HookRegistrationException;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class HookRegistrar {

    private final AuraSkills plugin;
    private final HookManager manager;

    public HookRegistrar(AuraSkills plugin, HookManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void registerHooks(ConfigurationNode config) {
        for (Hooks hookType : Hooks.values()) {
            // Don't re-register hooks
            if (manager.isRegistered(hookType.getHookClass())) continue;
            // Make sure the plugin that hooks into is enabled
            if (!plugin.getServer().getPluginManager().isPluginEnabled(hookType.getPluginName())) {
                continue;
            }

            try {
                ConfigurationNode hookConfig = config.node(hookType.getPluginName());
                // Ignore if not enabled
                if (!hookConfig.node("enabled").getBoolean(false)) {
                    continue;
                }
                Hook hook = createHook(hookType, hookConfig);
                // Register events in hook
                if (hook instanceof Listener) {
                    plugin.getServer().getPluginManager().registerEvents((Listener) hook, plugin);
                }

                manager.registerHook(hook.getClass(), hook);

                plugin.logger().info("Successfully registered hook " + hookType.getPluginName());
            } catch (HookRegistrationException e) {
                plugin.logger().warn("Failed to register hook " + hookType.getPluginName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Hook createHook(Hooks type, ConfigurationNode config) {
        Class<? extends Hook> hookClass = type.getHookClass();

        Constructor<?> constructor = hookClass.getDeclaredConstructors()[0];
        if (constructor == null) {
            throw new HookRegistrationException("Hook does not have a declared constructor");
        }
        try {
            return (Hook) constructor.newInstance(plugin, config);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new HookRegistrationException("Failed to construct hook using reflection: " + e.getMessage());
        }
    }

}
