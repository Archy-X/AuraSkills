package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;

import java.util.ArrayList;
import java.util.List;

public class LootRequirements {

    private final List<ConfigNode> requirements;

    public LootRequirements(List<ConfigNode> requirements) {
        this.requirements = requirements;
    }

    public List<ConfigNode> getRequirements() {
        return requirements;
    }

    public static LootRequirements parse(ConfigNode config) {
        List<ConfigNode> requirements = new ArrayList<>();

        if (config.hasChild("requirements") && config.node("requirements").isList()) {
            requirements = new ArrayList<>(config.node("requirements").childrenList());
        }

        return new LootRequirements(requirements);
    }

}
