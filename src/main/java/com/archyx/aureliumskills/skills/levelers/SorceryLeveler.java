package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.sources.SorcerySource;
import org.bukkit.entity.Player;

public class SorceryLeveler extends SkillLeveler {

    public SorceryLeveler(AureliumSkills plugin) {
        super(plugin, Skills.SORCERY);
    }

    public void level(Player player, double manaUsed) {
        plugin.getLeveler().addXp(player, Skills.SORCERY, manaUsed * getXp(SorcerySource.MANA_ABILITY_USE));
    }

}
