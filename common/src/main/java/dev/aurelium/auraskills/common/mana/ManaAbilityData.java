package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;

public class ManaAbilityData {

    private final ManaAbility manaAbility;
    private int cooldown;
    private boolean ready;
    private boolean activated;
    private int errorTimer;

    public ManaAbilityData(ManaAbility manaAbility) {
        this.manaAbility = manaAbility;
    }

    public ManaAbility getManaAbility() {
        return manaAbility;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getErrorTimer() {
        return errorTimer;
    }

    public void setErrorTimer(int errorTimer) {
        this.errorTimer = errorTimer;
    }
}
