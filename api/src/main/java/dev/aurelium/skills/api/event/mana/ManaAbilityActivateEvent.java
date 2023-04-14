package dev.aurelium.skills.api.event.mana;

import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.event.Cancellable;
import dev.aurelium.skills.api.player.SkillsPlayer;

public interface ManaAbilityActivateEvent extends AureliumSkillsEvent, Cancellable {

    SkillsPlayer getSkillsPlayer();

    ManaAbility getManaAbility();

    int getDuration();

    void setDuration(int duration);

}
