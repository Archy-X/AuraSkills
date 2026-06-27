package dev.aurelium.auraskills.bukkit.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityContext;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.ability.AbilityImpl;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public class BukkitAbilityImpl extends AbilityImpl implements Listener {

    protected final AuraSkills plugin;
    private final AbilityContext abilityContext;

    public BukkitAbilityImpl(AuraSkills plugin, Ability... abilities) {
        super(abilities);
        this.plugin = plugin;
        this.abilityContext = new AbilityContext(plugin.getApi());
    }

    protected boolean isDisabled(Ability ability) {
        return abilityContext.isDisabled(ability);
    }

    protected boolean failsChecks(Player player, Ability ability) {
        if (abilityContext.failsChecks(player, ability)) {
            return true;
        }
        // Check if ability is disabled during combat
        return isDisabledInCombat(player, ability);
    }

    /**
     * Checks if an ability is disabled during PvP combat.
     *
     * @param player  the player to check
     * @param ability the ability to check
     * @return true if the player is in combat and the ability is disabled during combat
     */
    protected boolean isDisabledInCombat(Player player, Ability ability) {
        if (player == null) return false;

        User user = plugin.getUser(player);

        // Check if player is in combat
        if (!plugin.getCombatTracker().isInCombat(user)) {
            return false;
        }

        // Get the list of disabled abilities during combat
        List<String> disabledAbilities = plugin.configStringList(Option.PVP_DISABLED_ABILITIES);
        if (disabledAbilities.isEmpty()) {
            return false;
        }

        // Check if this ability is in the disabled list
        String abilityId = ability.getId().toString();
        String abilityName = ability.getId().getKey();

        plugin.getLogger().info("Player " + player.getName() + " is in combat. Checking if ability " + abilityId + " is disabled during combat.");

        for (String disabled : disabledAbilities) {
            // Support both namespaced ID and just the key
            if (disabled.equalsIgnoreCase(abilityId) || disabled.equalsIgnoreCase(abilityName)) {
                plugin.getLogger().info("Ability " + abilityId + " is disabled during combat.");
                return true;
            }
        }

        return false;
    }

}
