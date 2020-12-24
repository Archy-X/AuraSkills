package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.mana.ManaManager;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

@CommandAlias("mana")
public class ManaCommand extends BaseCommand {

    @Default
    @CommandPermission("aureliumskills.mana")
    @Description("Display your or another player's current and max mana")
    public void onMana(CommandSender sender, @Flags("other") @CommandPermission("aureliumskills.mana.other") @Optional Player player) {
        if (sender instanceof Player && player == null) {
            Player target = (Player) sender;
            Locale locale = Lang.getLanguage(target);
            UUID id = target.getUniqueId();
            ManaManager mana = AureliumSkills.manaManager;
            sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_DISPLAY, locale)
                    , "{current}", NumberUtil.format1(mana.getMana(id))
                    , "{max}", NumberUtil.format1(mana.getMaxMana(id))));
        } else if (player != null) {
            Locale locale = Lang.getLanguage(player);
            UUID id = player.getUniqueId();
            ManaManager mana = AureliumSkills.manaManager;
            sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_DISPLAY_OTHER, locale)
                    , "{player}", player.getName()
                    , "{current}", NumberUtil.format1(mana.getMana(id))
                    , "{max}", NumberUtil.format1(mana.getMaxMana(id))));
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(Lang.getDefaultLanguage()) + Lang.getMessage(CommandMessage.MANA_CONSOLE_SPECIFY_PLAYER, Lang.getDefaultLanguage()));
        }
    }

    @Subcommand("add")
    @CommandPermission("aureliumskills.mana.add")
    @CommandCompletion("@players @nothing false|true")
    @Description("Adds mana to a player")
    public void onManaAdd(CommandSender sender, @Flags("other") Player player, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        Locale locale = Lang.getLanguage(player);
        ManaManager mana = AureliumSkills.manaManager;
        UUID id = player.getUniqueId();
        if (amount >= 0) {
            if (allowOverMax && OptionL.getBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) {
                mana.setMana(id, mana.getMana(id) + amount);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                            , "{amount}", NumberUtil.format2(amount)
                            , "{player}", player.getName()));
                }
            } else {
                if (mana.getMana(id) + amount <= mana.getMaxMana(id)) {
                    mana.setMana(id, mana.getMana(id) + amount);
                    if (!silent) {
                        sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                                , "{amount}", NumberUtil.format2(amount)
                                , "{player}", player.getName()));
                    }
                } else {
                    double added = mana.getMaxMana(id) - mana.getMana(id);
                    if (added >= 0) {
                        mana.setMana(id, mana.getMaxMana(id));
                        if (!silent) {
                            sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                                    , "{amount}", NumberUtil.format2(added)
                                    , "{player}", player.getName()));
                        }
                    } else {
                        if (!silent) {
                            sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_ADD, locale)
                                    , "{amount}", String.valueOf(0)
                                    , "{player}", player.getName()));
                        }
                    }
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
        }
    }

    @Subcommand("remove")
    @CommandPermission("aureliumskills.mana.remove")
    @CommandCompletion("@players")
    @Description("Removes mana from a player")
    public void onManaRemove(CommandSender sender, @Flags("other") Player player, double amount, @Default("false") boolean silent) {
        Locale locale = Lang.getLanguage(player);
        ManaManager mana = AureliumSkills.manaManager;
        UUID id = player.getUniqueId();
        if (amount >= 0) {
            if (mana.getMana(id) - amount >= 0) {
                mana.setMana(id, mana.getMana(id) - amount);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_REMOVE, locale)
                            , "{amount}", NumberUtil.format2(amount)
                            , "{player}", player.getName()));
                }
            } else {
                double removed = mana.getMana(id);
                mana.setMana(id, 0);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_REMOVE, locale)
                            , "{amount}", NumberUtil.format2(removed)
                            , "{player}", player.getName()));
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
        }
    }

    @Subcommand("set")
    @CommandPermission("aureliumskills.mana.set")
    @CommandCompletion("@players @nothing false|true")
    @Description("Sets the mana of player")
    public void onManaSet(CommandSender sender, @Flags("other") Player player, double amount, @Default("true") boolean allowOverMax, @Default("false") boolean silent) {
        Locale locale = Lang.getLanguage(player);
        ManaManager mana = AureliumSkills.manaManager;
        UUID id = player.getUniqueId();
        if (amount >= 0) {
            if (allowOverMax && OptionL.getBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) {
                mana.setMana(id, amount);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_SET, locale)
                            , "{amount}", NumberUtil.format2(amount)
                            , "{player}", player.getName()));
                }
            } else {
                if (amount <= mana.getMaxMana(id)) {
                    mana.setMana(id, amount);
                    if (!silent) {
                        sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_SET, locale)
                                , "{amount}", NumberUtil.format2(amount)
                                , "{player}", player.getName()));
                    }
                } else {
                    mana.setMana(id, mana.getMaxMana(id));
                    if (!silent) {
                        sender.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(CommandMessage.MANA_SET, locale)
                                , "{amount}", NumberUtil.format2(mana.getMaxMana(id))
                                , "{player}", player.getName()));
                    }
                }
            }
        } else {
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MANA_AT_LEAST_ZERO, locale));
            }
        }
    }

}
