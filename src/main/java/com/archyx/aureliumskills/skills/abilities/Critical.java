package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.PlayerSkill;

import java.util.Random;

public class Critical {

    private static final Random r = new Random();

    public static boolean isCrit(PlayerSkill playerSkill) {
        return r.nextDouble() < (Ability.CRIT_CHANCE.getValue(playerSkill.getAbilityLevel(Ability.CRIT_CHANCE)) / 100);
    }

    public static double getCritMultiplier(PlayerSkill playerSkill) {
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.CRIT_DAMAGE)) {
            double multiplier = Ability.CRIT_DAMAGE.getValue(playerSkill.getAbilityLevel(Ability.CRIT_DAMAGE)) / 100;
            return 2.0 * (1 + multiplier);
        }
        return 2.0;
    }

}
