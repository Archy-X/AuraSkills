package dev.aurelium.skills.common.rewards.type;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;
import dev.aurelium.skills.common.hooks.EconomyHook;
import dev.aurelium.skills.common.rewards.Reward;

import java.util.Locale;

public class MoneyReward extends Reward {

    private final double amount;

    public MoneyReward(AureliumSkillsPlugin plugin, double amount) {
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
