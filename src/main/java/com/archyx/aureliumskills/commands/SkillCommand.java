package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("skill")
public class SkillCommand extends BaseCommand {

    private final AureliumSkills plugin;

    public SkillCommand(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("setlevel")
    @CommandCompletion("@players @skills")
    @CommandPermission("aureliumskills.skill.setlevel")
    @Description("Sets a specific skill to a level for a player.")
    public void onSkillSetlevel(CommandSender sender, @Flags("other") Player player, Skill skill, int level) {
        Locale locale = plugin.getLang().getLocale(sender);
        if (OptionL.isEnabled(skill)) {
            if (level > 0) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                if (playerData == null) return;
                int oldLevel = playerData.getSkillLevel(skill);
                playerData.setSkillLevel(skill, level);
                playerData.setSkillXp(skill, 0);
                plugin.getLeveler().updateStats(player);
                plugin.getLeveler().updatePermissions(player);
                plugin.getLeveler().applyRevertCommands(player, skill, oldLevel, level);
                plugin.getLeveler().applyLevelUpCommands(player, skill, oldLevel, level);
                // Reload items and armor to check for newly met requirements
                this.plugin.getModifierManager().reloadPlayer(player);
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETLEVEL_SET, locale)
                        .replace("{skill}", skill.getDisplayName(locale))
                        .replace("{level}", String.valueOf(level))
                        .replace("{player}", player.getName()));
            } else {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETLEVEL_AT_LEAST_ONE, locale));
            }
        }
        else {
            sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
        }
    }

    @Subcommand("setall")
    @CommandCompletion("@players")
    @CommandPermission("aureliumskills.skill.setlevel")
    @Description("Sets all of a player's skills to a level.")
    public void onSkillSetall(CommandSender sender, @Flags("other") Player player, int level) {
        Locale locale = plugin.getLang().getLocale(sender);
        if (level > 0) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData == null) return;
            for (Skill skill : plugin.getSkillRegistry().getSkills()) {
                if (OptionL.isEnabled(skill)) {
                    int oldLevel = playerData.getSkillLevel(skill);
                    playerData.setSkillLevel(skill, level);
                    playerData.setSkillXp(skill, 0);
                    // Reload items and armor to check for newly met requirements
                    plugin.getModifierManager().reloadPlayer(player);
                    plugin.getLeveler().applyRevertCommands(player, skill, oldLevel, level);
                    plugin.getLeveler().applyLevelUpCommands(player, skill, oldLevel, level);
                }
            }
            plugin.getLeveler().updateStats(player);
            plugin.getLeveler().updatePermissions(player);
            sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETALL_SET, locale)
                    .replace("{level}", String.valueOf(level))
                    .replace("{player}", player.getName()));
        } else {
            sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETALL_AT_LEAST_ONE, locale));
        }
    }


    @Subcommand("reset")
    @CommandCompletion("@players @skills")
    @CommandPermission("aureliumskills.skill.reset")
    @Description("Resets all skills or a specific skill to level 1 for a player.")
    public void onSkillReset(CommandSender sender, @Flags("other") Player player, @Optional Skill skill) {
        Locale locale = plugin.getLang().getLocale(sender);
        if (skill != null) {
            if (OptionL.isEnabled(skill)) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                if (playerData == null) return;
                resetPlayerSkills(player, playerData, skill);
                // Reload items and armor to check for newly met requirements
                this.plugin.getModifierManager().reloadPlayer(player);
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_RESET_RESET_SKILL, locale)
                        .replace("{skill}", skill.getDisplayName(locale))
                        .replace("{player}", player.getName()));
            } else {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
            }
        }
        else {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData == null) return;
            for (Skill s : plugin.getSkillRegistry().getSkills()) {
                resetPlayerSkills(player, playerData, s);
            }
            sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_RESET_RESET_ALL, locale)
                    .replace("{player}", player.getName()));
        }
    }

    private void resetPlayerSkills(@Flags("other") Player player, PlayerData playerData, Skill skill) {
        int oldLevel = playerData.getSkillLevel(skill);
        playerData.setSkillLevel(skill, 1);
        playerData.setSkillXp(skill, 0);
        plugin.getLeveler().updateStats(player);
        plugin.getLeveler().updatePermissions(player);
        plugin.getLeveler().applyRevertCommands(player, skill, oldLevel, 1);
    }


}
