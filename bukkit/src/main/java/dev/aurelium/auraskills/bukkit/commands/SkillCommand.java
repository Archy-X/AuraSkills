package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("skill")
public class SkillCommand extends BaseCommand {

    private final AuraSkills plugin;

    public SkillCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("setlevel")
    @CommandCompletion("@players @skills")
    @CommandPermission("auraskills.command.skill.setlevel")
    @Description("%desc_skill_setlevel")
    public void onSkillSetlevel(CommandSender sender, @Flags("other") Player player, Skill skill, int level) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (skill.isEnabled()) {
            int startLevel = plugin.config().getStartLevel();
            if (level >= startLevel) {
                int oldLevel = user.getSkillLevel(skill);
                user.setSkillLevel(skill, level);
                user.setSkillXp(skill, 0);
                plugin.getStatManager().recalculateStats(user);
                plugin.getRewardManager().updatePermissions(user);
                plugin.getRewardManager().applyRevertCommands(user, skill, oldLevel, level);
                plugin.getRewardManager().applyLevelUpCommands(user, skill, oldLevel, level);
                // Reload items and armor to check for newly met requirements
                plugin.getModifierManager().applyModifiers(player, true);
                sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.SKILL_SETLEVEL_SET, locale)
                        .replace("{skill}", skill.getDisplayName(locale))
                        .replace("{level}", String.valueOf(level))
                        .replace("{player}", player.getName()));
            } else {
                sender.sendMessage(MessageBuilder.create(plugin).locale(locale)
                        .prefix()
                        .message(CommandMessage.SKILL_AT_LEAST, "level", String.valueOf(startLevel))
                        .toString());
            }
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

    @Subcommand("addlevel")
    @CommandCompletion("@players @skills")
    @CommandPermission("auraskills.command.skill.setlevel")
    @Description("%desc_skill_addlevel")
    public void onSkillAddlevel(CommandSender sender, @Flags("other") Player player, Skill skill, int level) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (skill.isEnabled()) {
            int oldLevel = user.getSkillLevel(skill);
            level = oldLevel + level;
            this.onSkillSetlevel(sender, player, skill, level);
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

    @Subcommand("setall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.skill.setlevel")
    @Description("%desc_skill_setall")
    public void onSkillSetall(CommandSender sender, @Flags("other") Player player, int level) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        int startLevel = plugin.config().getStartLevel();
        if (level >= startLevel) {
            for (Skill skill : plugin.getSkillRegistry().getValues()) {
                if (skill.isEnabled()) {
                    int oldLevel = user.getSkillLevel(skill);
                    user.setSkillLevel(skill, level);
                    user.setSkillXp(skill, 0);

                    plugin.getRewardManager().applyRevertCommands(user, skill, oldLevel, level);
                    plugin.getRewardManager().applyLevelUpCommands(user, skill, oldLevel, level);
                }
            }
            plugin.getStatManager().recalculateStats(user);
            plugin.getModifierManager().applyModifiers(player, true);
            plugin.getRewardManager().updatePermissions(user);

            sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.SKILL_SETALL_SET, locale)
                    .replace("{level}", String.valueOf(level))
                    .replace("{player}", player.getName()));
        } else {
            sender.sendMessage(MessageBuilder.create(plugin).locale(locale)
                    .prefix()
                    .message(CommandMessage.SKILL_AT_LEAST, "level", String.valueOf(startLevel))
                    .toString());
        }
    }

    @Subcommand("reset")
    @CommandCompletion("@players @skills")
    @CommandPermission("auraskills.command.skill.reset")
    @Description("%desc_skill_reset")
    public void onSkillReset(CommandSender sender, @Flags("other") Player player, @Optional Skill skill) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (skill != null) {
            if (skill.isEnabled()) {
                int level = user.resetSkill(skill);
                // Reload items and armor to check for newly met requirements
                plugin.getModifierManager().applyModifiers(player, true);
                sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.SKILL_SETLEVEL_SET, locale)
                        .replace("{skill}", skill.getDisplayName(locale))
                        .replace("{level}", String.valueOf(level))
                        .replace("{player}", player.getName()));
            } else {
                sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.UNKNOWN_SKILL, locale));
            }
        } else {
            int level = plugin.config().getStartLevel();
            for (Skill s : plugin.getSkillRegistry().getValues()) {
                level = user.resetSkill(s);
            }
            plugin.getModifierManager().applyModifiers(player, true);
            sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.SKILL_SETALL_SET, locale)
                    .replace("{level}", String.valueOf(level))
                    .replace("{player}", player.getName()));
        }
    }

}
