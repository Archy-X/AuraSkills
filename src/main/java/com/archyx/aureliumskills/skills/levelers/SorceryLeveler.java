package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.entity.Player;

public class SorceryLeveler extends SkillLeveler {

    public SorceryLeveler(AureliumSkills plugin) {
        super(plugin);
    }

    public void level(Player player, int manaUsed) {
        Leveler.addXp(player, Skill.SORCERY, manaUsed * getXp(Source.MANA_ABILITY_USE));
    }

}
