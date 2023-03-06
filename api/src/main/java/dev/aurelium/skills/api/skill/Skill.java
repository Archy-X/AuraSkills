package dev.aurelium.skills.api.skill;

import java.util.Locale;

public abstract class Skill {

    private final String id;

    public Skill(String id) {
        this.id = id.toLowerCase(Locale.ROOT);
    }

    public String getId() {
        return id;
    }

    public abstract int getMaxLevel();

    public abstract String getDisplayName(Locale locale);

    public abstract String getDescription(Locale locale);

}
