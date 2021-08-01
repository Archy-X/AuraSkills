package com.archyx.aureliumskills.skills.sorcery;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.entity.Player;

public class SorceryLeveler extends SkillLeveler {

    public SorceryLeveler(AureliumSkills plugin) {
        super(plugin, Skills.SORCERY);
    }

    public void level(Player player, double manaUsed) {
        plugin.getLeveler().addXp(player, Skills.SORCERY, manaUsed * getXp(player, SorcerySource.MANA_ABILITY_USE));
    }

}
