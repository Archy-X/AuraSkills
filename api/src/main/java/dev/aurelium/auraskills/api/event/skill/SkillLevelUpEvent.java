package dev.aurelium.auraskills.api.event.skill;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.skill.Skill;

public class SkillLevelUpEvent extends AuraSkillsEvent {

    private final SkillsUser user;
    private final Skill skill;
    private final int level;

    public SkillLevelUpEvent(AuraSkillsApi api, SkillsUser user, Skill skill, int level) {
        super(api);
        this.user = user;
        this.skill = skill;
        this.level = level;
    }

    public SkillsUser getUser() {
        return user;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

}
