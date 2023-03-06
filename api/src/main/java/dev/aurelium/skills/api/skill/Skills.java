package dev.aurelium.skills.api.skill;

import dev.aurelium.skills.api.util.NamespacedId;

public enum Skills implements Skill {

    FARMING("farming"),
    FORAGING("foraging"),
    MINING("mining"),
    FISHING("fishing"),
    EXCAVATION("excavation"),
    ARCHERY("archery"),
    FIGHTING("fighting"),
    DEFENSE("defense"),
    AGILITY("agility"),
    ENDURANCE("endurance"),
    ALCHEMY("alchemy"),
    ENCHANTING("enchanting"),
    SORCERY("sorcery"),
    HEALING("healing"),
    FORGING("forging");

    private final NamespacedId id;

    Skills(String id) {
        this.id = new NamespacedId("aureliumskills", id);
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

}
