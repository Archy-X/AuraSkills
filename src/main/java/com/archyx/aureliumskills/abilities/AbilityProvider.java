package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class AbilityProvider {

    public final AureliumSkills plugin;
    private final String skillName;

    public AbilityProvider(AureliumSkills plugin, Skill skill) {
        this.plugin = plugin;
        this.skillName = skill.toString().toLowerCase(Locale.ENGLISH);
    }

    public boolean blockAbility(Player player) {
        if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
            return true;
        }
        if (!player.hasPermission("aureliumskills." + skillName)) {
            return true;
        }
        if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
            return player.getGameMode().equals(GameMode.CREATIVE);
        }
        return false;
    }

    public boolean blockDisabled(Ability ability) {
        if (!OptionL.isEnabled(ability.getSkill())) {
            return true;
        }
        return !AureliumSkills.abilityManager.isEnabled(ability);
    }

    public boolean isEnabled(Ability ability) {
        return AureliumSkills.abilityManager.isEnabled(ability);
    }

}
