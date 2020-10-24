package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.entity.Player;

public class SorceryLeveler {

    public static void level(Player player, int manaUsed) {
        Leveler.addXp(player, Skill.SORCERY, manaUsed * OptionL.getXp(Source.MANA_ABILITY_USE));
    }

}
