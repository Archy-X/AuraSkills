package dev.auramc.auraskills.api.event.skill;

import dev.auramc.auraskills.api.AuraSkillsApi;
import dev.auramc.auraskills.api.event.AuraSkillsEvent;
import dev.auramc.auraskills.api.event.Cancellable;
import dev.auramc.auraskills.api.player.SkillsPlayer;
import dev.auramc.auraskills.api.skill.Skill;

public class XpGainEvent extends AuraSkillsEvent implements Cancellable {

    private final SkillsPlayer player;
    private final Skill skill;
    private double amount;
    private boolean cancelled = false;

    public XpGainEvent(AuraSkillsApi api, SkillsPlayer player, Skill skill, double amount) {
        super(api);
        this.player = player;
        this.skill = skill;
        this.amount = amount;
    }

    public SkillsPlayer getPlayer() {
        return player;
    }

    public Skill getSkill() {
        return skill;
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
