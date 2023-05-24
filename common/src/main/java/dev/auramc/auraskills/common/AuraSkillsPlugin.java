package dev.auramc.auraskills.common;

import dev.auramc.auraskills.api.AuraSkillsApi;
import dev.auramc.auraskills.common.ability.AbilityManager;
import dev.auramc.auraskills.common.ability.AbilityRegistry;
import dev.auramc.auraskills.common.config.ConfigProvider;
import dev.auramc.auraskills.common.config.Option;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerManager;
import dev.auramc.auraskills.common.event.AuraSkillsEventManager;
import dev.auramc.auraskills.common.hooks.HookManager;
import dev.auramc.auraskills.common.item.ItemRegistry;
import dev.auramc.auraskills.common.leaderboard.LeaderboardManager;
import dev.auramc.auraskills.common.leveler.Leveler;
import dev.auramc.auraskills.common.leveler.XpRequirements;
import dev.auramc.auraskills.common.mana.ManaAbilityManager;
import dev.auramc.auraskills.common.mana.ManaAbilityRegistry;
import dev.auramc.auraskills.common.message.MessageKey;
import dev.auramc.auraskills.common.message.MessageProvider;
import dev.auramc.auraskills.common.message.PlatformLogger;
import dev.auramc.auraskills.common.modifier.ModifierManager;
import dev.auramc.auraskills.common.rewards.RewardManager;
import dev.auramc.auraskills.common.scheduler.Scheduler;
import dev.auramc.auraskills.common.skill.SkillRegistry;
import dev.auramc.auraskills.common.stat.StatManager;
import dev.auramc.auraskills.common.stat.StatRegistry;
import dev.auramc.auraskills.common.storage.StorageProvider;
import dev.auramc.auraskills.common.ui.UiProvider;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public interface AuraSkillsPlugin {

    AuraSkillsApi getApi();

    MessageProvider getMessageProvider();

    ConfigProvider config();

    AbilityManager getAbilityManager();

    ManaAbilityManager getManaAbilityManager();

    StatManager getStatManager();

    ItemRegistry getItemRegistry();

    Leveler getLeveler();

    PlayerManager getPlayerManager();

    XpRequirements getXpRequirements();

    AuraSkillsEventManager getEventManager();

    PlatformLogger logger();

    SkillRegistry getSkillRegistry();

    StatRegistry getStatRegistry();

    AbilityRegistry getAbilityRegistry();

    ManaAbilityRegistry getManaAbilityRegistry();

    HookManager getHookManager();

    LeaderboardManager getLeaderboardManager();

    UiProvider getUiProvider();

    RewardManager getRewardManager();

    ModifierManager getModifierManager();

    Scheduler getScheduler();

    StorageProvider getStorageProvider();

    // Message convenience methods

    /**
     * Gets a message from the message provider.
     *
     * @param key The message key
     * @param locale The language to get the message in
     * @return The message
     */
    String getMsg(MessageKey key, Locale locale);

    /**
     * Gets the plugin prefix for chat messages
     *
     * @param locale The language to get the prefix in
     * @return The prefix
     */
    String getPrefix(Locale locale);

    /**
     * Gets the default language of the plugin as set by the server's configuration.
     *
     * @return The default language
     */
    default Locale getDefaultLanguage() {
        return getMessageProvider().getDefaultLanguage();
    }

    // Config convenience methods

    default boolean configBoolean(Option option) {
        return config().getBoolean(option);
    }

    default int configInt(Option option) {
        return config().getInt(option);
    }

    default double configDouble(Option option) {
        return config().getDouble(option);
    }

    default String configString(Option option) {
        return config().getString(option);
    }

    default List<String> configStringList(Option option) {
        return config().getStringList(option);
    }

    // Platform-dependent Minecraft methods

    /**
     * Executes a command as the console
     *
     * @param command The command to execute
     */
    void runConsoleCommand(String command);

    /**
     * Executes a command as a player
     *
     * @param playerData The player to execute the command as
     * @param command The command to execute
     */
    void runPlayerCommand(PlayerData playerData, String command);

    InputStream getResource(String path);

    void saveResource(String path, boolean replace);

    File getDataFolder();

}
