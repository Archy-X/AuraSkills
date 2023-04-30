package dev.aurelium.skills.api.event.mana;

import dev.aurelium.skills.api.AureliumSkillsApi;
import dev.aurelium.skills.api.mana.ManaAbility;
import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.event.Cancellable;
import dev.aurelium.skills.api.player.SkillsPlayer;

public class ManaAbilityActivateEvent extends AureliumSkillsEvent implements Cancellable {

    private final SkillsPlayer skillsPlayer;
    private final ManaAbility manaAbility;
    private int duration;
    private boolean cancelled = false;

    public ManaAbilityActivateEvent(AureliumSkillsApi api, SkillsPlayer skillsPlayer, ManaAbility manaAbility, int duration) {
        super(api);
        this.skillsPlayer = skillsPlayer;
        this.manaAbility = manaAbility;
        this.duration = duration;
    }

    public SkillsPlayer getSkillsPlayer() {
        return skillsPlayer;
    }

    public ManaAbility getManaAbility() {
        return manaAbility;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
