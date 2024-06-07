package dev.aurelium.auraskills.api;

import org.jetbrains.annotations.ApiStatus;

public final class AuraSkillsBukkitProvider {

    private static AuraSkillsBukkit instance = null;

    /**
     * Gets the instance of {@link AuraSkillsApi} containing API classes and methods.
     *
     * @return the API instance
     */
    public static AuraSkillsBukkit getInstance() {
        AuraSkillsBukkit instance = AuraSkillsBukkitProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("AuraSkillsBukkit is not initialized");
        }
        return instance;
    }

    @ApiStatus.Internal
    static void register(AuraSkillsBukkit instance) {
        AuraSkillsBukkitProvider.instance = instance;
    }

    @ApiStatus.Internal
    static void unregister() {
        AuraSkillsBukkitProvider.instance = null;
    }

    @ApiStatus.Internal
    private AuraSkillsBukkitProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }


}
