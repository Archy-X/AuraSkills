package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;

public enum EnchantingSources implements SourceProvider {

    WEAPON_PER_LEVEL,
    ARMOR_PER_LEVEL,
    TOOL_PER_LEVEL,
    BOOK_PER_LEVEL;

    @Override
    public Skill getSkill() {
        return Skills.ENCHANTING;
    }
}
