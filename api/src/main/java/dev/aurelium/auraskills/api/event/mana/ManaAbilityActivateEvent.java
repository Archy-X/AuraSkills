package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.event.Cancellable;

public class ManaAbilityActivateEvent extends AuraSkillsEvent implements Cancellable {

    private final SkillsUser skillsUser;
    private final ManaAbility manaAbility;
    private int duration;
    private boolean cancelled = false;
    private double manaUsed;

    public ManaAbilityActivateEvent(AuraSkillsApi api, SkillsUser skillsUser, ManaAbility manaAbility, int duration, double manaUsed) {
        super(api);
        this.skillsUser = skillsUser;
        this.manaAbility = manaAbility;
        this.duration = duration;
    }

    public SkillsUser getUser() {
        return skillsUser;
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
