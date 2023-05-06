package dev.auramc.auraskills.common;

import dev.auramc.auraskills.api.event.EventManager;
import dev.auramc.auraskills.common.ability.AbilityManager;
import dev.auramc.auraskills.common.ability.AbilityRegistry;
import dev.auramc.auraskills.common.config.ConfigProvider;
import dev.auramc.auraskills.common.hooks.HookManager;
import dev.auramc.auraskills.common.leveler.Leveler;
import dev.auramc.auraskills.common.leveler.XpRequirements;
import dev.auramc.auraskills.common.message.MessageKey;
import dev.auramc.auraskills.common.message.MessageProvider;
import dev.auramc.auraskills.common.message.PlatformLogger;
import dev.auramc.auraskills.common.skill.SkillRegistry;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.data.PlayerManager;
import dev.auramc.auraskills.common.item.ItemRegistry;
import dev.auramc.auraskills.common.leaderboard.LeaderboardManager;
import dev.auramc.auraskills.common.mana.ManaAbilityManager;
import dev.auramc.auraskills.common.mana.ManaAbilityRegistry;
import dev.auramc.auraskills.common.stat.StatManager;
import dev.auramc.auraskills.common.stat.StatRegistry;

import java.util.Locale;

public interface AuraSkillsPlugin {

    MessageProvider getMessageProvider();

    ConfigProvider getConfigProvider();

    AbilityManager getAbilityManager();

    ManaAbilityManager getManaAbilityManager();

    StatManager getStatManager();

    ItemRegistry getItemRegistry();

    Leveler getLeveler();

    PlayerManager getPlayerManager();

    XpRequirements getXpRequirements();

    EventManager getEventManager();

    PlatformLogger getLogger();

    SkillRegistry getSkillRegistry();

    StatRegistry getStatRegistry();

    AbilityRegistry getAbilityRegistry();

    ManaAbilityRegistry getManaAbilityRegistry();

    HookManager getHookManager();

    LeaderboardManager getLeaderboardManager();

    // Message convenience methods
    String getMsg(MessageKey key, Locale locale);

    default Locale getDefaultLanguage() {
        return getMessageProvider().getDefaultLanguage();
    }

    // Platform-dependent Minecraft methods
    void runConsoleCommand(String command);

    void runPlayerCommand(PlayerData playerData, String command);

}
