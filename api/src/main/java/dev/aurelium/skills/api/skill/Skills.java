package dev.aurelium.skills.api.skill;

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

    @Override
    public String getId() {
        return toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getDisplayName(Locale locale) {
        return null;
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }
}
