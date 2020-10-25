package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class AlchemyAbilities implements Listener {

    public static double getModifiedXp(Player player, Source source) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = OptionL.getXp(source);
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.BREWER)) {
            double modifier = 1;
            modifier += Ability.JUMPER.getValue(skill.getAbilityLevel(Ability.BREWER)) / 100;
            output *= modifier;
        }
        return output;
    }


}
