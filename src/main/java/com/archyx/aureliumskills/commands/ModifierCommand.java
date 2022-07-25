package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.math.NumberUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@CommandAlias("%skills_alias")
@Subcommand("modifier")
public class ModifierCommand extends BaseCommand {

    private final AureliumSkills plugin;

    public ModifierCommand(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("add")
    @CommandPermission("aureliumskills.modifier.add")
    @CommandCompletion("@players @stats @nothing @nothing true true")
    @Description("Adds a stat modifier to a player.")
    public void onAdd(CommandSender sender, @Flags("other") Player player, Stat stat, String name, double value, @Default("false") boolean silent, @Default("false") boolean stack) {
        Locale locale = plugin.getLang().getLocale(sender);
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            StatModifier modifier = new StatModifier(name, stat, value);
            if (!playerData.getStatModifiers().containsKey(name)) {
                playerData.addStatModifier(modifier);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_ADD_ADDED, locale), modifier, player, locale));
                }
            } else if (stack) { // Stack modifier by using a numbered name
                Set<String> modifierNames = playerData.getStatModifiers().keySet();
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

                String newModifierName = name + "(" + newStackNumber + ")";
                StatModifier newModifier = new StatModifier(newModifierName, stat, value);
                playerData.addStatModifier(newModifier);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_ADD_ADDED, locale), newModifier, player, locale));
                }
            } else {
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_ADD_ALREADY_EXISTS, locale), modifier, player, locale));
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
            }
        }
    }

    @Subcommand("remove")
    @CommandPermission("aureliumskills.modifier.remove")
    @CommandCompletion("@players @modifiers true")
    @Description("Removes a specific stat modifier from a player.")
    public void onRemove(CommandSender sender, @Flags("other") Player player, String name, @Default("false") boolean silent) {
        Locale locale = plugin.getLang().getLocale(sender);
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            if (playerData.removeStatModifier(name)) {
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_REMOVE_REMOVED, locale), name, player));
                }
            }
            else {
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_REMOVE_NOT_FOUND, locale), name, player));
                }
            }
        }
        else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
            }
        }
    }

    @Subcommand("list")
    @CommandCompletion("@players @stats")
    @CommandPermission("aureliumskills.modifier.list")
    @Description("Lists all or a specific stat's modifiers for a player.")
    public void onList(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat) {
        Locale locale = plugin.getLang().getLocale(sender);
        if (player == null) {
            if (sender instanceof Player) {
                Player target = (Player) sender;
                listModifiers(sender, target, stat, locale);
            }
            else {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MODIFIER_LIST_PLAYERS_ONLY, locale));
            }
        }
        else {
            listModifiers(sender, player, stat, locale);
        }
    }

    private void listModifiers(CommandSender sender, @Optional @Flags("other") Player player, @Optional Stat stat, Locale locale) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            StringBuilder message;
            if (stat == null) {
                message = new StringBuilder(StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ALL_STATS_HEADER, locale), player));
                for (String key : playerData.getStatModifiers().keySet()) {
                    StatModifier modifier = playerData.getStatModifiers().get(key);
                    message.append("\n").append(StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ALL_STATS_ENTRY, locale), modifier, player, locale));
                }
            } else {
                message = new StringBuilder(StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ONE_STAT_HEADER, locale), stat, player, locale));
                for (String key : playerData.getStatModifiers().keySet()) {
                    StatModifier modifier = playerData.getStatModifiers().get(key);
                    if (modifier.getStat() == stat) {
                        message.append("\n").append(StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ONE_STAT_ENTRY, locale), modifier, player, locale));
                    }
                }
            }
            sender.sendMessage(message.toString());
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
        }
    }

    @Subcommand("removeall")
    @CommandCompletion("@players @stats")
    @CommandPermission("aureliumskills.modifier.removeall")
    @Description("Removes all stat modifiers from a player.")
    public void onRemoveAll(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat, @Default("false") boolean silent) {
        Locale locale = plugin.getLang().getLocale(sender);
        if (player == null) {
            if (sender instanceof Player) {
                Player target = (Player) sender;
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(target);
                removeAllModifiers(sender, stat, silent, locale, target, playerData);
            }
            else {
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MODIFIER_REMOVEALL_PLAYERS_ONLY, locale));
                }
            }
        }
        else {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            removeAllModifiers(sender, stat, silent, locale, player, playerData);
        }
    }

    private void removeAllModifiers(CommandSender sender, @Optional Stat stat, @Default("false") boolean silent, Locale locale, Player target, PlayerData playerData) {
        if (playerData != null) {
            int removed = 0;
            List<String> toRemove = new ArrayList<>();
            for (String key : playerData.getStatModifiers().keySet()) {
                if (stat == null) {
                    toRemove.add(key);
                    removed++;
                }
                else if (playerData.getStatModifiers().get(key).getStat() == stat) {
                    toRemove.add(key);
                    removed++;
                }
            }
            for (String key : toRemove) {
                playerData.removeStatModifier(key);
            }
            if (!silent) {
                if (stat == null) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_REMOVEALL_REMOVED_ALL_STATS, locale), target).replace("{num}", String.valueOf(removed)));
                }
                else {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_REMOVEALL_REMOVED_ONE_STAT, locale), stat, target, locale).replace("{num}", String.valueOf(removed)));
                }
            }
        }
        else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
            }
        }
    }

}
