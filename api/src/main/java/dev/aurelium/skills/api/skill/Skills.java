package dev.aurelium.skills.api.skill;

import dev.aurelium.skills.api.util.NamespacedId;

import java.util.Locale;

public enum Skills implements Skill {

    FARMING,
    FORAGING,
    MINING,
    FISHING,
    EXCAVATION,
    ARCHERY,
    FIGHTING,
    DEFENSE,
    AGILITY,
    ENDURANCE,
    ALCHEMY,
    ENCHANTING,
    SORCERY,
    HEALING,
    FORGING;

    private final NamespacedId id;

    Skills() {
        this.id = NamespacedId.from("aureliumskills", this.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

}
