package dev.aurelium.skills.api.skill;

import java.util.Locale;

public interface Skill {

    String getId();

    String getDisplayName(Locale locale);

    String getDescription(Locale locale);

}
