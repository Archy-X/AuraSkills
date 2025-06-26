package dev.aurelium.auraskills.bukkit.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityContext;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.ability.AbilityImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
        return abilityContext.failsChecks(player, ability);
    }

}
