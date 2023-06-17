package dev.aurelium.auraskills.common.leveler;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.data.PlayerData;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.LevelerMessage;
import dev.aurelium.auraskills.common.rewards.Reward;
import dev.aurelium.auraskills.common.rewards.type.MoneyReward;
import dev.aurelium.auraskills.common.util.math.RomanNumber;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LevelUpMessenger {

    private final AuraSkillsPlugin plugin;
    private final PlayerData playerData;
    private final Locale locale;
    private final Skill skill;
    private final int level;
    private final List<Reward> rewards;

    public LevelUpMessenger(AuraSkillsPlugin plugin, PlayerData playerData, Locale locale, Skill skill, int level, List<Reward> rewards) {
        this.plugin = plugin;
        this.playerData = playerData;
        this.locale = locale;
        this.skill = skill;
        this.level = level;
        this.rewards = rewards;
    }

    public void sendChatMessage() {
        MessageBuilder.create(plugin).locale(locale).message(LevelerMessage.LEVEL_UP,
                "skill", skill.getDisplayName(locale),
                "old", RomanNumber.toRoman(level - 1, plugin),
                "new", RomanNumber.toRoman(level, plugin),
                "stat_level", getRewardMessage(),
                "ability_unlock", getAbilityUnlockMessage(),
                "ability_level_up", getAbilityLevelUpMessage(),
                "mana_ability_unlock", getManaAbilityUnlockMessage(),
                "mana_ability_level_up", getManaAbilityLevelUpMessage(),
                "money_reward", getMoneyRewardMessage());
    }

    public void sendTitle() {
        String title = MessageBuilder.create(plugin).locale(locale)
                .message(LevelerMessage.TITLE,
                        "skill", skill.getDisplayName(locale),
                        "old", RomanNumber.toRoman(level - 1, plugin),
                        "new", RomanNumber.toRoman(level, plugin))
                .toString();
        String subtitle = MessageBuilder.create(plugin).locale(locale)
                .message(LevelerMessage.SUBTITLE,
                        "skill", skill.getDisplayName(locale),
                        "old", RomanNumber.toRoman(level - 1, plugin),
                        "new", RomanNumber.toRoman(level, plugin))
                .toString();
        plugin.getUiProvider().sendTitle(playerData, title, subtitle, plugin.configInt(Option.LEVELER_TITLE_FADE_IN), plugin.configInt(Option.LEVELER_TITLE_STAY), plugin.configInt(Option.LEVELER_TITLE_FADE_OUT));
    }

    private String getRewardMessage() {
        StringBuilder rewardMessage = new StringBuilder();
        for (Reward reward : rewards) {
            rewardMessage.append(reward.getChatMessage(playerData, locale, skill, level));
        }
        return rewardMessage.toString();
    }

    private String getAbilityUnlockMessage() {
        MessageBuilder builder = MessageBuilder.create(plugin).locale(locale);
        for (Ability ability : plugin.getAbilityManager().getAbilities(skill, level)) {
            if (!ability.isEnabled()) {
                continue;
            }
            if (ability.getUnlock() == level) { // If ability is unlocked at this level
                builder.message(LevelerMessage.ABILITY_UNLOCK,
                        "ability", ability.getDisplayName(locale));
            }
        }
        return builder.toString();
    }

    private String getAbilityLevelUpMessage() {
        MessageBuilder builder = MessageBuilder.create(plugin).locale(locale);
        for (Ability ability : plugin.getAbilityManager().getAbilities(skill, level)) {
            if (!ability.isEnabled()) {
                continue;
            }
            if (ability.getUnlock() != level) { // If ability is unlocked at this level
                builder.message(LevelerMessage.ABILITY_LEVEL_UP,
                        "ability", ability.getDisplayName(locale),
                        "level", RomanNumber.toRoman(playerData.getAbilityLevel(ability), plugin));
            }
        }
        return builder.toString();
    }

    private String getManaAbilityUnlockMessage() {
        MessageBuilder builder = MessageBuilder.create(plugin).locale(locale);
        ManaAbility manaAbility = plugin.getManaAbilityManager().getManaAbilityAtLevel(skill, level);

        if (manaAbility == null) return "";
        if (!manaAbility.isEnabled()) return "";

        // If mana ability is unlocked at this level
        if (manaAbility.getUnlock() == level) {
            builder.message(LevelerMessage.MANA_ABILITY_UNLOCK,
                    "mana_ability", manaAbility.getDisplayName(locale));
        }
        return builder.toString();
    }

    private String getManaAbilityLevelUpMessage() {
        MessageBuilder builder = MessageBuilder.create(plugin).locale(locale);
        ManaAbility manaAbility = plugin.getManaAbilityManager().getManaAbilityAtLevel(skill, level);

        if (manaAbility == null) return "";
        if (!manaAbility.isEnabled()) return "";

        // If mana ability is unlocked at this level
        if (manaAbility.getUnlock() != level) {
            builder.message(LevelerMessage.MANA_ABILITY_LEVEL_UP,
                    "mana_ability", manaAbility.getDisplayName(locale),
                    "level", RomanNumber.toRoman(playerData.getManaAbilityLevel(manaAbility), plugin));
        }
        return builder.toString();
    }

    private String getMoneyRewardMessage() {
        MessageBuilder builder = MessageBuilder.create(plugin).locale(locale);
        double totalMoney = 0;
        // Legacy system
        if (plugin.getHookManager().isRegistered(EconomyHook.class)) {
            if (plugin.configBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
                double base = plugin.configDouble(Option.SKILL_MONEY_REWARDS_BASE);
                double multiplier = plugin.configDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
                totalMoney += base + (multiplier * level * level);
            }
        }
        // New rewards
        for (MoneyReward reward : plugin.getRewardManager().getRewardTable(skill).searchRewards(MoneyReward.class, level)) {
            totalMoney += reward.getAmount();
        }
        if (totalMoney > 0) {
            NumberFormat nf = new DecimalFormat("#.##");
            builder.message(LevelerMessage.MONEY_REWARD,
                    "amount", nf.format(totalMoney));
        }
        return builder.toString();
    }

}
