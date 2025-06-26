package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.common.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AbilityImpl {

    protected final Random rand = new Random();
    private final List<Ability> abilities = new ArrayList<>();

    public AbilityImpl(Ability... abilities) {
        this.abilities.addAll(Arrays.asList(abilities));
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public String replaceDescPlaceholders(String input, Ability ability, User user) {
        return input;
    }

    protected double getValue(Ability ability, User user) {
        return ability.getValue(user.getAbilityLevel(ability));
    }

    protected double getSecondaryValue(Ability ability, User user) {
        return ability.getSecondaryValue(user.getAbilityLevel(ability));
    }

}
