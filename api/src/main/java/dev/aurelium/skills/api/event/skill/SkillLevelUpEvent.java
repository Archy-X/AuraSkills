package dev.aurelium.skills.api.event.skill;

import dev.aurelium.skills.api.AureliumSkillsApi;
import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.player.SkillsPlayer;
import dev.aurelium.skills.api.skill.Skill;

public class SkillLevelUpEvent extends AureliumSkillsEvent {

    private final SkillsPlayer skillsPlayer;
    private final Skill skill;
    private final int level;

    public SkillLevelUpEvent(AureliumSkillsApi api, SkillsPlayer skillsPlayer, Skill skill, int level) {
        super(api);
        this.skillsPlayer = skillsPlayer;
        this.skill = skill;
        this.level = level;
    }

    public SkillsPlayer getSkillsPlayer() {
        return skillsPlayer;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

}
