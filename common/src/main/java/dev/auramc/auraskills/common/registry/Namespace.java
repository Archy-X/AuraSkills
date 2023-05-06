package dev.auramc.auraskills.common.registry;

import dev.auramc.auraskills.api.util.NamespacedId;

public class Namespace {

    private static final String AURELIUM_SKILLS_NAMESPACE = "aureliumskills";

    public static NamespacedId withKey(String key) {
        return NamespacedId.from(AURELIUM_SKILLS_NAMESPACE, key);
    }

}
