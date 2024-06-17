package dev.aurelium.auraskills.common.level;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityUtil;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.LevelerFormat;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.type.MoneyReward;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import net.kyori.adventure.text.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LevelUpMessenger {

    private static final int WRAP_LENGTH = 40;
    private final AuraSkillsPlugin plugin;
    private final User user;
    private final Locale locale;
    private final Skill skill;
    private final int level;
    private final List<SkillReward> rewards;

    public LevelUpMessenger(AuraSkillsPlugin plugin, User user, Locale locale, Skill skill, int level, List<SkillReward> rewards) {
        this.plugin = plugin;
        this.user = user;
        this.locale = locale;
        this.skill = skill;
        this.level = level;
        this.rewards = rewards;
    }

    public void sendChatMessage() {
        String message = MessageBuilder.create(plugin).locale(locale).rawMessage(LevelerFormat.CHAT,
                "skill", skill.getDisplayName(locale, false),
                "old", RomanNumber.toRoman(level - 1, plugin),
                "new", RomanNumber.toRoman(level, plugin),
                "stat_level", getRewardMessage(),
                "ability_unlock", getAbilityUnlockMessage(),
                "ability_level_up", getAbilityLevelUpMessage(),
                "mana_ability_unlock", getManaAbilityUnlockMessage(),
                "mana_ability_level_up", getManaAbilityLevelUpMessage(),
                "money_reward", getMoneyRewardMessage())
                .toString();
        // Replace PlaceholderAPI placeholders
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            message = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, message);
        }
        Component component = plugin.getMessageProvider().stringToComponent(message);
        user.sendMessage(component);
    }

    public void sendTitle() {
        String title = MessageBuilder.create(plugin).locale(locale)
                .rawMessage(LevelerFormat.TITLE,
                        "skill", skill.getDisplayName(locale, false),
                        "old", RomanNumber.toRoman(level - 1, plugin),
                        "new", RomanNumber.toRoman(level, plugin))
                .toString();
        String subtitle = MessageBuilder.create(plugin).locale(locale)
                .rawMessage(LevelerFormat.SUBTITLE,
                        "skill", skill.getDisplayName(locale, false),
                        "old", RomanNumber.toRoman(level - 1, plugin),
                        "new", RomanNumber.toRoman(level, plugin))
                .toString();
        plugin.getUiProvider().sendTitle(user, title, subtitle, plugin.configInt(Option.LEVELER_TITLE_FADE_IN), plugin.configInt(Option.LEVELER_TITLE_STAY), plugin.configInt(Option.LEVELER_TITLE_FADE_OUT));
    }

    private String getRewardMessage() {
        StringBuilder rewardMessage = new StringBuilder();
        for (SkillReward reward : rewards) {
            rewardMessage.append(reward.getChatMessage(user, locale, skill, level));
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
                String desc = TextUtil.replace(plugin.getAbilityManager().getBaseDescription(ability, user, false),
                        "{value}", AbilityUtil.getCurrentValue(ability, 1),
                        "{value_2}", AbilityUtil.getCurrentValue2(ability, 1));
                desc = TextUtil.wrapText(desc, WRAP_LENGTH, "\n" + descWrap(locale));
                builder.rawMessage(LevelerFormat.ABILITY_UNLOCK,
                        "ability", ability.getDisplayName(locale),
                        "desc", desc);
            }
        }
        return builder.toString();
    }

    private String getAbilityLevelUpMessage() {
        StringBuilder sb = new StringBuilder();
        for (Ability ability : plugin.getAbilityManager().getAbilities(skill, level)) {
            if (!ability.isEnabled()) {
                continue;
            }
            if (ability.getUnlock() != level) { // If ability is unlocked at this level
                int level = user.getAbilityLevel(ability);
                sb.append(TextUtil.replace(plugin.getMessageProvider().getRaw(LevelerFormat.ABILITY_LEVEL_UP, locale),
                        "{ability}", ability.getDisplayName(locale),
                        "{previous}", RomanNumber.toRoman(level - 1, plugin),
                        "{level}", RomanNumber.toRoman(level, plugin),
                        "{desc}", getAbilityLevelUpDesc(ability, level, locale)));
            }
        }
        return sb.toString();
    }

    private String getAbilityLevelUpDesc(Ability ability, int level, Locale locale) {
        String format = plugin.getMessageProvider().getRaw(LevelerFormat.DESC_UPGRADE_VALUE, locale);
        // Subtract 1 from level to go from previous to current value
        String desc = TextUtil.replace(plugin.getAbilityManager().getBaseDescription(ability, user, false),
                        "{value}", AbilityUtil.getUpgradeValue(ability, level - 1, format),
                        "{value_2}", AbilityUtil.getUpgradeValue2(ability, level - 1, format));
        desc = TextUtil.wrapText(desc, WRAP_LENGTH, "\n" + descWrap(locale));
        return desc;
    }

    private String getManaAbilityUnlockMessage() {
        StringBuilder sb = new StringBuilder();
        ManaAbility manaAbility = plugin.getManaAbilityManager().getManaAbilityAtLevel(skill, level);

        if (manaAbility == null) return "";
        if (!manaAbility.isEnabled()) return "";

        // If mana ability is unlocked at this level
        if (manaAbility.getUnlock() == level) {
            String desc = TextUtil.replace(plugin.getManaAbilityManager().getBaseDescription(manaAbility, user, false)
                            .replace("<1>", "<white>"),
                    "{value}", String.valueOf(manaAbility.getDisplayValue(1)),
                    "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)),
                    "{duration}", NumberUtil.format1(AbilityUtil.getDuration(manaAbility, 1)));
            desc = TextUtil.wrapText(desc, WRAP_LENGTH, "\n" + descWrap(locale));
            sb.append(TextUtil.replace(plugin.getMessageProvider().getRaw(LevelerFormat.MANA_ABILITY_UNLOCK, locale),
                    "{mana_ability}", manaAbility.getDisplayName(locale),
                    "{desc}", desc));
        }
        return sb.toString();
    }

    private String getManaAbilityLevelUpMessage() {
        StringBuilder sb = new StringBuilder();
        ManaAbility manaAbility = plugin.getManaAbilityManager().getManaAbilityAtLevel(skill, level);

        if (manaAbility == null) return "";
        if (!manaAbility.isEnabled()) return "";

        // If mana ability is unlocked at this level
        if (manaAbility.getUnlock() != level) {
            int level = user.getManaAbilityLevel(manaAbility);
            sb.append(TextUtil.replace(plugin.getMessageProvider().getRaw(LevelerFormat.MANA_ABILITY_LEVEL_UP, locale),
                    "{mana_ability}", manaAbility.getDisplayName(locale, false),
                    "{previous}", RomanNumber.toRoman(level - 1, plugin),
                    "{level}", RomanNumber.toRoman(level, plugin),
                    "{desc}", getManaAbilityLevelUpDesc(manaAbility, level, locale)));
        }
        return sb.toString();
    }

    private String getManaAbilityLevelUpDesc(ManaAbility manaAbility, int level, Locale locale) {
        String format = plugin.getMessageProvider().getRaw(LevelerFormat.DESC_UPGRADE_VALUE, locale);
        // Subtract 1 from level to go from previous to current value
        String message = TextUtil.replace(plugin.getManaAbilityManager().getBaseDescription(manaAbility, user, false)
                        .replace("<1>", "<white>"),
                        "{value}", AbilityUtil.getUpgradeValue(manaAbility, level - 1, format),
                        "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)),
                        "{duration}", AbilityUtil.getUpgradeDuration(manaAbility, level - 1, format));
        message = TextUtil.wrapText(message, WRAP_LENGTH, "\n" + descWrap(locale));
        return message;
    }

    private String getMoneyRewardMessage() {
        MessageBuilder builder = MessageBuilder.create(plugin).locale(locale);
        double totalMoney = 0;
        // New rewards
        for (MoneyReward reward : plugin.getRewardManager().getRewardTable(skill).searchRewards(MoneyReward.class, level)) {
            totalMoney += reward.getAmount(level);
        }
        if (totalMoney > 0) {
            NumberFormat nf = new DecimalFormat("#.##");
            builder.rawMessage(LevelerFormat.MONEY_REWARD,
                    "amount", nf.format(totalMoney));
        }
        return builder.toString();
    }

    private String descWrap(Locale locale) {
        return plugin.getMessageProvider().getRaw(LevelerFormat.DESC_WRAP, locale);
    }

}
