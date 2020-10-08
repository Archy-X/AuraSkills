package com.archyx.aureliumskills.lang;

import com.archyx.aureliumskills.skills.Skill;

public enum SkillMessage implements MessageKey {

    FARMING_NAME,
    FARMING_DESC,
    FORAGING_NAME,
    FORAGING_DESC,
    MINING_NAME,
    MINING_DESC,
    FISHING_NAME,
    FISHING_DESC,
    EXCAVATION_NAME,
    EXCAVATION_DESC,
    ARCHERY_NAME,
    ARCHERY_DESC,
    DEFENSE_NAME,
    DEFENSE_DESC,
    FIGHTING_NAME,
    FIGHTING_DESC,
    ENDURANCE_NAME,
    ENDURANCE_DESC,
    AGILITY_NAME,
    AGILITY_DESC,
    ALCHEMY_NAME,
    ALCHEMY_DESC,
    ENCHANTING_NAME,
    ENCHANTING_DESC,
    SORCERY_NAME,
    SORCERY_DESC,
    HEALING_NAME,
    HEALING_DESC,
    FORGING_NAME,
    FORGING_DESC;

    private final Skill skill = Skill.valueOf(this.name().substring(0, this.name().lastIndexOf("_") - 1));
    private final String path = "skills." + skill.name().toLowerCase() + "." + this.name().substring(this.name().lastIndexOf("_") + 1).toLowerCase();

    public String getPath() {
        return path;
    }

}
