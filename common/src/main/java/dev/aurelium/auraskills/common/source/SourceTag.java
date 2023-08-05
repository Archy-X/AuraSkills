package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;

import java.util.ArrayList;
import java.util.List;

public enum SourceTag {

    BOUNTIFUL_HARVEST_APPLICABLE(Skills.FARMING),
    TRIPLE_HARVEST_APPLICABLE(Skills.FARMING),
    LUMBERJACK_APPLICABLE(Skills.FORAGING),
    LUCKY_MINER_APPLICABLE(Skills.MINING),
    SPEED_MINE_APPLICABLE(Skills.MINING),
    DIRTS(Skills.EXCAVATION),
    BIGGER_SCOOP_APPLICABLE(Skills.EXCAVATION),
    METAL_DETECTOR_APPLICABLE(Skills.EXCAVATION),
    LUCKY_SPADES_APPLICABLE(Skills.EXCAVATION),
    TERRAFORM_APPLICABLE(Skills.EXCAVATION);

    private final Skill skill;

    SourceTag(Skill skill) {
        this.skill = skill;
    }

    public Skill getSkill() {
        return skill;
    }

    public static List<SourceTag> ofSkill(Skill skill) {
        List<SourceTag> list = new ArrayList<>();
        for (var tag : values()) {
            if (tag.getSkill().equals(skill)) {
                list.add(tag);
            }
        }
        return list;
    }

}
