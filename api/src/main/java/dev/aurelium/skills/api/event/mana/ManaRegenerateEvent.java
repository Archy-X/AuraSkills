package dev.aurelium.skills.api.event.mana;

import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.event.Cancellable;
import dev.aurelium.skills.api.player.SkillsPlayer;

public interface ManaRegenerateEvent extends AureliumSkillsEvent, Cancellable {

    SkillsPlayer getSkillsPlayer();

    double getAmount();

    void setAmount(double amount);

}
