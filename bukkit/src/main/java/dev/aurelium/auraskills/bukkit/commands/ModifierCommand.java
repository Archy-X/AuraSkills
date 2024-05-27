package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.stat.StatFormat;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@CommandAlias("%skills_alias")
@Subcommand("modifier")
public class ModifierCommand extends BaseCommand {

    private final AuraSkills plugin;
    private final StatFormat format;

    public ModifierCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.format = new StatFormat(plugin);
    }

    @Subcommand("add")
    @CommandPermission("auraskills.command.modifier")
    @CommandCompletion("@players @stats @nothing @nothing true true")
    @Description("Adds a stat modifier to a player.")
    public void onAdd(CommandSender sender, @Flags("other") Player player, Stat stat, String name, double value, @Default("false") boolean silent, @Default("false") boolean stack) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        StatModifier modifier = new StatModifier(name, stat, value);
        if (!user.getStatModifiers().containsKey(name)) {
            user.addStatModifier(modifier);
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_ADD_ADDED, locale), modifier, player, locale));
            }
        } else if (stack) { // Stack modifier by using a numbered name
            String newModifierName = getStackedName(user.getStatModifiers().keySet(), name);
            StatModifier newModifier = new StatModifier(newModifierName, stat, value);
            user.addStatModifier(newModifier);
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_ADD_ADDED, locale), newModifier, player, locale));
            }
        } else {
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_ADD_ALREADY_EXISTS, locale), modifier, player, locale));
            }
        }
    }

    public static String getStackedName(Set<String> modifierNames, String name) {
        int lastStackNumber = 1;
        for (String modifierName : modifierNames) { // Find the previous highest stack number
            if (modifierName.startsWith(name)) {
                String endName = modifierName.substring(name.length()); // Get the part of the string after name
                if (endName.startsWith("(") && endName.endsWith(")")) {
                    String numberString = endName.substring(1, endName.length() - 1); // String without first and last chars
                    int stackNumber = NumberUtil.toInt(numberString);
                    if (stackNumber > lastStackNumber) {
                        lastStackNumber = stackNumber;
                    }
                }
            }
        }
        int newStackNumber = lastStackNumber + 1;
        return name + "(" + newStackNumber + ")";
    }

    @Subcommand("remove")
    @CommandPermission("auraskills.command.modifier")
    @CommandCompletion("@players @modifiers true")
    @Description("Removes a specific stat modifier from a player.")
    public void onRemove(CommandSender sender, @Flags("other") Player player, String name, @Default("false") boolean silent) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (user.removeStatModifier(name)) {
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
    @CommandCompletion("@players @stats")
    @CommandPermission("auraskills.command.modifier")
    @Description("Lists all or a specific stat's modifiers for a player.")
    public void onList(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat) {
        Locale locale = plugin.getLocale(sender);
        if (player == null) {
            if (sender instanceof Player target) {
                listModifiers(sender, target, stat, locale);
            } else {
                sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.MODIFIER_LIST_PLAYERS_ONLY, locale));
            }
        } else {
            listModifiers(sender, player, stat, locale);
        }
    }

    private void listModifiers(CommandSender sender, @Optional @Flags("other") Player player, @Optional Stat stat, Locale locale) {
        User user = plugin.getUser(player);
        StringBuilder message;
        if (stat == null) {
            message = new StringBuilder(format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_LIST_ALL_STATS_HEADER, locale), player));
            for (String key : user.getStatModifiers().keySet()) {
                StatModifier modifier = user.getStatModifiers().get(key);
                message.append("\n").append(format.applyPlaceholders(plugin.getRawMsg(CommandMessage.MODIFIER_LIST_ALL_STATS_ENTRY, locale), modifier, player, locale));
            }
        } else {
            message = new StringBuilder(format.applyPlaceholders(plugin.getRawMsg(CommandMessage.MODIFIER_LIST_ONE_STAT_HEADER, locale), stat, player, locale));
            for (String key : user.getStatModifiers().keySet()) {
                StatModifier modifier = user.getStatModifiers().get(key);
                if (modifier.stat() == stat) {
                    message.append("\n").append(format.applyPlaceholders(plugin.getRawMsg(CommandMessage.MODIFIER_LIST_ONE_STAT_ENTRY, locale), modifier, player, locale));
                }
            }
        }
        sender.sendMessage(message.toString());
    }

    @Subcommand("removeall")
    @CommandCompletion("@players @stats")
    @CommandPermission("auraskills.command.modifier")
    @Description("Removes all stat modifiers from a player.")
    public void onRemoveAll(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat, @Default("false") boolean silent) {
        Locale locale = plugin.getLocale(sender);
        if (player == null) {
            if (sender instanceof Player target) {
                removeAllModifiers(sender, stat, silent, locale, target, plugin.getUser(target));
            } else {
                if (!silent) {
                    sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.MODIFIER_REMOVEALL_PLAYERS_ONLY, locale));
                }
            }
        } else {
            removeAllModifiers(sender, stat, silent, locale, player, plugin.getUser(player));
        }
    }

    private void removeAllModifiers(CommandSender sender, @Optional Stat stat, @Default("false") boolean silent, Locale locale, Player target, User playerData) {
        if (playerData != null) {
            int removed = 0;
            List<String> toRemove = new ArrayList<>();
            for (String key : playerData.getStatModifiers().keySet()) {
                if (stat == null) {
                    toRemove.add(key);
                    removed++;
                }
                else if (playerData.getStatModifiers().get(key).stat() == stat) {
                    toRemove.add(key);
                    removed++;
                }
            }
            for (String key : toRemove) {
                playerData.removeStatModifier(key);
            }
            if (!silent) {
                if (stat == null) {
                    sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_REMOVEALL_REMOVED_ALL_STATS, locale), target).replace("{num}", String.valueOf(removed)));
                } else {
                    sender.sendMessage(plugin.getPrefix(locale) + format.applyPlaceholders(plugin.getMsg(CommandMessage.MODIFIER_REMOVEALL_REMOVED_ONE_STAT, locale), stat, target, locale).replace("{num}", String.valueOf(removed)));
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.NO_PROFILE, locale));
            }
        }
    }

}
