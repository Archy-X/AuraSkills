package com.archyx.aureliumskills.skills.enchanting;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;

public enum EnchantingSource implements Source {

    WEAPON_PER_LEVEL,
    ARMOR_PER_LEVEL,
    TOOL_PER_LEVEL,
    BOOK_PER_LEVEL;

    @Override
    public Skill getSkill() {
        return Skills.ENCHANTING;
    }
}
