package com.archyx.aureliumskills.requirement;

import com.archyx.aureliumskills.modifier.ModifierType;
import com.archyx.aureliumskills.skills.Skill;
import com.cryptomorin.xseries.XMaterial;

import java.util.Map;

public class GlobalRequirement {

    private final ModifierType type;
    private final XMaterial material;
    private final Map<Skill, Integer> requirements;

    public GlobalRequirement(ModifierType type, XMaterial material, Map<Skill, Integer> requirements) {
        this.type = type;
        this.material = material;
        this.requirements = requirements;
    }

    public ModifierType getType() {
        return type;
    }

    public XMaterial getMaterial() {
        return material;
    }

    public Map<Skill, Integer> getRequirements() {
        return requirements;
    }

}
