package dev.aurelium.auraskills.common.hooks;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages hooks into external plugins.
 */
public class HookManager {

    private final Map<Class<? extends Hook>, Hook> hooks;

    public HookManager() {
        this.hooks = new HashMap<>();
    }

    /**
     * Checks if a hook is registered
     *
     * @param type The hook type
     * @return True if the hook is registered, false if not
     */
    public boolean isRegistered(Class<? extends Hook> type) {
        if (this.hooks.containsKey(type)) {
            return true;
        } else {
            // Try to find hook class from super class
            for (Class<? extends Hook> clazz : hooks.keySet()) {
                if (type.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Gets a hook
     *
     * @param type The hook type
     * @return The hook
     */
    public <T extends Hook> T getHook(Class<T> type) {
        Hook hook = this.hooks.get(type);
        if (hook == null) {
            // Try to find hook class from super class
            for (Class<? extends Hook> clazz : hooks.keySet()) {
                if (type.isAssignableFrom(clazz)) {
                    hook = hooks.get(clazz);
                }
            }
            if (hook == null) {
                throw new IllegalArgumentException("No registered hook of type " + type.getName() + "!");
            }
        }
        return type.cast(hook);
    }

    /**
     * Registers a hook
     *
     * @param type The hook type
     * @param hook The hook
     */
    public void registerHook(Class<? extends Hook> type, Hook hook) {
        this.hooks.put(type, hook);
    }

}
