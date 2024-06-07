package dev.aurelium.auraskills.api;

import org.jetbrains.annotations.ApiStatus.Internal;

public final class AuraSkillsProvider {

    private static AuraSkillsApi instance = null;

    /**
     * Gets the instance of {@link AuraSkillsApi} containing API classes and methods.
     *
     * @return the API instance
     */
    public static AuraSkillsApi getInstance() {
        AuraSkillsApi instance = AuraSkillsProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("AuraSkillsApi is not initialized");
        }
        return instance;
    }

    @Internal
    static void register(AuraSkillsApi instance) {
        AuraSkillsProvider.instance = instance;
    }

    @Internal
    static void unregister() {
        AuraSkillsProvider.instance = null;
    }

    @Internal
    private AuraSkillsProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
