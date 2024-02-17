package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;

import java.util.ArrayList;
import java.util.List;

public enum SourceTag {

    FARMING_LUCK_APPLICABLE(Skills.FARMING),
    FORAGING_LUCK_APPLICABLE(Skills.FORAGING),
    MINING_LUCK_APPLICABLE(Skills.MINING),
    FISHING_LUCK_APPLICABLE(Skills.FISHING),
    EXCAVATION_LUCK_APPLICABLE(Skills.EXCAVATION),
    SPEED_MINE_APPLICABLE(Skills.MINING),
    DIRTS(Skills.EXCAVATION),
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
