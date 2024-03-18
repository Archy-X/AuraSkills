package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("xp")
public class XpCommand extends BaseCommand {

    private final AuraSkills plugin;

    public XpCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("add")
    @CommandCompletion("@players @skills")
    @CommandPermission("auraskills.command.xp.add")
    @Description("Adds skill XP to a player for a certain skill.")
    public void onXpAdd(CommandSender sender, @Flags("other") Player player, Skill skill, double amount, @Default("false") boolean silent) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (skill.isEnabled()) {
            plugin.getLevelManager().addXp(user, skill, null, amount);
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.XP_ADD, locale),
                        "{amount}", String.valueOf(amount),
                        "{skill}", skill.getDisplayName(locale),
                        "{player}", player.getName()));
            }
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + ChatColor.YELLOW + plugin.getMsg(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

    @Subcommand("set")
    @CommandCompletion("@players @skills")
    @CommandPermission("auraskills.command.xp.set")
    @Description("Sets a player's skill XP for a certain skill to an amount.")
    public void onXpSet(CommandSender sender, @Flags("other") Player player, Skill skill, double amount, @Default("false") boolean silent) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (skill.isEnabled()) {
            plugin.getLevelManager().setXp(user, skill, amount);
            if (!silent) {
                sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.XP_SET, locale),
                        "{amount}", String.valueOf(amount),
                        "{skill}", skill.getDisplayName(locale),
                        "{player}", player.getName()));
            }
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + ChatColor.YELLOW + plugin.getMsg(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

    @Subcommand("remove")
    @CommandCompletion("@players @skills")
    @CommandPermission("auraskills.command.xp.remove")
    @Description("Removes skill XP from a player in a certain skill.")
    public void onXpRemove(CommandSender sender, @Flags("other") Player player, Skill skill, double amount, @Default("false") boolean silent) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (skill.isEnabled()) {
            if (user.getSkillXp(skill) - amount >= 0) {
                plugin.getLevelManager().setXp(user, skill, user.getSkillXp(skill) - amount);
                if (!silent) {
                    sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.XP_REMOVE, locale),
                            "{amount}", String.valueOf(amount),
                            "{skill}", skill.getDisplayName(locale),
                            "{player}", player.getName()));
                }
            } else {
                if (!silent) {
                    sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.XP_REMOVE, locale),
                            "{amount}", String.valueOf(user.getSkillXp(skill)),
                            "{skill}", skill.getDisplayName(locale),
                            "{player}", player.getName()));
                }
                plugin.getLevelManager().setXp(user, skill, 0);
            }
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + ChatColor.YELLOW + plugin.getMsg(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

}
