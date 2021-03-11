package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;

public class AbilityReward extends Reward {

    private final Ability ability;

    public AbilityReward(AureliumSkills plugin, Ability ability) {
        super(plugin);
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }

    @Override
    public void giveReward(Player player, Skill skill, int level) {
        // Nothing needs to be done because ability levels are calculated not stored
    }
}
