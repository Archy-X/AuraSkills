package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("xp")
public class XpCommand extends BaseCommand {

    private final AureliumSkills plugin;

    public XpCommand(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("add")
    @CommandCompletion("@players @skills")
    @CommandPermission("aureliumskills.xp.add")
    @Description("Adds skill XP to a player for a certain skill.")
    public void onXpAdd(CommandSender sender, @Flags("other") Player player, Skill skill, double amount, @Default("false") boolean silent) {
        Locale locale = plugin.getLang().getLocale(player);
        if (OptionL.isEnabled(skill)) {
            plugin.getLeveler().addXp(player, skill, amount);
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_ADD, locale).replace("{amount}", String.valueOf(amount)).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
            }
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.YELLOW + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

    @Subcommand("set")
    @CommandCompletion("@players @skills")
    @CommandPermission("aureliumskills.xp.set")
    @Description("Sets a player's skill XP for a certain skill to an amount.")
    public void onXpSet(CommandSender sender, @Flags("other") Player player, Skill skill, double amount, @Default("false") boolean silent) {
        Locale locale = plugin.getLang().getLocale(player);
        if (OptionL.isEnabled(skill)) {
            plugin.getLeveler().setXp(player, skill, amount);
            if (!silent) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_SET, locale).replace("{amount}", String.valueOf(amount)).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
            }
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.YELLOW + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

    @Subcommand("remove")
    @CommandCompletion("@players @skills")
    @CommandPermission("aureliumskills.xp.remove")
    @Description("Removes skill XP from a player in a certain skill.")
    public void onXpRemove(CommandSender sender, @Flags("other") Player player, Skill skill, double amount, @Default("false") boolean silent) {
        Locale locale = plugin.getLang().getLocale(player);
        if (OptionL.isEnabled(skill)) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData == null) return;
            if (playerData.getSkillXp(skill) - amount >= 0) {
                plugin.getLeveler().setXp(player, skill, playerData.getSkillXp(skill) - amount);
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_REMOVE, locale).replace("{amount}", String.valueOf(amount)).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
                }
            } else {
                if (!silent) {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_REMOVE, locale).replace("{amount}", String.valueOf(playerData.getSkillXp(skill))).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
                }
                plugin.getLeveler().setXp(player, skill, 0);
            }
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.YELLOW + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

}
