package dev.aurelium.skills.common.hooks;

import java.util.HashMap;
import java.util.Map;

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
        return this.hooks.containsKey(type);
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
            throw new IllegalArgumentException("No registered hook of type " + type.getName() + "!");
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
