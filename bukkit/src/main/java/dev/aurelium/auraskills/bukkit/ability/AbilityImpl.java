package dev.aurelium.auraskills.bukkit.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public class AbilityImpl implements Listener {

    protected final AuraSkills plugin;
    protected final Random rand = new Random();
    private final List<Ability> abilities = new ArrayList<>();

    public AbilityImpl(AuraSkills plugin, Ability... abilities) {
        this.plugin = plugin;
        this.abilities.addAll(Arrays.asList(abilities));
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    protected boolean isDisabled(Ability ability) {
        if (!ability.getSkill().isEnabled()) {
            return true;
        }
        return !ability.isEnabled();
    }

    protected double getValue(Ability ability, User user) {
        return ability.getValue(user.getAbilityLevel(ability));
    }

    protected boolean failsChecks(Player player, Ability ability) {
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return true;
        }
        if (!player.hasPermission("auraskills.skill." + ability.getSkill().name().toLowerCase(Locale.ROOT))) {
            return true;
        }
        if (plugin.configBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
            return player.getGameMode().equals(GameMode.CREATIVE);
        }
        return false;
    }

}
