package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.util.AuraSkillsModifier;

public class TraitModifier extends AuraSkillsModifier<Trait> {

    public static final String ITEM_PREFIX = "AuraSkills.TraitModifiers.";

    public TraitModifier(String name, Trait trait, double value, Operation operation) {
        super(name, trait, value, operation);
    }

    @Deprecated
    public TraitModifier(String name, Trait trait, double value) {
        this(name, trait, value, Operation.ADD);
    }

    public Trait trait() {
        return type;
    }

}
