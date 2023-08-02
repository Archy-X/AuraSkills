package dev.aurelium.auraskills.api.event.mana;

import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.Cancellable;

public class ManaRegenerateEvent extends AuraSkillsEvent implements Cancellable {

    private final SkillsUser skillsUser;
    private double amount;
    private boolean cancelled = false;

    public ManaRegenerateEvent(AuraSkillsApi api, SkillsUser skillsUser, double amount) {
        super(api);
        this.skillsUser = skillsUser;
        this.amount = amount;
    }

    public SkillsUser getUser() {
        return skillsUser;
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
