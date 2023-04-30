package dev.aurelium.skills.common.hooks;

import java.util.HashMap;
import java.util.Map;

public class HookManager {

    private final Map<Hooks, Hook> hooks;

    public HookManager() {
        this.hooks = new HashMap<>();
    }

    /**
     * Checks if a hook is registered
     *
     * @param type The hook type
     * @return True if the hook is registered, false if not
     */
    public boolean isRegistered(Hooks type) {
        return this.hooks.containsKey(type);
    }

    /**
     * Gets a hook
     *
     * @param type The hook type
     * @return The hook
     */
    public Hook getHook(Hooks type) {
        Hook hook = this.hooks.get(type);
        if (hook == null) {
            throw new IllegalArgumentException("No registered hook of type " + type.name() + "!");
        }
        return hook;
    }

    /**
     * Registers a hook
     *
     * @param type The hook type
     * @param hook The hook
     */
    public void registerHook(Hooks type, Hook hook) {
        this.hooks.put(type, hook);
    }

}
