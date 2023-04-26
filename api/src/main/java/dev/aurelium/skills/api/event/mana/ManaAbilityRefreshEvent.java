package dev.aurelium.skills.api.event.mana;

import dev.aurelium.skills.api.AureliumSkillsApi;
import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.player.SkillsPlayer;

public class ManaAbilityRefreshEvent extends AureliumSkillsEvent {

    private final SkillsPlayer skillsPlayer;
    private final ManaAbility manaAbility;

    public ManaAbilityRefreshEvent(AureliumSkillsApi api, SkillsPlayer skillsPlayer, ManaAbility manaAbility) {
        super(api);
        this.skillsPlayer = skillsPlayer;
        this.manaAbility = manaAbility;
    }

    public SkillsPlayer getSkillsPlayer() {
        return skillsPlayer;
    }

    public ManaAbility getManaAbility() {
        return manaAbility;
    }

}
