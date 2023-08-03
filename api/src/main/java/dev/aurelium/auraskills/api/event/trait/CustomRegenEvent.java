package dev.aurelium.auraskills.api.event.trait;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.event.Cancellable;
import dev.aurelium.auraskills.api.user.SkillsUser;

public class CustomRegenEvent extends AuraSkillsEvent implements Cancellable {

    private final SkillsUser user;
    private double amount;
    private final Reason reason;
    private boolean cancelled = false;

    public CustomRegenEvent(AuraSkillsApi api, SkillsUser user, double amount, Reason reason) {
        super(api);
        this.user = user;
        this.amount = amount;
        this.reason = reason;
    }

    public SkillsUser getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Reason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Reason {

        SATURATION,
        HUNGER

    }

}
