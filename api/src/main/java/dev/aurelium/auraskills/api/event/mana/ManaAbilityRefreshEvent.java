package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.mana.ManaAbility;

public class ManaAbilityRefreshEvent extends AuraSkillsEvent {

    private final SkillsUser skillsUser;
    private final ManaAbility manaAbility;

    public ManaAbilityRefreshEvent(AuraSkillsApi api, SkillsUser skillsUser, ManaAbility manaAbility) {
        super(api);
        this.skillsUser = skillsUser;
        this.manaAbility = manaAbility;
    }

    public SkillsUser getUser() {
        return skillsUser;
    }

    public ManaAbility getManaAbility() {
        return manaAbility;
    }

}
