package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.util.AuraSkillsModifier;

public class TraitModifier extends AuraSkillsModifier<Trait> {

    public TraitModifier(String name, Trait trait, double value) {
        super(name, trait, value);
    }

    public Trait trait() {
        return type;
    }

}
