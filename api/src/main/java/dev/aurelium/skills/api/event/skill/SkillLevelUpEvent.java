package dev.aurelium.skills.api.event.skill;

import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.player.SkillsPlayer;
import dev.aurelium.skills.api.skill.Skill;

public interface SkillLevelUpEvent extends AureliumSkillsEvent {

    SkillsPlayer getSkillsPlayer();

    Skill getSkill();

    int getLevel();

}
