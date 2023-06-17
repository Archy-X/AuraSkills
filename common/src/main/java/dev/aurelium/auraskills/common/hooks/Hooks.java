package dev.aurelium.auraskills.common.hooks;

public enum Hooks {

    PERMISSIONS(PermissionsHook.class),
    PLACEHOLDER(PlaceholderHook.class);

    private final Class<?> hookClass;

    Hooks(Class<?> hookClass) {
        this.hookClass = hookClass;
    }

    public Class<?> getHookClass() {
        return hookClass;
    }

}
