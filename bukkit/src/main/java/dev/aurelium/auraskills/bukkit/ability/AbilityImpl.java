package dev.aurelium.auraskills.bukkit.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Locale;
import java.util.Random;

public class AbilityImpl implements Listener {

    protected final AuraSkills plugin;
    protected final Skill skill;
    protected final Random rand = new Random();

    public AbilityImpl(AuraSkills plugin, Skill skill) {
        this.plugin = plugin;
        this.skill = skill;
    }

    protected boolean isDisabled(Ability ability) {
        if (!skill.isEnabled()) {
            return true;
        }
        return !ability.isEnabled();
    }

    protected double getValue(Ability ability, User user) {
        return ability.getValue(user.getAbilityLevel(ability));
    }

    protected boolean failsChecks(Player player) {
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return true;
        }
        if (!player.hasPermission("auraskills.skill" + skill.name().toLowerCase(Locale.ROOT))) {
            return true;
        }
        if (plugin.configBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
            return player.getGameMode().equals(GameMode.CREATIVE);
        }
        return false;
    }

}
