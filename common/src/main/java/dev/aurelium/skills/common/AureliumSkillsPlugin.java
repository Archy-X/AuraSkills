package dev.aurelium.skills.common;

import dev.aurelium.skills.api.event.EventManager;
import dev.aurelium.skills.common.ability.AbilityManager;
import dev.aurelium.skills.common.ability.AbilityRegistry;
import dev.aurelium.skills.common.hooks.HookManager;
import dev.aurelium.skills.common.mana.ManaAbilityRegistry;
import dev.aurelium.skills.common.config.ConfigProvider;
import dev.aurelium.skills.common.data.PlayerManager;
import dev.aurelium.skills.common.item.ItemRegistry;
import dev.aurelium.skills.common.leveler.Leveler;
import dev.aurelium.skills.common.leveler.XpRequirements;
import dev.aurelium.skills.common.mana.ManaAbilityManager;
import dev.aurelium.skills.common.message.MessageProvider;
import dev.aurelium.skills.common.message.PlatformLogger;
import dev.aurelium.skills.common.skill.SkillRegistry;
import dev.aurelium.skills.common.stat.StatManager;

public interface AureliumSkillsPlugin {

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

    AbilityRegistry getAbilityRegistry();

    ManaAbilityRegistry getManaAbilityRegistry();

    HookManager getHookManager();

}
