package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class HookRegistrar {

    private final AuraSkillsPlugin plugin;
    private final HookManager manager;

    public HookRegistrar(AuraSkillsPlugin plugin, HookManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public abstract void registerEvents(Hook hook);

    public abstract boolean isPluginEnabled(String name);

    public void registerHooks(ConfigurationNode config, HookType[] hooks) {
        for (HookType hookType : hooks) {
            // Don't re-register hooks
            if (manager.isRegistered(hookType.getHookClass())) continue;
            // Make sure the plugin that hooks into is enabled
            if (!isPluginEnabled(hookType.getPluginName())) {
                continue;
            }

            try {
                ConfigurationNode hookConfig = config.node(hookType.getPluginName());
                // Ignore if not enabled
                if (!hookConfig.node("enabled").getBoolean(false)) {
                    continue;
                }
                Hook hook = createHook(hookType, hookConfig);
                registerEvents(hook);

                manager.registerHook(hook.getClass(), hook);

                plugin.logger().info("Successfully registered hook " + hookType.getPluginName());
            } catch (HookRegistrationException e) {
                plugin.logger().warn("Failed to register hook " + hookType.getPluginName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    protected Hook createHook(HookType type, ConfigurationNode config) {
        Class<? extends Hook> hookClass = type.getHookClass();

        Constructor<?>[] constructors = hookClass.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new HookRegistrationException("Hook does not have a declared constructor");
        }
        Constructor<?> constructor = constructors[0];
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
