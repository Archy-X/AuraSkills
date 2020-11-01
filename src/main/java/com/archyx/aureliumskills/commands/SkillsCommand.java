package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Locale;

@CommandAlias("skills|sk|skill")
public class SkillsCommand extends BaseCommand {
 
	private final AureliumSkills plugin;
	private final Lang lang;

	public SkillsCommand(AureliumSkills plugin) {
		this.plugin = plugin;
		lang = new Lang(plugin);
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
		Locale locale = Lang.getLanguage(player);
		if (OptionL.isEnabled(skill)) {
			Leveler.addXp(player, skill, amount);
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_ADD, locale).replace("{amount}", String.valueOf(amount)).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
		}
		else {
			sender.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.YELLOW + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
		}
	}

	@Subcommand("xp set")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.xp.set")
	@Description("Sets a player's skill XP for a certain skill to an amount.")
	public void onXpSet(CommandSender sender, @Flags("other") Player player, Skill skill, double amount) {
		Locale locale = Lang.getLanguage(player);
		if (OptionL.isEnabled(skill)) {
			Leveler.setXp(player, skill, amount);
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_SET, locale).replace("{amount}", String.valueOf(amount)).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
		}
		else {
			sender.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.YELLOW + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
		}
	}

	@Subcommand("xp remove")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.xp.remove")
	@Description("Removes skill XP from a player in a certain skill.")
	public void onXpRemove(CommandSender sender, @Flags("other") Player player, Skill skill, double amount) {
		Locale locale = Lang.getLanguage(player);
		if (OptionL.isEnabled(skill)) {
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (playerSkill.getXp(skill) - amount >= 0) {
					Leveler.setXp(player, skill, playerSkill.getXp(skill) - amount);
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_REMOVE, locale).replace("{amount}", String.valueOf(amount)).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
				}
				else {
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.XP_REMOVE, locale).replace("{amount}", String.valueOf(playerSkill.getXp(skill))).replace("{skill}", skill.getDisplayName(locale)).replace("{player}", player.getName()));
					Leveler.setXp(player, skill, 0);
				}
			}
		}
		else {
			sender.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.YELLOW + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
		}
	}

	@Subcommand("top")
	@CommandAlias("skilltop")
	@CommandCompletion("@skills")
	@CommandPermission("aureliumskills.top")
	@Description("Shows the top players in a skill")
	@Syntax("Usage: /sk top <page> or /sk top [skill] <page>")
	public void onTop(CommandSender sender, String[] args) {
		Locale locale = Lang.getLanguage(sender);
		if (args.length == 0) {
			List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readPowerLeaderboard(1, 10);
			sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_HEADER, locale));
			for (PlayerSkillInstance playerSkill : lb) {
				OfflinePlayer player = Bukkit.getOfflinePlayer(playerSkill.getPlayerId());
				sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_ENTRY, locale)
						.replace("{rank}", String.valueOf(lb.indexOf(playerSkill) + 1))
						.replace("{player}", player.getName() != null ? player.getName() : "?")
						.replace("{level}", String.valueOf(playerSkill.getPowerLevel())));
			}
		}
		else if (args.length == 1) {
			try {
				int page = Integer.parseInt(args[0]);
				List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readPowerLeaderboard(page, 10);
				sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_HEADER_PAGE, locale).replace("{page}", String.valueOf(page)));
				for (PlayerSkillInstance playerSkill : lb) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(playerSkill.getPlayerId());
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_ENTRY, locale)
							.replace("{rank}", String.valueOf((page - 1) * 10 + lb.indexOf(playerSkill) + 1))
							.replace("{player}", player.getName() != null ? player.getName() : "?")
							.replace("{level}", String.valueOf(playerSkill.getPowerLevel())));
				}
			}
			catch (Exception e) {
				try {
					Skill skill = Skill.valueOf(args[0].toUpperCase());
					List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readSkillLeaderboard(skill, 1, 10);
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_HEADER, locale).replace("&", "§").replace("{skill}", skill.getDisplayName(locale)));
					for (PlayerSkillInstance playerSkill : lb) {
						OfflinePlayer player = Bukkit.getOfflinePlayer(playerSkill.getPlayerId());
						sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_ENTRY, locale)
								.replace("{rank}", String.valueOf(lb.indexOf(playerSkill) + 1))
								.replace("{player}", player.getName() != null ? player.getName() : "?")
								.replace("{level}", String.valueOf(playerSkill.getSkillLevel(skill))));
					}
				}
				catch (IllegalArgumentException iae) {
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_USAGE, locale));
				}
			}
		}
		else if (args.length == 2) {
			try {
				Skill skill = Skill.valueOf(args[0].toUpperCase());
				try {
					int page = Integer.parseInt(args[1]);
					List<PlayerSkillInstance> lb = AureliumSkills.leaderboard.readSkillLeaderboard(skill, page, 10);
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_HEADER_PAGE, locale).replace("{page}", String.valueOf(page)).replace("{skill}", skill.getDisplayName(locale)));
					for (PlayerSkillInstance playerSkill : lb) {
						OfflinePlayer player = Bukkit.getOfflinePlayer(playerSkill.getPlayerId());
						sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_ENTRY, locale)
								.replace("{rank}", String.valueOf((page - 1) * 10 + lb.indexOf(playerSkill) + 1))
								.replace("{player}", player.getName() != null ? player.getName() : "?")
								.replace("{level}", String.valueOf(playerSkill.getSkillLevel(skill))));
					}
				}
				catch (Exception e) {
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_USAGE, locale));
				}
			}
			catch (IllegalArgumentException iae) {
				sender.sendMessage(Lang.getMessage(CommandMessage.TOP_USAGE, locale));
			}
		}
	}


	@Subcommand("save")
	@CommandPermission("aureliumskills.save")
	@Description("Saves skill data")
	public void onSave(CommandSender sender) {
		Locale locale = Lang.getLanguage(sender);
		if (OptionL.getBoolean(Option.MYSQL_ENABLED)) {
			if (!MySqlSupport.isSaving) {
				if (plugin.mySqlSupport != null) {
					new BukkitRunnable() {
						@Override
						public void run() {
							plugin.mySqlSupport.saveData(false);
						}
					}.runTaskAsynchronously(plugin);
					if (sender instanceof Player) {
						sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_SAVED, locale));
					}
				}
				else {
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_MYSQL_NOT_ENABLED, locale));
					if (!SkillLoader.isSaving) {
						new BukkitRunnable() {
							@Override
							public void run() {
								plugin.getSkillLoader().saveSkillData(false);
							}
						}.runTaskAsynchronously(plugin);
						if (sender instanceof Player) {
							sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_SAVED, locale));
						}
					}
					else {
						sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_ALREADY_SAVING, locale));
					}
				}
			}
			else {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_ALREADY_SAVING, locale));
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
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_SAVED, locale));
				}
			}
			else {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_ALREADY_SAVING, locale));
			}
		}
	}

	@Subcommand("updateleaderboards")
	@CommandPermission("aureliumskills.updateleaderboards")
	@Description("Updates and sorts the leaderboards")
	public void onUpdateLeaderboards(CommandSender sender) {
		Locale locale = Lang.getLanguage(sender);
		if (!Leaderboard.isSorting) {
			new BukkitRunnable() {
				@Override
				public void run() {
					AureliumSkills.leaderboard.updateLeaderboards(false);
				}
			}.runTaskAsynchronously(plugin);
			if (sender instanceof Player) {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UPDATELEADERBOARDS_UPDATED, locale));
			}
		}
		else {
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UPDATELEADERBOARDS_ALREADY_UPDATING, locale));
		}
	}

	@Subcommand("toggle")
	@CommandAlias("abtoggle")
	@CommandPermission("aureliumskills.abtoggle")
	@Description("Toggle your own action bar")
	public void onActionBarToggle(Player player) {
		Locale locale = Lang.getLanguage(player);
		ActionBar actionBar = plugin.getActionBar();
		if (OptionL.getBoolean(Option.ACTION_BAR_ENABLED)) {
			if (actionBar.getActionBarDisabled().contains(player.getUniqueId())) {
				actionBar.getActionBarDisabled().remove(player.getUniqueId());
				player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.TOGGLE_ENABLED, locale));
			}
			else {
				actionBar.getActionBarDisabled().add(player.getUniqueId());
				player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.TOGGLE_DISABLED, locale));
			}
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.TOGGLE_NOT_ENABLED, locale));
		}
	}

	@Subcommand("rank")
	@CommandAlias("skillrank")
	@CommandPermission("aureliumskills.rank")
	@Description("Shows your skill rankings")
	public void onRank(Player player) {
		Locale locale = Lang.getLanguage(player);
		player.sendMessage(Lang.getMessage(CommandMessage.RANK_HEADER, locale));
		player.sendMessage(Lang.getMessage(CommandMessage.RANK_POWER, locale)
				.replace("{rank}", String.valueOf(AureliumSkills.leaderboard.getPowerRank(player.getUniqueId())))
				.replace("{total}", String.valueOf(AureliumSkills.leaderboard.getSize())));
		for (Skill skill : Skill.values()) {
			if (OptionL.isEnabled(skill)) {
				player.sendMessage(Lang.getMessage(CommandMessage.RANK_ENTRY, locale)
						.replace("{skill}", String.valueOf(skill.getDisplayName(locale)))
						.replace("{rank}", String.valueOf(AureliumSkills.leaderboard.getSkillRank(skill, player.getUniqueId())))
						.replace("{total}", String.valueOf(AureliumSkills.leaderboard.getSize())));
			}
		}
	}

	@Subcommand("lang")
	@CommandCompletion("@lang")
	@CommandPermission("aureliumskills.lang")
	@Description("Changes your player language")
	public void onLanguage(Player player, String language) {
		Locale locale = new Locale(language.toLowerCase());
		if (Lang.hasLocale(locale)) {
			Lang.setLanguage(player, locale);
			plugin.getCommandManager().setPlayerLocale(player, locale);
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.LANG_SET, locale).replace("{lang}", Lang.getDefinedLanguages().get(locale)));
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.LANG_NOT_FOUND, Lang.getLanguage(player)));
		}
	}
	
	@Subcommand("reload")
	@CommandPermission("aureliumskills.reload")
	@Description("Reloads the config, messages, menus, loot tables, action bars, boss bars, and health and luck stats.")
	public void reload(CommandSender sender) {
		Locale locale = Lang.getLanguage(sender);
		plugin.reloadConfig();
		plugin.saveDefaultConfig();
		AureliumSkills.optionLoader.loadOptions();
		lang.loadEmbeddedMessages(plugin.getCommandManager());
		lang.loadLanguages(plugin.getCommandManager());
		try {
			AureliumSkills.getMenuLoader().load();
		}
		catch (IllegalAccessException|InstantiationException e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("[AureliumSkills] Error while loading menus!");
		}
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
		// Resets all action bars
		plugin.getActionBar().resetActionBars();
		// Load boss bars
		plugin.getBossBar().loadOptions();
		sender.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.GREEN + Lang.getMessage(CommandMessage.RELOAD, locale));
	}
	
	@Subcommand("skill setlevel")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.skill.setlevel")
	@Description("Sets a specific skill to a level for a player.")
	public void onSkillSetlevel(CommandSender sender, @Flags("other") Player player, Skill skill, int level) {
		Locale locale = Lang.getLanguage(sender);
		if (OptionL.isEnabled(skill)) {
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				if (level > 0) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setSkillLevel(skill, level);
					playerSkill.setXp(skill, 0);
					Leveler.updateStats(player);
					Leveler.updateAbilities(player, skill);
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETLEVEL_SET, locale)
							.replace("{skill}", skill.getDisplayName(locale))
							.replace("{level}", String.valueOf(level))
							.replace("{player}", player.getName()));
				} else {
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETLEVEL_AT_LEAST_ONE, locale));
				}
			}
		}
		else {
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
		}
	}

	@Subcommand("skill setall")
	@CommandCompletion("@players")
	@CommandPermission("aureliumskills.skill.setlevel")
	@Description("Sets all of a player's skills to a level.")
	public void onSkillSetall(CommandSender sender, @Flags("other") Player player, int level) {
		Locale locale = Lang.getLanguage(sender);
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
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETALL_SET, locale)
					.replace("{level}", String.valueOf(level))
					.replace("{player}", player.getName()));
		} else {
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_SETALL_AT_LEAST_ONE, locale));
		}
	}


	@Subcommand("skill reset")
	@CommandCompletion("@players @skills")
	@CommandPermission("aureliumskills.skill.reset")
	@Description("Resets all skills or a specific skill to level 1 for a player.")
	public void onSkillReset(CommandSender sender, @Flags("other") Player player, @Optional Skill skill) {
		Locale locale = Lang.getLanguage(sender);
		if (skill != null) {
			if (OptionL.isEnabled(skill)) {
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					playerSkill.setSkillLevel(skill, 1);
					playerSkill.setXp(skill, 0);
					Leveler.updateStats(player);
					Leveler.updateAbilities(player, skill);
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_RESET_RESET_SKILL, locale)
							.replace("{skill}", skill.getDisplayName(locale))
							.replace("{player}", player.getName()));
				}
			} else {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UNKNOWN_SKILL, locale));
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
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SKILL_RESET_RESET_ALL, locale)
						.replace("{player}", player.getName()));
			}
		}
	}

	@Subcommand("modifier add")
	@CommandPermission("aureliumskills.modifier.add")
	@CommandCompletion("@players @stats @nothing @nothing true")
	@Description("Adds a stat modifier to a player.")
	public void onAdd(CommandSender sender, @Flags("other") Player player, Stat stat, String name, int value, @Default("false") boolean silent) {
		Locale locale = Lang.getLanguage(sender);
		if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
			StatModifier modifier = new StatModifier(name, stat, value);
			if (!playerStat.getModifiers().containsKey(name)) {
				playerStat.addModifier(modifier);
				if (!silent) {
					sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_ADD_ADDED, locale), modifier, player, locale));
				}
			}
			else {
				if (!silent) {
					sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_ADD_ALREADY_EXISTS, locale), modifier, player, locale));
				}
			}
		}
		else {
			if (!silent) {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
			}
		}
	}

	@Subcommand("modifier remove")
	@CommandPermission("aureliumskills.modifier.remove")
	@CommandCompletion("@players @modifiers true")
	@Description("Removes a specific stat modifier from a player.")
	public void onRemove(CommandSender sender, @Flags("other") Player player, String name, @Default("false") boolean silent) {
		Locale locale = Lang.getLanguage(sender);
		if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
			if (playerStat.removeModifier(name)) {
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

	@Subcommand("modifier list")
	@CommandCompletion("@players @stats")
	@CommandPermission("aureliumskills.modifier.list")
	@Description("Lists all or a specific stat's modifiers for a player.")
	public void onList(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat) {
		Locale locale = Lang.getLanguage(sender);
		if (player == null) {
			if (sender instanceof Player) {
				Player target = (Player) sender;
				if (SkillLoader.playerStats.containsKey(target.getUniqueId())) {
					PlayerStat targetStat = SkillLoader.playerStats.get(target.getUniqueId());
					String message;
					if (stat == null) {
						message = StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ALL_STATS_HEADER, locale), target);
						for (String key : targetStat.getModifiers().keySet()) {
							StatModifier modifier = targetStat.getModifiers().get(key);
							message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ALL_STATS_ENTRY, locale), modifier, target, locale);
						}
					} else {
						message = StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ONE_STAT_HEADER, locale), stat, target, locale);
						for (String key : targetStat.getModifiers().keySet()) {
							StatModifier modifier = targetStat.getModifiers().get(key);
							if (modifier.getStat() == stat) {
								message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ONE_STAT_ENTRY, locale), modifier, target, locale);
							}
						}
					}
					sender.sendMessage(message);
				} else {
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
				}
			}
			else {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MODIFIER_LIST_PLAYERS_ONLY, locale));
			}
		}
		else {
			if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
				PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
				String message;
				if (stat == null) {
					message = StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ALL_STATS_HEADER, locale), player);
					for (String key : playerStat.getModifiers().keySet()) {
						StatModifier modifier = playerStat.getModifiers().get(key);
						message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ALL_STATS_ENTRY, locale), modifier, player, locale);
					}
				} else {
					message = StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ONE_STAT_HEADER, locale), stat, player, locale);
					for (String key : playerStat.getModifiers().keySet()) {
						StatModifier modifier = playerStat.getModifiers().get(key);
						if (modifier.getStat() == stat) {
							message += "\n" + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_LIST_ONE_STAT_ENTRY, locale), modifier, player, locale);
						}
					}
				}
				sender.sendMessage(message);
			} else {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.NO_PROFILE, locale));
			}
		}
	}

	@Subcommand("modifier removeall")
	@CommandCompletion("@players @stats")
	@CommandPermission("aureliumskills.modifier.removeall")
	@Description("Removes all stat modifiers from a player.")
	public void onRemoveAll(CommandSender sender, @Flags("other") @Optional Player player, @Optional Stat stat, @Default("false") boolean silent) {
		Locale locale = Lang.getLanguage(sender);
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
			else {
				if (!silent) {
					sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MODIFIER_REMOVEALL_PLAYERS_ONLY, locale));
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
						sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_REMOVEALL_REMOVED_ALL_STATS, locale), player).replace("{num}", String.valueOf(removed)));
					}
					else {
						sender.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.MODIFIER_REMOVEALL_REMOVED_ONE_STAT, locale), stat, player, locale).replace("{num}", String.valueOf(removed)));
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

	@Subcommand("item modifier add")
	@CommandCompletion("@stats @nothing false|true")
	@CommandPermission("aureliumskills.item.modifier.add")
	@Description("Adds an item stat modifier to the item held, along with lore by default.")
	public void onItemModifierAdd(Player player, Stat stat, int value, @Default("true") boolean lore) {
		Locale locale = Lang.getLanguage(player);
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			for (StatModifier statModifier : ItemModifier.getItemModifiers(item)) {
				if (statModifier.getStat() == stat) {
					player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_ADD_ALREADY_EXISTS, locale), stat, locale));
					return;
				}
			}
			if (lore) {
				ItemModifier.addLore(item, stat, value);
			}
			ItemStack newItem = ItemModifier.addItemModifier(item, stat, value);
			player.getInventory().setItemInMainHand(newItem);
			player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_ADD_ADDED, locale), stat, value, locale));
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_MODIFIER_ADD_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("item modifier remove")
	@CommandCompletion("@stats false|true")
	@CommandPermission("aureliumskills.item.modifier.remove")
	@Description("Removes an item stat modifier from the item held, and the lore associated with it by default.")
	public void onItemModifierRemove(Player player, Stat stat, @Default("true") boolean lore) {
		Locale locale = Lang.getLanguage(player);
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
				player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVE_REMOVED, locale), stat, locale));
			}
			else {
				player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), stat, locale));
			}
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVE_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("item modifier list")
	@CommandPermission("aureliumskills.item.modifier.list")
	@Description("Lists all item stat modifiers on the item held.")
	public void onItemModifierList(Player player) {
		Locale locale = Lang.getLanguage(player);
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			StringBuilder message = new StringBuilder(Lang.getMessage(CommandMessage.ITEM_MODIFIER_LIST_HEADER, locale));
			for (StatModifier modifier : ItemModifier.getItemModifiers(item)) {
				message.append("\n").append(StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ITEM_MODIFIER_LIST_ENTRY, locale), modifier, locale));
			}
			player.sendMessage(message.toString());
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_MODIFIER_LIST_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("item modifier removeall")
	@CommandPermission("aureliumskills.item.modifier.removall")
	@Description("Removes all item stat modifiers from the item held.")
	public void onItemModifierRemoveAll(Player player) {
		Locale locale = Lang.getLanguage(player);
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = ItemModifier.removeAllItemModifiers(player.getInventory().getItemInMainHand());
			player.getInventory().setItemInMainHand(item);
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVEALL_REMOVED, locale));
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ITEM_MODIFIER_REMOVEALL_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("armor modifier add")
	@CommandCompletion("@stats @nothing false|true")
	@CommandPermission("aureliumskills.armor.modifier.add")
	@Description("Adds an armor stat modifier to the item held, along with lore by default.")
	public void onArmorModifierAdd(Player player, Stat stat, int value, @Default("true") boolean lore) {
		Locale locale = Lang.getLanguage(player);
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			for (StatModifier statModifier : ArmorModifier.getArmorModifiers(item)) {
				if (statModifier.getStat() == stat) {
					player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ARMOR_MODIFIER_ADD_ALREADY_EXISTS, locale), stat, locale));
					return;
				}
			}
			if (lore) {
				ArmorModifier.addLore(item, stat, value);
			}
			ItemStack newItem = ArmorModifier.addArmorModifier(item, stat, value);
			player.getInventory().setItemInMainHand(newItem);
			player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ARMOR_MODIFIER_ADD_ADDED, locale), stat, value, locale));
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ARMOR_MODIFIER_ADD_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("armor modifier remove")
	@CommandCompletion("@stats false|true")
	@CommandPermission("aureliumskills.armor.modifier.remove")
	@Description("Removes an armor stat modifier from the item held, and the lore associated with it by default.")
	public void onArmorModifierRemove(Player player, Stat stat, @Default("true") boolean lore) {
		Locale locale = Lang.getLanguage(player);
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
				player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ARMOR_MODIFIER_REMOVE_REMOVED, locale), stat, locale));
			}
			else {
				player.sendMessage(AureliumSkills.getPrefix(locale) + StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ARMOR_MODIFIER_REMOVE_DOES_NOT_EXIST, locale), stat, locale));
			}
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ARMOR_MODIFIER_REMOVE_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("armor modifier list")
	@CommandPermission("aureliumskills.armor.modifier.list")
	@Description("Lists all armor stat modifiers on the item held.")
	public void onArmorModifierList(Player player) {
		Locale locale = Lang.getLanguage(player);
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			StringBuilder message = new StringBuilder(Lang.getMessage(CommandMessage.ARMOR_MODIFIER_LIST_HEADER, locale));
			for (StatModifier modifier : ArmorModifier.getArmorModifiers(item)) {
				message.append("\n").append(StatModifier.applyPlaceholders(Lang.getMessage(CommandMessage.ARMOR_MODIFIER_LIST_ENTRY, locale), modifier, locale));
			}
			player.sendMessage(message.toString());
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ARMOR_MODIFIER_LIST_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("armor modifier removeall")
	@CommandPermission("aureliumskills.armor.modifier.removeall")
	@Description("Removes all armor stat modifiers from the item held.")
	public void onArmorModifierRemoveAll(Player player) {
		Locale locale = Lang.getLanguage(player);
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			ItemStack item = ArmorModifier.removeAllArmorModifiers(player.getInventory().getItemInMainHand());
			player.getInventory().setItemInMainHand(item);
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ARMOR_MODIFIER_REMOVEALL_REMOVED, locale));
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.ARMOR_MODIFIER_REMOVEALL_MUST_HOLD_ITEM, locale));
		}
	}

	@Subcommand("multiplier")
	@CommandCompletion("@players")
	@CommandPermission("aureliumskills.multipliercommand")
	@Description("Shows a player's current XP multiplier based on their permissions.")
	public void onMultiplier(CommandSender sender, @Optional @Flags("other") Player player) {
		if (player == null) {
			if (sender instanceof Player) {
				Player target = (Player) sender;
				Locale locale = Lang.getLanguage(sender);
				double multiplier = Leveler.getMultiplier(target);
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MULTIPLIER_LIST, locale)
						.replace("{player}", target.getName())
						.replace("{multiplier}", String.valueOf(multiplier))
						.replace("{percent}", String.valueOf((multiplier - 1) * 100)));
			}
			else {
				Locale locale = Locale.ENGLISH;
				sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MULTIPLIER_PLAYERS_ONLY, locale));
			}
		}
		else {
			Locale locale = Lang.getLanguage(player);
			double multiplier = Leveler.getMultiplier(player);
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.MULTIPLIER_LIST, locale)
					.replace("{player}", player.getName())
					.replace("{multiplier}", String.valueOf(multiplier))
					.replace("{percent}", String.valueOf((multiplier - 1) * 100)));
		}
	}

	@Subcommand("help")
	@CommandPermission("aureliumskills.help")
	public void onHelp(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}
}
