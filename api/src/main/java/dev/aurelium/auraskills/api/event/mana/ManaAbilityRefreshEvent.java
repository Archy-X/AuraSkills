package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.player.SkillsPlayer;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.mana.ManaAbility;

public class ManaAbilityRefreshEvent extends AuraSkillsEvent {

    private final SkillsPlayer skillsPlayer;
    private final ManaAbility manaAbility;

    public ManaAbilityRefreshEvent(AuraSkillsApi api, SkillsPlayer skillsPlayer, ManaAbility manaAbility) {
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
