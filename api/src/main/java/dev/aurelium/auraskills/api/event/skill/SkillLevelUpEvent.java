package dev.aurelium.auraskills.api.event.skill;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.skill.Skill;

public class SkillLevelUpEvent extends AuraSkillsEvent {

    private final SkillsUser skillsUser;
    private final Skill skill;
    private final int level;

    public SkillLevelUpEvent(AuraSkillsApi api, SkillsUser skillsUser, Skill skill, int level) {
        super(api);
        this.skillsUser = skillsUser;
        this.skill = skill;
        this.level = level;
    }

    public SkillsUser getUser() {
        return skillsUser;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

}
