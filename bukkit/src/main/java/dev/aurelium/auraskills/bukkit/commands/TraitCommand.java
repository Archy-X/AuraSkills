package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.stat.StatFormat;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("trait")
public class TraitCommand extends BaseCommand {

    private final AuraSkills plugin;
    private final StatFormat format;

    public TraitCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.format = new StatFormat(plugin);
    }

    @Subcommand("add")
    @CommandPermission("auraskills.command.modifier")
    @CommandCompletion("@players @traits @nothing @nothing true true")
    @Description("Adds a trait modifier to a player.")
    public void onAdd(CommandSender sender, @Flags("other") Player player, Trait trait, String name, double value, @Default("false") boolean silent, @Default("false") boolean stack) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        TraitModifier modifier = new TraitModifier(name, trait, value);
        if (!user.getTraitModifiers().containsKey(name)) {
            user.addTraitModifier(modifier);
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_ADD_ADDED, locale), modifier, player, locale));
            }
        } else if (stack) { // Stack modifier by using a numbered name
            String newModifierName = ModifierCommand.getStackedName(user.getTraitModifiers().keySet(), name);
            TraitModifier newModifier = new TraitModifier(newModifierName, trait, value);
            user.addTraitModifier(newModifier);
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_ADD_ADDED, locale), newModifier, player, locale));
            }
        } else {
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.TRAIT_ADD_ALREADY_EXISTS, locale), modifier, player, locale));
            }
        }
    }

    @Subcommand("remove")
    @CommandPermission("auraskills.command.modifier")
    @CommandCompletion("@players @modifiers true")
    @Description("Removes a specific trait modifier from a player.")
    public void onRemove(CommandSender sender, @Flags("other") Player player, String name, @Default("false") boolean silent) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (user.removeTraitModifier(name)) {
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_REMOVE_REMOVED, locale), name, player));
            }
        } else {
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_REMOVE_NOT_FOUND, locale), name, player));
            }
        }
    }

    @Subcommand("list")
    @CommandCompletion("@players @traits")
    @CommandPermission("auraskills.command.modifier")
    @Description("Lists all or a specific trait's modifiers for a player.")
    public void onList(CommandSender sender, @Flags("other") @Optional Player player, @Optional Trait trait) {
        Locale locale = plugin.getLocale(sender);
        if (player == null) {
            if (sender instanceof Player target) {
                listModifiers(sender, target, trait, locale);
            } else {
                sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.MODIFIER_LIST_PLAYERS_ONLY, locale));
            }
        } else {
            listModifiers(sender, player, trait, locale);
        }
    }

    private void listModifiers(CommandSender sender, @Optional @Flags("other") Player player, @Optional Trait trait, Locale locale) {
        User user = plugin.getUser(player);
        StringBuilder message;
        if (trait == null) {
            message = new StringBuilder(format.applyPlaceholders(plugin.getMsg(CommandMessage.TRAIT_LIST_ALL_TRAITS_HEADER, locale), player));
            for (String key : user.getTraitModifiers().keySet()) {
                TraitModifier modifier = user.getTraitModifiers().get(key);
                message.append("\n").append(format.applyPlaceholders(plugin.getRawMsg(CommandMessage.MODIFIER_LIST_ALL_STATS_ENTRY, locale), modifier, player, locale));
            }
        } else {
            message = new StringBuilder(format.applyPlaceholders(plugin.getRawMsg(CommandMessage.TRAIT_LIST_ONE_TRAIT_HEADER, locale), trait, player, locale));
            for (String key : user.getTraitModifiers().keySet()) {
                TraitModifier modifier = user.getTraitModifiers().get(key);
                if (modifier.trait().equals(trait)) {
                    message.append("\n").append(format.applyPlaceholders(plugin.getRawMsg(CommandMessage.MODIFIER_LIST_ONE_STAT_ENTRY, locale), modifier, player, locale));
                }
            }
        }
        sender.sendMessage(message.toString());
    }

    @Subcommand("removeall")
    @CommandCompletion("@players @traits")
    @CommandPermission("auraskills.command.modifier")
    @Description("Removes all trait modifiers from a player.")
    public void onRemoveAll(CommandSender sender, @Flags("other") @Optional Player player, @Optional Trait trait, @Default("false") boolean silent) {
        Locale locale = plugin.getLocale(sender);
        if (player == null) {
            if (sender instanceof Player target) {
                removeAllModifiers(sender, trait, silent, locale, target, plugin.getUser(target));
            } else {
                if (!silent) {
                    sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.MODIFIER_REMOVEALL_PLAYERS_ONLY, locale));
                }
            }
        } else {
            removeAllModifiers(sender, trait, silent, locale, player, plugin.getUser(player));
        }
    }

    private void removeAllModifiers(CommandSender sender, @Optional Trait trait, @Default("false") boolean silent, Locale locale, Player target, User user) {
        int removed = 0;
        List<String> toRemove = new ArrayList<>();
        for (String key : user.getTraitModifiers().keySet()) {
            if (trait == null) {
                toRemove.add(key);
                removed++;
            } else if (user.getTraitModifiers().get(key).trait().equals(trait)) {
                toRemove.add(key);
                removed++;
            }
        }
        for (String key : toRemove) {
            user.removeTraitModifier(key);
        }
        if (!silent) {
            if (trait == null) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.TRAIT_REMOVEALL_REMOVED_ALL_TRAITS, locale), target).replace("{num}", String.valueOf(removed)));
            } else {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_REMOVEALL_REMOVED_ONE_STAT, locale), trait, target, locale).replace("{num}", String.valueOf(removed)));
            }
        }
    }

}
