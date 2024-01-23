package dev.aurelium.auraskills.api.ability;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.AuraSkillsBukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Locale;

public class AbilityContext {

    private final AuraSkillsApi api;

    public AbilityContext(AuraSkillsApi api) {
        this.api = api;
    }

    /**
     * Gets whether the ability or it's parent skill is disabled
     *
     * @param ability the ability to check
     * @return whether the ability is disabled
     */
    public boolean isDisabled(Ability ability) {
        if (!ability.getSkill().isEnabled()) {
            return true;
        }
        return !ability.isEnabled();
    }

    /**
     * Performs multiple checks to see whether the player should be allowed to use the ability.
     * Checks for the ability being locked, the location being disabled, inadequate permissions, among
     * other checks.
     *
     * @param player the player to check
     * @param ability the ability to check for
     * @return true if one of the checks failed, false if all checks passed
     */
    public boolean failsChecks(Player player, Ability ability) {
        if (api.getUser(player.getUniqueId()).getAbilityLevel(ability) <= 0) {
            return true;
        }
        if (AuraSkillsBukkit.get().getLocationManager().isPluginDisabled(player.getLocation(), player)) {
            return true;
        }
        if (!player.hasPermission("auraskills.skill." + ability.getSkill().name().toLowerCase(Locale.ROOT))) {
            return true;
        }
        if (api.getMainConfig().isDisabledInCreative()) {
            return player.getGameMode().equals(GameMode.CREATIVE);
        }
        return false;
    }

}
