package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.player.SkillsPlayer;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.event.Cancellable;

public class ManaAbilityActivateEvent extends AuraSkillsEvent implements Cancellable {

    private final SkillsPlayer skillsPlayer;
    private final ManaAbility manaAbility;
    private int duration;
    private boolean cancelled = false;
    private double manaUsed;

    public ManaAbilityActivateEvent(AuraSkillsApi api, SkillsPlayer skillsPlayer, ManaAbility manaAbility, int duration, double manaUsed) {
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

    public double getManaUsed() {
        return manaUsed;
    }

    public void setManaUsed(double manaUsed) {
        this.manaUsed = manaUsed;
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
