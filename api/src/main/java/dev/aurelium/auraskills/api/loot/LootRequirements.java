package dev.aurelium.auraskills.api.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;

import java.util.List;

public abstract class LootRequirements {
    protected final List<ConfigNode> requirements;

    public LootRequirements(List<ConfigNode> requirements) {
        this.requirements = requirements;
    }

    public List<ConfigNode> getRequirements() {
        return requirements;
    }

    public Boolean hasRequirements() {
        if (requirements == null || requirements.isEmpty()) {
            return false;
        }

        return true;
    }

}
