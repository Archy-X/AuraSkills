package dev.aurelium.skills.api.event.mana;

import dev.aurelium.skills.api.AureliumSkillsApi;
import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.event.Cancellable;
import dev.aurelium.skills.api.player.SkillsPlayer;

public class ManaRegenerateEvent extends AureliumSkillsEvent implements Cancellable {

    private final SkillsPlayer skillsPlayer;
    private double amount;
    private boolean cancelled = false;

    public ManaRegenerateEvent(AureliumSkillsApi api, SkillsPlayer skillsPlayer, double amount) {
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
