package dev.auramc.auraskills.common.rewards.type;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.hooks.EconomyHook;
import dev.auramc.auraskills.common.rewards.Reward;

import java.util.Locale;

public class MoneyReward extends Reward {

    private final double amount;

    public MoneyReward(AuraSkillsPlugin plugin, double amount) {
        super(plugin);
        this.amount = amount;
    }

    @Override
    public void giveReward(PlayerData player, Skill skill, int level) {
        if (hooks.isRegistered(EconomyHook.class)) {
            hooks.getHook(EconomyHook.class).deposit(player, amount);
        }
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String getMenuMessage(PlayerData player, Locale locale, Skill skill, int level) {
        return ""; // All money rewards have to be added into one line
    }

    @Override
    public String getChatMessage(PlayerData player, Locale locale, Skill skill, int level) {
        return ""; // ALl money rewards have to be added into one line
    }
}
