package dev.auramc.auraskills.api.event.mana;

import dev.auramc.auraskills.api.event.AuraSkillsEvent;
import dev.auramc.auraskills.api.player.SkillsPlayer;
import dev.auramc.auraskills.api.AuraSkillsApi;
import dev.auramc.auraskills.api.event.Cancellable;

public class ManaRegenerateEvent extends AuraSkillsEvent implements Cancellable {

    private final SkillsPlayer skillsPlayer;
    private double amount;
    private boolean cancelled = false;

    public ManaRegenerateEvent(AuraSkillsApi api, SkillsPlayer skillsPlayer, double amount) {
        super(api);
        this.skillsPlayer = skillsPlayer;
        this.amount = amount;
    }

    public SkillsPlayer getSkillsPlayer() {
        return skillsPlayer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
