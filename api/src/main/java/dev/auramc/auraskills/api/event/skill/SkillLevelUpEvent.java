package dev.auramc.auraskills.api.event.skill;

import dev.auramc.auraskills.api.AuraSkillsApi;
import dev.auramc.auraskills.api.event.AuraSkillsEvent;
import dev.auramc.auraskills.api.player.SkillsPlayer;
import dev.auramc.auraskills.api.skill.Skill;

public class SkillLevelUpEvent extends AuraSkillsEvent {

    private final SkillsPlayer skillsPlayer;
    private final Skill skill;
    private final int level;

    public SkillLevelUpEvent(AuraSkillsApi api, SkillsPlayer skillsPlayer, Skill skill, int level) {
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
