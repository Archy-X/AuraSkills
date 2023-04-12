package dev.aurelium.skills.common;

import dev.aurelium.skills.common.ability.AbilityManager;
import dev.aurelium.skills.common.config.ConfigProvider;
import dev.aurelium.skills.common.item.ItemRegistry;
import dev.aurelium.skills.common.leveler.Leveler;
import dev.aurelium.skills.common.mana.ManaAbilityManager;
import dev.aurelium.skills.common.message.MessageProvider;
import dev.aurelium.skills.common.registry.RegistryManager;
import dev.aurelium.skills.common.stat.StatManager;

public interface AureliumSkillsPlugin {

    MessageProvider getMessageProvider();

    RegistryManager getRegistryManager();

    ConfigProvider getConfigProvider();

    AbilityManager getAbilityManager();

    ManaAbilityManager getManaAbilityManager();

    StatManager getStatManager();

    ItemRegistry getItemRegistry();

    Leveler getLeveler();

}
