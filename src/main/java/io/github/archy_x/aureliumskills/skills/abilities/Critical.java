package io.github.archy_x.aureliumskills.skills.abilities;

import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import org.bukkit.entity.Player;

import java.util.Random;

public class Critical {

    private static Random r = new Random();

    public static boolean isCrit(Player player) {
        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
            PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (r.nextDouble() < (Ability.CRIT_CHANCE.getValue(skill.getAbilityLevel(Ability.CRIT_CHANCE)) / 100)) {
                return true;
            }
        }
        return false;
    }

    public static double getCritMultiplier(Player player) {
        return 2.0;
    }

}
