package dev.aurelium.auraskills.api.stat;

import dev.aurelium.auraskills.api.util.AuraSkillsModifier;

public class StatModifier extends AuraSkillsModifier<Stat> {

    public StatModifier(String name, Stat stat, double value) {
        super(name, stat, value);
    }

    public Stat stat() {
        return type;
    }

}
