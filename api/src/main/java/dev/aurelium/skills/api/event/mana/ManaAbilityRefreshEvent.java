package dev.aurelium.skills.api.event.mana;

import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.player.SkillsPlayer;

public interface ManaAbilityRefreshEvent extends AureliumSkillsEvent {

    SkillsPlayer getSkillsPlayer();

    ManaAbility getManaAbility();

}
