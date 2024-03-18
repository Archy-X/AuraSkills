package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Skill;
import org.bukkit.Material;

import java.util.Map;

public class GlobalRequirement {

    private final ModifierType type;
    private final Material material;
    private final Map<Skill, Integer> requirements;

    public GlobalRequirement(ModifierType type, Material material, Map<Skill, Integer> requirements) {
        this.type = type;
        this.material = material;
        this.requirements = requirements;
    }

    public ModifierType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public Map<Skill, Integer> getRequirements() {
        return requirements;
    }

}
