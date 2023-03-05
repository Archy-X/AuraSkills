package dev.aurelium.skills.api;

import org.jetbrains.annotations.ApiStatus.Internal;

public final class AureliumSkillsProvider {

    private static AureliumSkillsAPI instance = null;

    public static AureliumSkillsAPI getInstance() {
        AureliumSkillsAPI instance = AureliumSkillsProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("AureliumSkillsAPI is not initialized");
        }
        return instance;
    }

    @Internal
    static void register(AureliumSkillsAPI instance) {
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
