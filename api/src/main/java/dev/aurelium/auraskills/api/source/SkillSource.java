package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.skill.Skill;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SkillSource<?> that = (SkillSource<?>) o;
        return Objects.equals(source, that.source) && Objects.equals(skill, that.skill);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, skill);
    }
}
