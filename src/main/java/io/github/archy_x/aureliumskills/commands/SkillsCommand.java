package io.github.archy_x.aureliumskills.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Lang;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.menu.SkillsMenu;
import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.abilities.Ability;
import io.github.archy_x.aureliumskills.skills.levelers.Leveler;

@CommandAlias("skills|sk|skill")
public class SkillsCommand extends BaseCommand {
 
	private Plugin plugin;
	private Options options;
	private Lang lang;
	
	public SkillsCommand(Plugin plugin) {
		this.plugin = plugin;
		options = new Options(plugin);
		lang = new Lang(plugin);
	}
	
	@Default
	public void onSkills(Player player) {
		SkillsMenu.getInventory(player).open(player);
	}
	
	@Subcommand("lang")
	@CommandCompletion("@lang")
	@CommandPermission("aureliumskills.lang")
	public void onLanguage(Player player, String language) {
		if (lang.setLanguage(language)) {
			player.sendMessage(AureliumSkills.tag + ChatColor.GREEN + "Language set to " + language);
		}
		else {
			player.sendMessage(AureliumSkills.tag + ChatColor.RED + "Language not found in config!");
		}
	}
	
	
	@Subcommand("reload")
	@CommandPermission("aureliumskills.reload")
	public void reload(CommandSender sender) {
		plugin.reloadConfig();
		plugin.saveDefaultConfig();
		options.loadConfig();
		lang.loadLanguages();
		Leveler.loadLevelReqs();
		sender.sendMessage(AureliumSkills.tag + ChatColor.GREEN + "Config reloaded!");
	}
	
	@Subcommand("sp add")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.sp.add")
	public void onSkillPointsAdd(CommandSender sender, @Flags("other") Player player, Skill skill, int amount) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			SkillLoader.playerSkills.get(player.getUniqueId()).addSkillPoints(skill, amount);
			sender.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "Added " + ChatColor.AQUA + amount + " " + skill.getDisplayName() + ChatColor.GRAY + " Skill Points to " + ChatColor.GOLD + player.getName());
		}
		else {
			sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "That player does not have a skills profile!");
		}
	}
	
	@Subcommand("skill setlevel")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.skill.setlevel")
	public void onSkillLevelSet(CommandSender sender, @Flags("other") Player player, Skill skill, int level) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			if (level > 0) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setSkillLevel(skill, level);
					Leveler.updateStats(player);
					sender.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "Skill " + ChatColor.AQUA + skill.getDisplayName() + ChatColor.GRAY + " set to level " + ChatColor.AQUA + level + ChatColor.GRAY + " for player " + ChatColor.GOLD + player.getName());
			}
			else {
				sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Level must be at least 1!");
			}
		}
	}
	
	@Subcommand("ability|ab setlevel")
	@CommandCompletion("@players @abilities")
	@CommandPermission("aureliumskills.ability.setlevel")
	public void onAbilityLevelSet(CommandSender sender, @Flags("other") Player player, Ability ability, int level) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			if (level >= 0) {
				if (level <= ability.getMaxLevel()) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setAbilityLevel(ability, level);
					sender.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "Ability " + ChatColor.GREEN + ability.getDisplayName() + ChatColor.GRAY + " set to level " + ChatColor.GREEN + level + ChatColor.GRAY + " for player " + ChatColor.GOLD + player.getName());
				}
				else {
					sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Level exceeds maximum level for that skill!");
				}
			}
			else {
				sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Level cannot be negative!");
			}
		}
	}
	
}
