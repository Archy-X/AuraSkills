package dev.aurelium.auraskills.bukkit.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityContext;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AbilityImpl extends AbilityContext implements Listener {

    protected final AuraSkills plugin;
    protected final Random rand = new Random();
    private final List<Ability> abilities = new ArrayList<>();

    public AbilityImpl(AuraSkills plugin, Ability... abilities) {
        super(plugin.getApi());
        this.plugin = plugin;
        this.abilities.addAll(Arrays.asList(abilities));
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    protected double getValue(Ability ability, User user) {
        return ability.getValue(user.getAbilityLevel(ability));
    }

    protected double getSecondaryValue(Ability ability, User user) {
        return ability.getSecondaryValue(user.getAbilityLevel(ability));
    }

}
