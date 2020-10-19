package com.archyx.aureliumskills.menu.templates;

public enum TemplateType {

    SKILL(SkillTemplate.class),
    STAT(StatTemplate.class),
    UNLOCKED(UnlockedTemplate.class),
    IN_PROGRESS(InProgressTemplate.class),
    LOCKED(LockedTemplate.class);

    private final Class<?> loader;

    TemplateType(Class<?> loader) {
        this.loader = loader;
    }

    public Class<?> getLoader() {
        return loader;
    }

}
