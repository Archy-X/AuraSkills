package dev.aurelium.skills.common.registry;

import dev.aurelium.skills.api.util.NamespacedId;

public class Namespace {

    private static final String AURELIUM_SKILLS_NAMESPACE = "aureliumskills";

    public static NamespacedId withKey(String key) {
        return NamespacedId.from(AURELIUM_SKILLS_NAMESPACE, key);
    }

}
