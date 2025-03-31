package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum TraitModifiers {

    BOUNTIFUL_HARVEST("farming_luck_ability"),
    LUMBERJACK("foraging_luck_ability"),
    LUCKY_MINER("mining_luck_ability"),
    LUCKY_CATCH("fishing_luck_ability"),
    BIGGER_SCOOP("excavation_luck_ability"),
    FLEETING("auraskills/fleeting");

    private final String id;

    TraitModifiers(String id) {
        this.id = id;
    }

    @Nullable
    public static TraitModifiers fromId(String id) {
        for (TraitModifiers instance : TraitModifiers.values()) {
            if (instance.getId().equals(id)) {
                return instance;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getNameMessage(AuraSkills plugin, Locale locale) {
        return plugin.getMsg(MessageKey.of("abilities." + this.toString().toLowerCase(Locale.ROOT) + ".name"), locale);
    }

    public String getDescriptionMessage(AuraSkills plugin, Locale locale) {
        return plugin.getMsg(MenuMessage.ABILITY_MODIFIER_DESC, locale).replace("{ability}", getNameMessage(plugin, locale));
    }

}
