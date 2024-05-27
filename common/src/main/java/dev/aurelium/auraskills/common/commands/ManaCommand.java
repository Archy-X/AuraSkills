package dev.aurelium.auraskills.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;

import java.util.Locale;

@CommandAlias("mana")
public class ManaCommand extends BaseCommand {

    private final AuraSkillsPlugin plugin;

    public ManaCommand(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandPermission("auraskills.command.mana")
    @Description("Display your or another player's current and max mana")
    public void onMana(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.mana.other") @Optional User user) {
        if (issuer.isPlayer()) { // Get issuer's own mana
            // Get the PlayerData of the issuer
            User issuerUser = plugin.getUserManager().getUser(issuer.getUniqueId());
            if (issuerUser == null) return;
            Locale locale = issuerUser.getLocale();

            MessageBuilder.create(plugin).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_DISPLAY,
                            "current", NumberUtil.format1(issuerUser.getMana()),
                            "max", NumberUtil.format1(issuerUser.getMaxMana()))
                    .send(issuer);
        } else if (user != null) { // Get target player's mana
            Locale locale = user.getLocale();

            MessageBuilder.create(plugin).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_DISPLAY_OTHER,
                            "player", user.getUsername(),
                            "current", NumberUtil.format1(user.getMana()),
                            "max", NumberUtil.format1(user.getMaxMana()))
                    .send(issuer);
        } else { // Player not specified
            Locale defLocale = plugin.getDefaultLanguage();

            MessageBuilder.create(plugin).locale(defLocale)
                    .prefix()
                    .message(CommandMessage.MANA_CONSOLE_SPECIFY_PLAYER)
                    .send(issuer);
        }
    }

    @Subcommand("add")
    @CommandPermission("auraskills.command.mana.add")
    @CommandCompletion("@players @nothing false|true")
    @Description("Adds mana to a player")
    public void onManaAdd(CommandIssuer issuer, @Flags("other") User user, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        Locale locale = user.getLocale();
        if (amount < 0) { // Validate amount
            if (!silent) {
                issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
            return;
        }
        // Calculate how much mana to add
        double manaToAdd = amount;
        if (user.getMana() + manaToAdd > user.getMaxMana()) { // If adding mana will go over max mana
            boolean allowOverflow = Traits.MAX_MANA.isEnabled() && Traits.MAX_MANA.optionBoolean("allow_overflow");
            if (!allowOverMax || !allowOverflow) { // Should not go over max mana
                manaToAdd = user.getMaxMana() - user.getMana(); // Set mana to add to difference between max and current
            }
        }
        if (manaToAdd > 0) { // Add mana
            user.setMana(user.getMana() + manaToAdd);
        }
        if (!silent) { // Send message
            MessageBuilder.create(plugin).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_ADD,
                            "amount", NumberUtil.format2(manaToAdd),
                            "player", user.getUsername())
                    .send(issuer);
        }
    }

    @Subcommand("remove")
    @CommandPermission("auraskills.command.mana.remove")
    @CommandCompletion("@players")
    @Description("Removes mana from a player")
    public void onManaRemove(CommandIssuer issuer, @Flags("other") User user, double amount, @Default("false") boolean silent) {
        Locale locale = user.getLocale();
        if (amount < 0) { // Validate amount
            if (!silent) {
                MessageBuilder.create(plugin).locale(locale)
                        .prefix()
                        .message(CommandMessage.MANA_AT_LEAST_ZERO)
                        .send(issuer);
            }
            return;
        }
        double manaToRemove = amount;
        if (user.getMana() - manaToRemove < 0) { // If removing mana will go below 0
            manaToRemove = user.getMana(); // Set mana to remove to all current mana
        }
        user.setMana(user.getMana() - manaToRemove); // Remove mana

        if (!silent) { // Send message
            MessageBuilder.create(plugin).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_REMOVE,
                            "amount", NumberUtil.format2(manaToRemove),
                            "player", user.getUsername())
                    .send(issuer);
        }
    }

    @Subcommand("set")
    @CommandPermission("auraskills.command.mana.set")
    @CommandCompletion("@players @nothing false|true")
    @Description("Sets the mana of player")
    public void onManaSet(CommandIssuer issuer, @Flags("other") User user, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        if (user == null) return;
        Locale locale = user.getLocale();
        if (amount < 0) { // Validate amount
            if (!silent) {
                MessageBuilder.create(plugin).locale(locale)
                        .prefix()
                        .message(CommandMessage.MANA_AT_LEAST_ZERO)
                        .send(issuer);
            }
        }
        double manaToSet = amount;
        if (manaToSet > user.getMaxMana()) { // If setting mana will go over max mana
            if (!allowOverMax || !Traits.MAX_MANA.optionBoolean("allow_overflow", false)) { // Should not go over max mana
                manaToSet = user.getMaxMana(); // Set mana to set to max mana
            }
        }
        user.setMana(manaToSet); // Set mana
        // Send message
        if (!silent) {
            MessageBuilder.create(plugin).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_SET,
                            "amount", NumberUtil.format2(manaToSet),
                            "player", user.getUsername())
                    .send(issuer);
        }
    }

}
