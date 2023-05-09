package dev.auramc.auraskills.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.config.Option;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.message.MessageBuilder;
import dev.auramc.auraskills.common.message.type.CommandMessage;
import dev.auramc.auraskills.common.util.math.NumberUtil;

import java.util.Locale;

@CommandAlias("mana")
public class ManaCommand extends BaseCommand {

    private final AuraSkillsPlugin plugin;

    public ManaCommand(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandPermission("aureliumskills.mana")
    @Description("Display your or another player's current and max mana")
    public void onMana(CommandIssuer issuer, @Flags("other") @CommandPermission("aureliumskills.mana.other") @Optional PlayerData playerData) {
        if (issuer.isPlayer()) { // Get issuer's own mana
            // Get the PlayerData of the issuer
            PlayerData issuerPlayerData = plugin.getPlayerManager().getPlayerData(issuer.getUniqueId());
            if (issuerPlayerData == null) return;
            Locale locale = issuerPlayerData.getLocale();

            MessageBuilder.create(plugin).to(issuer).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_DISPLAY,
                            "current", NumberUtil.format1(issuerPlayerData.getMana()),
                            "max", NumberUtil.format1(issuerPlayerData.getMaxMana()))
                    .send();
        } else if (playerData != null) { // Get target player's mana
            Locale locale = playerData.getLocale();

            MessageBuilder.create(plugin).to(issuer).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_DISPLAY_OTHER,
                            "player", playerData.getUsername(),
                            "current", NumberUtil.format1(playerData.getMana()),
                            "max", NumberUtil.format1(playerData.getMaxMana()))
                    .send();
        } else { // Player not specified
            Locale defLocale = plugin.getDefaultLanguage();

            MessageBuilder.create(plugin).to(issuer).locale(defLocale)
                    .prefix()
                    .message(CommandMessage.MANA_CONSOLE_SPECIFY_PLAYER)
                    .send();
        }
    }

    @Subcommand("add")
    @CommandPermission("aureliumskills.mana.add")
    @CommandCompletion("@players @nothing false|true")
    @Description("Adds mana to a player")
    public void onManaAdd(CommandIssuer issuer, @Flags("other") PlayerData playerData, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        Locale locale = playerData.getLocale();
        if (amount < 0) { // Validate amount
            if (!silent) {
                issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
            return;
        }
        // Calculate how much mana to add
        double manaToAdd = amount;
        if (playerData.getMaxMana() + manaToAdd > playerData.getMaxMana()) { // If adding mana will go over max mana
            if (!allowOverMax || !plugin.configBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) { // Should not go over max mana
                manaToAdd = playerData.getMaxMana() - playerData.getMana(); // Set mana to add to difference between max and current
            }
        }
        if (manaToAdd > 0) { // Add mana
            playerData.setMana(playerData.getMana() + manaToAdd);
        }
        if (!silent) { // Send message
            MessageBuilder.create(plugin).to(issuer).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_ADD,
                            "amount", NumberUtil.format2(manaToAdd),
                            "player", playerData.getUsername())
                    .send();
        }
    }

    @Subcommand("remove")
    @CommandPermission("aureliumskills.mana.remove")
    @CommandCompletion("@players")
    @Description("Removes mana from a player")
    public void onManaRemove(CommandIssuer issuer, @Flags("other") PlayerData playerData, double amount, @Default("false") boolean silent) {
        Locale locale = playerData.getLocale();
        if (amount < 0) { // Validate amount
            if (!silent) {
                MessageBuilder.create(plugin).to(issuer).locale(locale)
                        .prefix()
                        .message(CommandMessage.MANA_AT_LEAST_ZERO)
                        .send();
            }
            return;
        }
        double manaToRemove = amount;
        if (playerData.getMana() - manaToRemove < 0) { // If removing mana will go below 0
            manaToRemove = playerData.getMana(); // Set mana to remove to all current mana
        }
        playerData.setMana(playerData.getMana() - manaToRemove); // Remove mana

        if (!silent) { // Send message
            MessageBuilder.create(plugin).to(issuer).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_REMOVE,
                            "amount", NumberUtil.format2(manaToRemove),
                            "player", playerData.getUsername())
                    .send();
        }
    }

    @Subcommand("set")
    @CommandPermission("aureliumskills.mana.set")
    @CommandCompletion("@players @nothing false|true")
    @Description("Sets the mana of player")
    public void onManaSet(CommandIssuer issuer, @Flags("other") PlayerData playerData, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        if (playerData == null) return;
        Locale locale = playerData.getLocale();
        if (amount < 0) { // Validate amount
            if (!silent) {
                MessageBuilder.create(plugin).to(issuer).locale(locale)
                        .prefix()
                        .message(CommandMessage.MANA_AT_LEAST_ZERO)
                        .send();
            }
        }
        double manaToSet = amount;
        if (manaToSet > playerData.getMaxMana()) { // If setting mana will go over max mana
            if (!allowOverMax || !plugin.configBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) { // Should not go over max mana
                manaToSet = playerData.getMaxMana(); // Set mana to set to max mana
            }
        }
        playerData.setMana(manaToSet); // Set mana
        // Send message
        if (!silent) {
            MessageBuilder.create(plugin).to(issuer).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_SET,
                            "amount", NumberUtil.format2(manaToSet),
                            "player", playerData.getUsername())
                    .send();
        }
    }

}
