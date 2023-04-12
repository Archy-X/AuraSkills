package dev.aurelium.skills.api;

import org.jetbrains.annotations.ApiStatus.Internal;

public final class AureliumSkillsProvider {

    private static AureliumSkillsApi instance = null;

    public static AureliumSkillsApi getInstance() {
        AureliumSkillsApi instance = AureliumSkillsProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("AureliumSkillsAPI is not initialized");
        }
        return instance;
    }

    @Internal
    static void register(AureliumSkillsApi instance) {
        AureliumSkillsProvider.instance = instance;
    }

    @Internal
    static void unregister() {
        AureliumSkillsProvider.instance = null;
    }

    @Internal
    private AureliumSkillsProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
