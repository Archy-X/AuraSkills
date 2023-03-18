package dev.aurelium.skills.api.ability;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.skill.Skills;
import dev.aurelium.skills.api.util.NamespacedId;

public enum ManaAbilities implements ManaAbility {

    REPLENISH("replenish", Skills.FARMING),
    TREECAPITATOR("treecapitator", Skills.FORAGING),
    SPEED_MINE("speed_mine", Skills.MINING),
    SHARP_HOOK("sharp_hook", Skills.FISHING),
    TERRAFORM("terraform", Skills.EXCAVATION),
    CHARGED_SHOT("charged_shot", Skills.ARCHERY),
    ABSORPTION("absorption", Skills.DEFENSE),
    LIGHTNING_BLADE("lightning_blade", Skills.FIGHTING);

    private final NamespacedId id;
    private final Skill skill;

    ManaAbilities(String id, Skill skill) {
        this.id = new NamespacedId("aureliumskills", id);
        this.skill = skill;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public Skill getSkill() {
        return skill;
    }
}
