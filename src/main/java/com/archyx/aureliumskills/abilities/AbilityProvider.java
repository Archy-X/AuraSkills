package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
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
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
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
        return !plugin.getAbilityManager().isEnabled(ability);
    }

    public double getXp(Player player, Source source, Ability ability) {
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            double output = plugin.getSourceManager().getXp(source);
            if (ability != null) {
                if (plugin.getAbilityManager().isEnabled(ability)) {
                    double modifier = 1;
                    modifier += plugin.getAbilityManager().getValue(ability, playerSkill.getAbilityLevel(ability)) / 100;
                    output *= modifier;
                }
            }
            return output;
        }
        return 0.0;
    }

    public boolean isEnabled(Ability ability) {
        return plugin.getAbilityManager().isEnabled(ability);
    }

    public double getValue(Ability ability, PlayerSkill playerSkill) {
        return plugin.getAbilityManager().getValue(ability, playerSkill.getAbilityLevel(ability));
    }

    public double getValue2(Ability ability, PlayerSkill playerSkill) {
        return plugin.getAbilityManager().getValue2(ability, playerSkill.getAbilityLevel(ability));
    }

    public double getValue(MAbility mability, PlayerSkill playerSkill) {
        return plugin.getManaAbilityManager().getValue(mability, playerSkill.getManaAbilityLevel(mability));
    }

    public double getManaCost(MAbility mability, PlayerSkill playerSkill) {
        return plugin.getManaAbilityManager().getManaCost(mability, playerSkill);
    }

}
