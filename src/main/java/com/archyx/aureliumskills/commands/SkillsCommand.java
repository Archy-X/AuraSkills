package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.menu.SkillsMenu;
import com.archyx.aureliumskills.modifier.ArmorModifier;
import com.archyx.aureliumskills.modifier.ItemModifier;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.*;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.stats.*;
import com.archyx.aureliumskills.util.MySqlSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@CommandAlias("skills|sk|skill")
public class SkillsCommand extends BaseCommand {
 
	private final AureliumSkills plugin;
	private final Lang lang;
	private final Leaderboard leaderboard;

	public SkillsCommand(AureliumSkills plugin) {
		this.plugin = plugin;
		lang = new Lang(plugin);
		leaderboard = AureliumSkills.leaderboard;
	}
	
	@Default
	@CommandPermission("aureliumskills.skills")
	@Description("Opens the Skills menu, where you can browse skills, progress, and abilities.")
	public void onSkills(Player player) {
		SkillsMenu.getInventory(player).open(player);
	}
	
	@Subcommand("xp add")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.xp.add")
	@Description("Adds skill XP to a player for a certain skill.")
	public void onXpAdd(CommandSender sender, @Flags("other") Player player, Skill skill, double amount) {
		if (OptionL.isEnabled(skill)) {
			Leveler.addXp(player, skill, amount);
			sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.XP_ADD).replace("$amount$", String.valueOf(amount)).replace("$skill$", skill.getDisplayName()).replace("$player$", player.getName()).replace("&", "§"));
		}
		else {
			sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.UNKNOWN_SKILL));
		}
	}

	@Subcommand("xp set")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.xp.set")
	@Description("Sets a player's skill XP for a certain skill to an amount.")
	public void onXpSet(CommandSender sender, @Flags("other") Player player, Skill skill, double amount) {
		if (OptionL.isEnabled(skill)) {
			Leveler.setXp(player, skill, amount);
			sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.XP_SET).replace("$amount$", String.valueOf(amount)).replace("$skill$", skill.getDisplayName()).replace("$player$", player.getName()).replace("&", "§"));
		}
		else {
			sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.UNKNOWN_SKILL));
		}
	}

	@Subcommand("xp remove")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.xp.remove")
	@Description("Removes skill XP from a player in a certain skill.")
	public void onXpRemove(CommandSender sender, @Flags("other") Player player, Skill skill, double amount) {
		if (OptionL.isEnabled(skill)) {
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (playerSkill.getXp(skill) - amount >= 0) {
					Leveler.setXp(player, skill, playerSkill.getXp(skill) - amount);
					sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.XP_REMOVE).replace("$amount$", String.valueOf(amount)).replace("$skill$", skill.getDisplayName()).replace("$player$", player.getName()).replace("&", "§"));
				}
				else {
					sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.XP_REMOVE).replace("$amount$", String.valueOf(playerSkill.getXp(skill))).replace("$skill$", skill.getDisplayName()).replace("$player$", player.getName()).replace("&", "§"));
					Leveler.setXp(player, skill, 0);
				}
			}
		}
		else {
			sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.UNKNOWN_SKILL));
		}
	}

	@Subcommand("top")
	@CommandAlias("skilltop")
	@CommandCompletion("@skills")
	@CommandPermission("aureliumskills.top")
	@Description("Shows the top players in a skill")
	@Syntax("Usage: /sk top <page> or /sk top [skill] <page>")
	public void onTop(CommandSender sender, String[] args) {
		if (args.length == 0) {
			List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readPowerLeaderboard(1, 10);
			sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_POWER_HEADER).replace("&", "§"));
			for (PlayerSkillInstance playerSkill : lb) {
				sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_POWER_ENTRY)
						.replace("&", "§")
						.replace("$rank$", String.valueOf(lb.indexOf(playerSkill) + 1))
						.replace("$player_name$", playerSkill.getPlayerName())
						.replace("$level$", String.valueOf(playerSkill.getPowerLevel())));
			}
		}
		else if (args.length == 1) {
			try {
				int page = Integer.parseInt(args[0]);
				List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readPowerLeaderboard(page, 10);
				sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_POWER_HEADER_PAGE).replace("&", "§").replace("$page$", String.valueOf(page)));
				for (PlayerSkillInstance playerSkill : lb) {
					sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_POWER_ENTRY)
							.replace("&", "§")
							.replace("$rank$", String.valueOf((page - 1) * 10 + lb.indexOf(playerSkill) + 1))
							.replace("$player_name$", playerSkill.getPlayerName())
							.replace("$level$", String.valueOf(playerSkill.getPowerLevel())));
				}
			}
			catch (Exception e) {
				try {
					Skill skill = Skill.valueOf(args[0].toUpperCase());
					List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readSkillLeaderboard(skill, 1, 10);
					sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_SKILL_HEADER).replace("&", "§").replace("$skill$", skill.getDisplayName()));
					for (PlayerSkillInstance playerSkill : lb) {
						sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_SKILL_ENTRY)
								.replace("&", "§")
								.replace("$rank$", String.valueOf(lb.indexOf(playerSkill) + 1))
								.replace("$player_name$", playerSkill.getPlayerName())
								.replace("$level$", String.valueOf(playerSkill.getSkillLevel(skill))));
					}
				}
				catch (IllegalArgumentException iae) {
					sender.sendMessage(ChatColor.YELLOW + "Usage: " + ChatColor.GREEN + "/sk top " + ChatColor.WHITE + "<page>" + ChatColor.GRAY +  " or "  + ChatColor.GREEN + "/sk top " + ChatColor.WHITE + "[skill] <page>");
				}
			}
		}
		else if (args.length == 2) {
			try {
				Skill skill = Skill.valueOf(args[0].toUpperCase());
				try {
					int page = Integer.parseInt(args[1]);
					List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readSkillLeaderboard(skill, page, 10);
					sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_SKILL_HEADER_PAGE).replace("&", "§").replace("$page$", String.valueOf(page)).replace("$skill$", skill.getDisplayName()));
					for (PlayerSkillInstance playerSkill : lb) {
						sender.sendMessage(Lang.getMessage(Message.LEADERBOARD_SKILL_ENTRY)
								.replace("&", "§")
								.replace("$rank$", String.valueOf((page - 1) * 10 + lb.indexOf(playerSkill) + 1))
								.replace("$player_name$", playerSkill.getPlayerName())
								.replace("$level$", String.valueOf(playerSkill.getSkillLevel(skill))));
					}
				}
				catch (Exception e) {
					sender.sendMessage(ChatColor.YELLOW + "Usage: " + ChatColor.GREEN + "/sk top " + ChatColor.WHITE + "<page>" + ChatColor.GRAY +  " or "  + ChatColor.GREEN + "/sk top " + ChatColor.WHITE + "[skill] <page>");
				}
			}
			catch (IllegalArgumentException iae) {
				sender.sendMessage(ChatColor.YELLOW + "Usage: " + ChatColor.GREEN + "/sk top " + ChatColor.WHITE + "<page>" + ChatColor.GRAY +  " or "  + ChatColor.GREEN + "/sk top " + ChatColor.WHITE + "[skill] <page>");
			}
		}
	}


	@Subcommand("save")
	@CommandPermission("aureliumskills.save")
	@Description("Saves skill data")
	public void onSave(CommandSender sender) {
		if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
			if (!MySqlSupport.isSaving) {
				new BukkitRunnable() {
					@Override
					public void run() {
						plugin.mySqlSupport.saveData(false);
					}
				}.runTaskAsynchronously(plugin);
				if (sender instanceof Player) {
					sender.sendMessage(AureliumSkills.tag + ChatColor.GREEN + "Skill data saved!");
				}
			}
			else {
				sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Data is already saving!");
			}
		}
		else {
			if (!SkillLoader.isSaving) {
				new BukkitRunnable() {
					@Override
					public void run() {
						plugin.getSkillLoader().saveSkillData(false);
					}
				}.runTaskAsynchronously(plugin);
				if (sender instanceof Player) {
					sender.sendMessage(AureliumSkills.tag + ChatColor.GREEN + "Skill data saved!");
				}
			}
			else {
				sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Data is already saving!");
			}
		}
	}

	@Subcommand("updateleaderboards")
	@CommandPermission("aureliumskills.updateleaderboards")
	@Description("Updates and sorts the leaderboards")
	public void onUpdateLeaderboards(CommandSender sender) {
		if (!Leaderboard.isSorting) {
			new BukkitRunnable() {
				@Override
				public void run() {
					AureliumSkills.leaderboard.updateLeaderboards(false);
				}
			}.runTaskAsynchronously(plugin);
			if (sender instanceof Player) {
				sender.sendMessage(AureliumSkills.tag + ChatColor.GREEN + "Leaderboard updated!");
			}
		}
		else {
			sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Leaderboard is already updating!");
		}
	}

	@Subcommand("toggle")
	@CommandAlias("abtoggle")
	@CommandPermission("aureliumskills.abtoggle")
	@Description("Toggle your own action bar")
	public void onActionBarToggle(Player player) {
		if (OptionL.getBoolean(Option.ENABLE_ACTION_BAR)) {
			if (ActionBar.actionBarDisabled.contains(player.getUniqueId())) {
				ActionBar.actionBarDisabled.remove(player.getUniqueId());
				player.sendMessage(AureliumSkills.tag + ChatColor.GREEN + "Your skills action bar has been enabled");
			}
			else {
				ActionBar.actionBarDisabled.add(player.getUniqueId());
				player.sendMessage(AureliumSkills.tag + ChatColor.RED + "Your skills action bar has been disabled");
			}
		}
		else {
			player.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Skills action bar is not enabled on this server!");
		}
	}

	@Subcommand("rank")
	@CommandAlias("skillrank")
	@CommandPermission("aureliumskills.rank")
	@Description("Shows your skill rankings")
	public void onRank(Player player) {
		player.sendMessage(Lang.getMessage(Message.SKILL_RANK_HEADER).replace("&", "§"));
		player.sendMessage(Lang.getMessage(Message.SKILL_RANK_POWER).replace("&", "§").replace("$rank$", String.valueOf(AureliumSkills.leaderboard.getPowerRank(player.getUniqueId()))).replace("$total$", String.valueOf(AureliumSkills.leaderboard.getSize())));
		for (Skill skill : Skill.values()) {
			player.sendMessage(Lang.getMessage(Message.SKILL_RANK_ENTRY)
					.replace("&", "§").replace("$skill$", skill.getDisplayName())
					.replace("$rank$", String.valueOf(AureliumSkills.leaderboard.getSkillRank(skill, player.getUniqueId())))
					.replace("$total$", String.valueOf(AureliumSkills.leaderboard.getSize())));
		}
	}


	@Subcommand("lang")
	@CommandCompletion("@lang")
	@CommandPermission("aureliumskills.lang")
	@Description("Sets the language displayed to a certain language defined in messages.yml, but will not change the default language set in the file.")
	public void onLanguage(Player player, String language) {
		if (lang.setLanguage(language)) {
			player.sendMessage(AureliumSkills.tag + ChatColor.GREEN + Lang.getMessage(Message.LANGUAGE_SET_TO).replace("_", language));
		}
		else {
			player.sendMessage(AureliumSkills.tag + ChatColor.RED + Lang.getMessage(Message.LANGUAGE_NOT_FOUND));
		}
	}
	
	
	@Subcommand("reload")
	@CommandPermission("aureliumskills.reload")
	@Description("Reloads the config, messages, loot tables, and health and luck stats.")
	public void reload(CommandSender sender) {
		plugin.reloadConfig();
		plugin.saveDefaultConfig();
		AureliumSkills.optionLoader.loadOptions();
		lang.loadDefaultMessages();
		lang.loadLanguages();
		AureliumSkills.abilityOptionManager.loadOptions();
		Leveler.loadLevelReqs();
		AureliumSkills.lootTableManager.loadLootTables();
		AureliumSkills.worldManager.loadWorlds();
		if (AureliumSkills.worldGuardEnabled) {
			AureliumSkills.worldGuardSupport.loadRegions();
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			Health.reload(player);
			Luck.reload(player);
		}
		sender.sendMessage(AureliumSkills.tag + ChatColor.GREEN + Lang.getMessage(Message.CONFIG_RELOADED));
	}
	
	@Subcommand("skill setlevel")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.skill.setlevel")
	@Description("Sets a specific skill to a level for a player.")
	public void onSkillSetlevel(CommandSender sender, @Flags("other") Player player, Skill skill, int level) {
		if (OptionL.isEnabled(skill)) {
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				if (level > 0) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setSkillLevel(skill, level);
					playerSkill.setXp(skill, 0);
					Leveler.updateStats(player);
					Leveler.updateAbilities(player, skill);
					sender.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "Skill " + ChatColor.AQUA + skill.getDisplayName() + ChatColor.GRAY + " set to level " + ChatColor.AQUA + level + ChatColor.GRAY + " for player " + ChatColor.GOLD + player.getName());
				} else {
					sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Level must be at least 1!");
				}
			}
		}
		else {
			sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.UNKNOWN_SKILL));
		}
	}

	@Subcommand("skill setall")
	@CommandCompletion("@players")
	@CommandPermission("aureliumskills.skill.setlevel")
	@Description("Sets all of a player's skills to a level.")
	public void onSkillSetall(CommandSender sender, @Flags("other") Player player, int level) {
		if (level > 0) {
			for (Skill skill : Skill.values()) {
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setSkillLevel(skill, level);
					playerSkill.setXp(skill, 0);
					Leveler.updateStats(player);
					Leveler.updateAbilities(player, skill);
				}
			}
			sender.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "All skills set to level " + ChatColor.AQUA + level + ChatColor.GRAY + " for player " + ChatColor.GOLD + player.getName());
		} else {
			sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Level must be at least 1!");
		}
	}


	@Subcommand("skill reset")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.skill.reset")
	@Description("Resets all skills or a specific skill to level 1 for a player.")
	public void onSkillReset(CommandSender sender, @Flags("other") Player player, @Optional Skill skill) {
		if (skill != null) {
			if (OptionL.isEnabled(skill)) {
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setSkillLevel(skill, 1);
					playerSkill.setXp(skill, 0);
					Leveler.updateStats(player);
					Leveler.updateAbilities(player, skill);
					sender.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "Skill " + ChatColor.AQUA + skill.getDisplayName() + ChatColor.GRAY + " set to level " + ChatColor.AQUA + "1" + ChatColor.GRAY + " for player " + ChatColor.GOLD + player.getName());
				}
			} else {
				sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.UNKNOWN_SKILL));
			}
		}
		else {
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				for (Skill s : Skill.values()) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setSkillLevel(s, 1);
					playerSkill.setXp(s, 0);
					Leveler.updateStats(player);
					Leveler.updateAbilities(player, s);
				}
				sender.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "All Skills set to level " + ChatColor.AQUA + "1" + ChatColor.GRAY + " for player " + ChatColor.GOLD + player.getName());
			}
		}
	}

	@Subcommand("modifier add")
	@CommandPermission("aureliumskills.modifier.add")
	@CommandCompletion("@players @stats @nothing @nothing true")
	@Description("Adds a stat modifier to a player.")
	public void onAdd(CommandSender sender, @Flags("other") Player player, Stat stat, String name, int value, @Default("false") boolean silent) {
		if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
			StatModifier modifier = new StatModifier(name, stat, value);
			if (!playerStat.getModifiers().containsKey(name)) {
				playerStat.addModifier(modifier);
				if (!silent) {
					sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_ADD_ADDED), modifier, player));
				}
			}
			else {
				if (!silent) {
					sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_ADD_ALREADY_EXISTS), modifier, player));
				}
			}
		}
		else {
			if (!silent) {
				sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_NO_PROFILE).replace("&", "§"));
			}
		}
	}

	@Subcommand("modifier remove")
	@CommandPermission("aureliumskills.modifier.remove")
	@CommandCompletion("@players @nothing true")
	@Description("Removes a specific stat modifier from a player.")
	public void onRemove(CommandSender sender, @Flags("other") Player player, String name, @Default("false") boolean silent) {
		if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
			if (playerStat.removeModifier(name)) {
				if (!silent) {
					sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_REMOVE_REMOVED), name, player));
				}
			}
			else {
				if (!silent) {
					sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_REMOVE_NOT_FOUND), name, player));
				}
			}
		}
		else {
			if (!silent) {
				sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_NO_PROFILE).replace("&", "§"));
			}
		}
	}

	@Subcommand("modifier list")
	@CommandCompletion("@players @stats")
	@CommandPermission("aureliumskills.modifier.list")
	@Description("Lists all or a specific stat's modifiers for a player.")
	public void onList(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat) {
		if (player == null) {
			if (sender instanceof Player) {
				Player target = (Player) sender;
				if (SkillLoader.playerStats.containsKey(target.getUniqueId())) {
					PlayerStat targetStat = SkillLoader.playerStats.get(target.getUniqueId());
					String message;
					if (stat == null) {
						message = StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ALL_STATS_HEADER), target);
						for (String key : targetStat.getModifiers().keySet()) {
							StatModifier modifier = targetStat.getModifiers().get(key);
							message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ALL_STATS_LINE), modifier, target);
						}
					} else {
						message = StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ONE_STAT_HEADER), target);
						for (String key : targetStat.getModifiers().keySet()) {
							StatModifier modifier = targetStat.getModifiers().get(key);
							if (modifier.getStat() == stat) {
								message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ONE_STAT_LINE), modifier, target);
							}
						}
					}
					sender.sendMessage(message);
				} else {
					sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_NO_PROFILE).replace("&", "§"));
				}
			}
			else {
				sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_LIST_PLAYERS_ONLY).replace("&", "§"));
			}
		}
		else {
			if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
				PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
				String message;
				if (stat == null) {
					message = StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ALL_STATS_HEADER), player);
					for (String key : playerStat.getModifiers().keySet()) {
						StatModifier modifier = playerStat.getModifiers().get(key);
						message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ALL_STATS_LINE), modifier, player);
					}
				} else {
					message = StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ONE_STAT_HEADER), player);
					for (String key : playerStat.getModifiers().keySet()) {
						StatModifier modifier = playerStat.getModifiers().get(key);
						if (modifier.getStat() == stat) {
							message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_LIST_ONE_STAT_LINE), modifier, player);
						}
					}
				}
				sender.sendMessage(message);
			} else {
				sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_NO_PROFILE).replace("&", "§"));
			}
		}
	}

	@Subcommand("modifier removeall")
	@CommandCompletion("@players @stats")
	@CommandPermission("aureliumskills.modifier.removeall")
	@Description("Removes all stat modifiers from a player.")
	public void onRemoveAll(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat, @Default("false") boolean silent) {
		if (player == null) {
			if (sender instanceof Player) {
				Player target = (Player) sender;
				if (SkillLoader.playerStats.containsKey(target.getUniqueId())) {
					PlayerStat playerStat = SkillLoader.playerStats.get(target.getUniqueId());
					int removed = 0;
					for (String key : playerStat.getModifiers().keySet()) {
						if (stat == null) {
							playerStat.removeModifier(key);
							removed++;
						}
						else if (playerStat.getModifiers().get(key).getStat() == stat) {
							playerStat.removeModifier(key);
							removed++;
						}
					}
					if (!silent) {
						if (stat == null) {
							sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_REMOVE_ALL_REMOVED_ALL_STATS), target).replace("$removed$", String.valueOf(removed)));
						}
						else {
							sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_REMOVE_ALL_REMOVED_ONE_STAT), stat, target).replace("$removed$", String.valueOf(removed)));
						}
					}
				}
				else {
					if (!silent) {
						sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_NO_PROFILE).replace("&", "§"));
					}
				}
			}
			else {
				if (!silent) {
					sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_REMOVE_ALL_PLAYERS_ONLY).replace("&", "§"));
				}
			}
		}
		else {
			if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
				PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
				int removed = 0;
				for (String key : playerStat.getModifiers().keySet()) {
					if (stat == null) {
						playerStat.removeModifier(key);
						removed++;
					}
					else if (playerStat.getModifiers().get(key).getStat() == stat) {
						playerStat.removeModifier(key);
						removed++;
					}
				}
				if (!silent) {
					if (stat == null) {
						sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_REMOVE_ALL_REMOVED_ALL_STATS), player).replace("$removed$", String.valueOf(removed)));
					}
					else {
						sender.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.MODIFIER_REMOVE_ALL_REMOVED_ONE_STAT), stat, player).replace("$removed$", String.valueOf(removed)));
					}
				}
			}
			else {
				if (!silent) {
					sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.MODIFIER_NO_PROFILE).replace("&", "§"));
				}
			}
		}
	}

	@Subcommand("item modifier add")
	@CommandCompletion("@stats @nothing false|true")
	@CommandPermission("aureliumskills.item.modifier.add")
	@Description("Adds an item stat modifier to the item held, along with lore by default.")
	public void onItemModifierAdd(Player player, Stat stat, int value, @Default("true") boolean lore) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			for (StatModifier statModifier : ItemModifier.getItemModifiers(item)) {
				if (statModifier.getStat() == stat) {
					player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ITEM_ADD_ALREADY_EXISTS), stat));
					return;
				}
			}
			if (lore) {
				ItemModifier.addLore(item, stat, value);
			}
			ItemStack newItem = ItemModifier.addItemModifier(item, stat, value);
			player.getInventory().setItemInMainHand(newItem);
			player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ITEM_ADD_ADDED), stat, value));
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ITEM_ADD_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("item modifier remove")
	@CommandCompletion("@stats false|true")
	@CommandPermission("aureliumskills.item.modifier.remove")
	@Description("Removes an item stat modifier from the item held, and the lore associated with it by default.")
	public void onItemModifierRemove(Player player, Stat stat, @Default("true") boolean lore) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			boolean removed = false;
			for (StatModifier modifier : ItemModifier.getItemModifiers(item)) {
				if (modifier.getStat() == stat) {
					item = ItemModifier.removeItemModifier(item, stat);
					removed = true;
					break;
				}
			}
			if (lore) {
				ItemModifier.removeLore(item, stat);
			}
			player.getInventory().setItemInMainHand(item);
			if (removed) {
				player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ITEM_REMOVE_REMOVED), stat));
			}
			else {
				player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ITEM_REMOVE_DOES_NOT_EXIST), stat));
			}
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ITEM_REMOVE_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("item modifier list")
	@CommandPermission("aureliumskills.item.modifier.list")
	@Description("Lists all item stat modifiers on the item held.")
	public void onItemModifierList(Player player) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			StringBuilder message = new StringBuilder(Lang.getMessage(Message.ITEM_LIST_HEADER).replace("&", "§"));
			for (StatModifier modifier : ItemModifier.getItemModifiers(item)) {
				message.append("\n").append(StatModifier.applyPlaceholders(Lang.getMessage(Message.ITEM_LIST_LINE), modifier));
			}
			player.sendMessage(message.toString());
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ITEM_LIST_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("item modifier removeall")
	@CommandPermission("aureliumskills.item.modifier.removall")
	@Description("Removes all item stat modifiers from the item held.")
	public void onItemModifierRemoveAll(Player player) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = ItemModifier.removeAllItemModifiers(player.getInventory().getItemInMainHand());
			player.getInventory().setItemInMainHand(item);
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ITEM_REMOVE_ALL_REMOVED).replace("&", "§"));
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ITEM_REMOVE_ALL_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("armor modifier add")
	@CommandCompletion("@stats @nothing false|true")
	@CommandPermission("aureliumskills.armor.modifier.add")
	@Description("Adds an armor stat modifier to the item held, along with lore by default.")
	public void onArmorModifierAdd(Player player, Stat stat, int value, @Default("true") boolean lore) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			for (StatModifier statModifier : ArmorModifier.getArmorModifiers(item)) {
				if (statModifier.getStat() == stat) {
					player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ARMOR_ADD_ALREADY_EXISTS), stat));
					return;
				}
			}
			if (lore) {
				ArmorModifier.addLore(item, stat, value);
			}
			ItemStack newItem = ArmorModifier.addArmorModifier(item, stat, value);
			player.getInventory().setItemInMainHand(newItem);
			player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ARMOR_ADD_ADDED), stat, value));
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ARMOR_ADD_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("armor modifier remove")
	@CommandCompletion("@stats false|true")
	@CommandPermission("aureliumskills.armor.modifier.remove")
	@Description("Removes an armor stat modifier from the item held, and the lore associated with it by default.")
	public void onArmorModifierRemove(Player player, Stat stat, @Default("true") boolean lore) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			boolean removed = false;
			for (StatModifier modifier : ArmorModifier.getArmorModifiers(item)) {
				if (modifier.getStat() == stat) {
					item = ArmorModifier.removeArmorModifier(item, stat);
					removed = true;
					break;
				}
			}
			if (lore) {
				ItemModifier.removeLore(item, stat);
			}
			player.getInventory().setItemInMainHand(item);
			if (removed) {
				player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ARMOR_REMOVE_REMOVED), stat));
			}
			else {
				player.sendMessage(AureliumSkills.tag + StatModifier.applyPlaceholders(Lang.getMessage(Message.ARMOR_REMOVE_DOES_NOT_EXIST), stat));
			}
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ARMOR_REMOVE_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("armor modifier list")
	@CommandPermission("aureliumskills.armor.modifier.list")
	@Description("Lists all armor stat modifiers on the item held.")
	public void onArmorModifierList(Player player) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			StringBuilder message = new StringBuilder(Lang.getMessage(Message.ARMOR_LIST_HEADER).replace("&", "§"));
			for (StatModifier modifier : ArmorModifier.getArmorModifiers(item)) {
				message.append("\n").append(StatModifier.applyPlaceholders(Lang.getMessage(Message.ARMOR_LIST_LINE), modifier));
			}
			player.sendMessage(message.toString());
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ARMOR_LIST_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("armor modifier removeall")
	@CommandPermission("aureliumskills.armor.modifier.removeall")
	@Description("Removes all armor stat modifiers from the item held.")
	public void onArmorModifierRemoveAll(Player player) {
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = ArmorModifier.removeAllArmorModifiers(player.getInventory().getItemInMainHand());
			player.getInventory().setItemInMainHand(item);
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ARMOR_REMOVE_ALL_REMOVED).replace("&", "§"));
		}
		else {
			player.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.ARMOR_REMOVE_ALL_MUST_HOLD_ITEM).replace("&", "§"));
		}
	}

	@Subcommand("multiplier")
	@CommandCompletion("@players")
	@CommandPermission("aureliumskills.multiplier")
	@Description("Shows a player's current XP multiplier based on their permissions.")
	public void onMultiplier(CommandSender sender, @Optional @Flags("other") Player player) {
		if (player == null) {
			if (sender instanceof Player) {
				Player target = (Player) sender;
				double multiplier = Leveler.getMultiplier(target);
				sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.XP_MULTIPLIER).replace("&", "§").replace("$player_name$", target.getName()).replace("$multiplier$", String.valueOf(multiplier)).replace("$multiplier_percent$", String.valueOf((multiplier - 1) * 100)));
			}
			else {
				sender.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Only players can list their own multiplier! Specify a player!");
			}
		}
		else {
			double multiplier = Leveler.getMultiplier(player);
			sender.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.XP_MULTIPLIER).replace("&", "§").replace("$player_name$", player.getName()).replace("$multiplier$", String.valueOf(multiplier)).replace("$multiplier_percent$", String.valueOf((multiplier - 1) * 100)));
		}
	}

	@HelpCommand
	@CommandPermission("aureliumskills.help")
	public void onHelp(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}
}
