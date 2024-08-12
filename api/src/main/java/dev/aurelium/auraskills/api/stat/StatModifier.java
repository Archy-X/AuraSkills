package dev.aurelium.auraskills.api.stat;

import dev.aurelium.auraskills.api.util.AuraSkillsModifier;

public class StatModifier extends AuraSkillsModifier<Stat> {

    public StatModifier(String name, Stat stat, double value, Operation operation) {
        super(name, stat, value, operation);
    }

    public StatModifier(String name, Stat stat, double value) {
        this(name, stat, value, Operation.ADD);
    }

    public Stat stat() {
        return type;
    }

}
