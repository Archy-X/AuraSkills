package dev.aurelium.auraskills.common.reward.type;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.data.PlayerData;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.reward.SkillReward;

import java.util.Locale;

public class MoneyReward extends SkillReward {

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
