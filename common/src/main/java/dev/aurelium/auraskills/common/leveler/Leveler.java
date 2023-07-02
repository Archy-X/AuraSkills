package dev.aurelium.auraskills.common.leveler;

import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.data.PlayerData;
import dev.aurelium.auraskills.common.hooks.EconomyHook;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.scheduler.Tick;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Interface with methods to add xp and level up players.
 */
public abstract class Leveler {

    private final AuraSkillsPlugin plugin;
    private final XpRequirements xpRequirements;

    public Leveler(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.xpRequirements = plugin.getXpRequirements();
    }

    public double getPermissionMultiplier(@NotNull PlayerData playerData, Skill skill) {
        return playerData.getPermissionMultiplier(skill);
    }

    public abstract void playLevelUpSound(@NotNull PlayerData playerData);

    public void addXp(PlayerData playerData, Skill skill, double amount) {
        if (amount == 0) return; // Ignore if source amount is 0

        double amountToAdd = amount * calculateMultiplier(playerData, skill);

        // Call event
        XpGainEvent event = new XpGainEvent(plugin.getApi(), playerData.toApi(), skill, amountToAdd);
        plugin.getEventManager().callEvent(event);
        if (event.isCancelled()) return;

        // Add XP to player
        amountToAdd = event.getAmount();
        playerData.addSkillXp(skill, amountToAdd);

        checkLevelUp(playerData, skill);

        // Send action bar and boss bar
        sendXpUi(playerData, skill, amountToAdd);
    }

    private void sendXpUi(PlayerData playerData, Skill skill, double xpGained) {
        double currentXp = playerData.getSkillXp(skill);
        int level = playerData.getSkillLevel(skill);
        double levelXp = xpRequirements.getXpRequired(skill, level + 1);
        boolean maxed = xpRequirements.getListSize(skill) <= playerData.getSkillLevel(skill) - 1 || level >= plugin.config().getMaxLevel(skill);

        if (plugin.configBoolean(Option.ACTION_BAR_XP)) {
            plugin.getUiProvider().sendXpActionBar(playerData, currentXp, levelXp, xpGained, level, maxed);
        }
        if (plugin.configBoolean(Option.BOSS_BAR_ENABLED)) {
            plugin.getUiProvider().sendXpBossBar(playerData, skill, currentXp, levelXp, xpGained, level, maxed);
        }
    }

    public void checkLevelUp(PlayerData playerData, Skill skill) {
        int currentLevel = playerData.getSkillLevel(skill);
        double currentXp = playerData.getSkillXp(skill);

        if (currentLevel >= plugin.config().getMaxLevel(skill)) return; // Check max level options
        if (xpRequirements.getListSize(skill) <= currentLevel - 1) return; // Check if skill is maxed

        if (currentXp >= xpRequirements.getXpRequired(skill, currentLevel + 1)) {
            levelUpSkill(playerData, skill);
        }
    }

    private void levelUpSkill(PlayerData playerData, Skill skill) {
        Locale locale = playerData.getLocale();

        double currentXp = playerData.getSkillXp(skill);
        int level = playerData.getSkillLevel(skill) + 1;

        playerData.setSkillXp(skill, currentXp - xpRequirements.getXpRequired(skill, level));
        playerData.setSkillLevel(skill, level);
        // Give custom rewards
        List<SkillReward> rewards = plugin.getRewardManager().getRewardTable(skill).getRewards(level);
        for (SkillReward reward : rewards) {
            reward.giveReward(playerData, skill, level);
        }
        giveLegacyMoneyRewards(playerData, level);

        // Reload items and armor to check for newly met requirements
        plugin.getStatManager().reloadPlayer(playerData);
        // Calls event
        SkillLevelUpEvent event = new SkillLevelUpEvent(plugin.getApi(), playerData.toApi(), skill, level);
        plugin.getEventManager().callEvent(event);

        // Sends messages
        LevelUpMessenger messenger = new LevelUpMessenger(plugin, playerData, locale, skill, level, rewards);
        if (plugin.configBoolean(Option.LEVELER_TITLE_ENABLED)) {
            messenger.sendTitle();
        }
        if (plugin.configBoolean(Option.LEVELER_SOUND_ENABLED)) {
            playLevelUpSound(playerData);
        }
        messenger.sendChatMessage();

        // Check for multiple level ups in a row after a delay
        plugin.getScheduler().scheduleSync(() -> checkLevelUp(playerData, skill), Tick.MS * plugin.configInt(Option.LEVELER_DOUBLE_CHECK_DELAY), TimeUnit.MILLISECONDS);
    }

    private void giveLegacyMoneyRewards(PlayerData playerData, int level) {
        // Adds money rewards if enabled
        if (plugin.getHookManager().isRegistered(EconomyHook.class)) {
            if (plugin.configBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
                double base = plugin.configDouble(Option.SKILL_MONEY_REWARDS_BASE);
                double multiplier = plugin.configDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
                double moneyToAdd = base + (multiplier * level * level);

                plugin.getHookManager().getHook(EconomyHook.class).deposit(playerData, moneyToAdd);
            }
        }
    }

    private double calculateMultiplier(@NotNull PlayerData playerData, Skill skill) {
        double multiplier = 1.0;
        multiplier += getItemMultiplier(playerData, skill);
        multiplier += getPermissionMultiplier(playerData, skill);
        return multiplier;
    }

    private double getItemMultiplier(@NotNull PlayerData playerData, Skill skill) {
        return playerData.getTotalMultiplier(skill) / 100;
    }

}
