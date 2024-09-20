package dev.aurelium.auraskills.common;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.common.ability.AbilityManager;
import dev.aurelium.auraskills.common.ability.AbilityRegistry;
import dev.aurelium.auraskills.common.api.implementation.ApiProvider;
import dev.aurelium.auraskills.common.config.ConfigProvider;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.config.preset.PresetManager;
import dev.aurelium.auraskills.common.event.EventHandler;
import dev.aurelium.auraskills.common.hooks.HookManager;
import dev.aurelium.auraskills.common.item.ItemRegistry;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.level.LevelManager;
import dev.aurelium.auraskills.common.level.XpRequirements;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import dev.aurelium.auraskills.common.mana.ManaAbilityRegistry;
import dev.aurelium.auraskills.common.menu.MenuHelper;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.message.PlatformLogger;
import dev.aurelium.auraskills.common.modifier.ModifierManager;
import dev.aurelium.auraskills.common.region.WorldManager;
import dev.aurelium.auraskills.common.reward.RewardManager;
import dev.aurelium.auraskills.common.scheduler.Scheduler;
import dev.aurelium.auraskills.common.skill.SkillManager;
import dev.aurelium.auraskills.common.skill.SkillRegistry;
import dev.aurelium.auraskills.common.source.SourceTypeRegistry;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.stat.StatRegistry;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.storage.backup.BackupProvider;
import dev.aurelium.auraskills.common.trait.TraitManager;
import dev.aurelium.auraskills.common.trait.TraitRegistry;
import dev.aurelium.auraskills.common.ui.UiProvider;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserManager;
import dev.aurelium.auraskills.common.util.PlatformUtil;

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

    LevelManager getLevelManager();

    UserManager getUserManager();

    XpRequirements getXpRequirements();

    PlatformLogger logger();

    SkillManager getSkillManager();

    SkillRegistry getSkillRegistry();

    StatRegistry getStatRegistry();

    TraitRegistry getTraitRegistry();

    TraitManager getTraitManager();

    AbilityRegistry getAbilityRegistry();

    ManaAbilityRegistry getManaAbilityRegistry();

    SourceTypeRegistry getSourceTypeRegistry();

    HookManager getHookManager();

    LeaderboardManager getLeaderboardManager();

    UiProvider getUiProvider();

    RewardManager getRewardManager();

    Scheduler getScheduler();

    StorageProvider getStorageProvider();

    BackupProvider getBackupProvider();

    WorldManager getWorldManager();

    MenuHelper getMenuHelper();

    EventHandler getEventHandler();

    PresetManager getPresetManager();

    PlatformUtil getPlatformUtil();

    ApiProvider getApiProvider();

    ModifierManager getModifierManager();

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
     * @param user The player to execute the command as
     * @param command The command to execute
     */
    void runPlayerCommand(User user, String command);

    InputStream getResource(String path);

    void saveResource(String path, boolean replace);

    File getPluginFolder();

}
