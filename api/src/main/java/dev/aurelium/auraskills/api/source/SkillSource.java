package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.skill.Skill;

public class SkillSource<T extends XpSource> {

    private final T source;
    private final Skill skill;

    public SkillSource(T source, Skill skill) {
        this.source = source;
        this.skill = skill;
    }

    public T source() {
        return source;
    }

    public Skill skill() {
        return skill;
    }
}
