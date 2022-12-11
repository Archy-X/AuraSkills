package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.item.UnclaimedItemsMenu;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.leaderboard.SkillValue;
import com.archyx.aureliumskills.menus.sources.SorterItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.ui.ActionBar;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.aureliumskills.util.version.UpdateChecker;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@CommandAlias("%skills_alias")
public class SkillsRootCommand extends BaseCommand {
 
	private final AureliumSkills plugin;
	private final ReloadManager reloadManager;

	public SkillsRootCommand(AureliumSkills plugin) {
		this.plugin = plugin;
		this.reloadManager = new ReloadManager(plugin);
	}

	@Default
	@CommandPermission("aureliumskills.skills")
	@Description("Opens the Skills menu, where you can browse skills, progress, and abilities.")
	public void onSkills(Player player) {
		if (plugin.getPlayerManager().hasPlayerData(player)) {
			plugin.getMenuManager().openMenu(player, "skills");
		} else {
			player.sendMessage(Lang.getMessage(CommandMessage.NO_PROFILE, Lang.getDefaultLanguage()));
		}
	}

	@Subcommand("top")
	@CommandAlias("skilltop")
	@CommandCompletion("@skillTop")
	@CommandPermission("aureliumskills.top")
	@Description("Shows the top players in a skill")
	@Syntax("Usage: /sk top <page> or /sk top [skill] <page>")
	public void onTop(CommandSender sender, String[] args) {
		Locale locale = plugin.getLang().getLocale(sender);
		if (args.length == 0) {
			List<SkillValue> lb = plugin.getLeaderboardManager().getPowerLeaderboard(1, 10);
			sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_HEADER, locale));
			for (SkillValue skillValue : lb) {
				String name = Bukkit.getOfflinePlayer(skillValue.getId()).getName();
				sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_ENTRY, locale)
						.replace("{rank}", String.valueOf(lb.indexOf(skillValue) + 1))
						.replace("{player}", name != null ? name : "?")
						.replace("{level}", String.valueOf(skillValue.getLevel())));
			}
		}
		else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("average")) {
				List<SkillValue> lb = plugin.getLeaderboardManager().getAverageLeaderboard(1, 10);
				sender.sendMessage(Lang.getMessage(CommandMessage.TOP_AVERAGE_HEADER, locale));
				sendLeaderboardEntries(sender, locale, lb);
			} else {
				try {
					int page = Integer.parseInt(args[0]);
					List<SkillValue> lb = plugin.getLeaderboardManager().getPowerLeaderboard(page, 10);
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_HEADER_PAGE, locale).replace("{page}", String.valueOf(page)));
					for (SkillValue skillValue : lb) {
						String name = Bukkit.getOfflinePlayer(skillValue.getId()).getName();
						sender.sendMessage(Lang.getMessage(CommandMessage.TOP_POWER_ENTRY, locale)
								.replace("{rank}", String.valueOf((page - 1) * 10 + lb.indexOf(skillValue) + 1))
								.replace("{player}", name != null ? name : "?")
								.replace("{level}", String.valueOf(skillValue.getLevel())));
					}
				} catch (Exception e) {
					Skill skill = plugin.getSkillRegistry().getSkill(args[0]);
					if (skill != null) {
						List<SkillValue> lb = plugin.getLeaderboardManager().getLeaderboard(skill, 1, 10);
						sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_HEADER, locale).replace("{skill}", skill.getDisplayName(locale)));
						for (SkillValue skillValue : lb) {
							String name = Bukkit.getOfflinePlayer(skillValue.getId()).getName();
							sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_ENTRY, locale)
									.replace("{rank}", String.valueOf(lb.indexOf(skillValue) + 1))
									.replace("{player}", name != null ? name : "?")
									.replace("{level}", String.valueOf(skillValue.getLevel())));
						}
					} else {
						sender.sendMessage(Lang.getMessage(CommandMessage.TOP_USAGE, locale));
					}
				}
			}
		}
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("average")) {
				try {
					int page = Integer.parseInt(args[1]);
					List<SkillValue> lb = plugin.getLeaderboardManager().getAverageLeaderboard(page, 10);
					sender.sendMessage(TextUtil.replace(Lang.getMessage(CommandMessage.TOP_AVERAGE_HEADER_PAGE, locale),
							"{page}", String.valueOf(page)));
					sendLeaderboardEntries(sender, locale, lb);
				} catch (Exception e) {
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_USAGE, locale));
				}
			} else {
				Skill skill = plugin.getSkillRegistry().getSkill(args[0]);
				if (skill != null) {
					try {
						int page = Integer.parseInt(args[1]);
						List<SkillValue> lb = plugin.getLeaderboardManager().getLeaderboard(skill, page, 10);
						sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_HEADER_PAGE, locale).replace("{page}", String.valueOf(page)).replace("{skill}", skill.getDisplayName(locale)));
						for (SkillValue skillValue : lb) {
							String name = Bukkit.getOfflinePlayer(skillValue.getId()).getName();
							sender.sendMessage(Lang.getMessage(CommandMessage.TOP_SKILL_ENTRY, locale)
									.replace("{rank}", String.valueOf((page - 1) * 10 + lb.indexOf(skillValue) + 1))
									.replace("{player}", name != null ? name : "?")
									.replace("{level}", String.valueOf(skillValue.getLevel())));
						}
					} catch (Exception e) {
						sender.sendMessage(Lang.getMessage(CommandMessage.TOP_USAGE, locale));
					}
				} else {
					sender.sendMessage(Lang.getMessage(CommandMessage.TOP_USAGE, locale));
				}
			}
		}
	}

	private void sendLeaderboardEntries(CommandSender sender, Locale locale, List<SkillValue> lb) {
		for (SkillValue skillValue : lb) {
			String name = Bukkit.getOfflinePlayer(skillValue.getId()).getName();
			sender.sendMessage(TextUtil.replace(Lang.getMessage(CommandMessage.TOP_AVERAGE_ENTRY, locale),
					"{rank}", String.valueOf(lb.indexOf(skillValue) + 1),
					"{player}", name != null ? name : "?",
					"{level}", NumberUtil.format2(skillValue.getXp())));
		}
	}


	@Subcommand("save")
	@CommandPermission("aureliumskills.save")
	@Description("Saves skill data")
	public void onSave(CommandSender sender) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Locale locale = plugin.getLang().getLocale(sender);
				for (Player player : Bukkit.getOnlinePlayers()) {
					plugin.getStorageProvider().save(player, false);
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.SAVE_SAVED, locale));
					}
				}.runTask(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}

	@Subcommand("updateleaderboards")
	@CommandPermission("aureliumskills.updateleaderboards")
	@Description("Updates and sorts the leaderboards")
	public void onUpdateLeaderboards(CommandSender sender) {
		Locale locale = plugin.getLang().getLocale(sender);
		if (plugin.getLeaderboardManager().isNotSorting()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					plugin.getStorageProvider().updateLeaderboards();
					new BukkitRunnable() {
						@Override
						public void run() {
							sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UPDATELEADERBOARDS_UPDATED, locale));
						}
					}.runTask(plugin);
				}
			}.runTaskAsynchronously(plugin);
		} else {
			sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.UPDATELEADERBOARDS_ALREADY_UPDATING, locale));
		}
	}

	@Subcommand("toggle")
	@CommandAlias("abtoggle")
	@CommandPermission("aureliumskills.abtoggle")
	@Description("Toggle your own action bar")
	public void onActionBarToggle(Player player) {
		Locale locale = plugin.getLang().getLocale(player);
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
		Locale locale = plugin.getLang().getLocale(player);
		player.sendMessage(Lang.getMessage(CommandMessage.RANK_HEADER, locale));
		player.sendMessage(Lang.getMessage(CommandMessage.RANK_POWER, locale)
				.replace("{rank}", String.valueOf(plugin.getLeaderboardManager().getPowerRank(player.getUniqueId())))
				.replace("{total}", String.valueOf(plugin.getLeaderboardManager().getPowerLeaderboard().size())));
		for (Skill skill : plugin.getSkillRegistry().getSkills()) {
			if (OptionL.isEnabled(skill)) {
				player.sendMessage(Lang.getMessage(CommandMessage.RANK_ENTRY, locale)
						.replace("{skill}", String.valueOf(skill.getDisplayName(locale)))
						.replace("{rank}", String.valueOf(plugin.getLeaderboardManager().getSkillRank(skill, player.getUniqueId())))
						.replace("{total}", String.valueOf(plugin.getLeaderboardManager().getLeaderboard(skill).size())));
			}
		}
	}

	@Subcommand("lang")
	@CommandCompletion("@lang")
	@CommandPermission("aureliumskills.lang")
	@Description("Changes your player language")
	public void onLanguage(Player player, String language) {
		Locale locale = new Locale(language.toLowerCase(Locale.ENGLISH));
		if (Lang.hasLocale(locale)) {
			PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
			if (playerData == null) return;
			playerData.setLocale(locale);
			plugin.getCommandManager().setPlayerLocale(player, locale);
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.LANG_SET, locale).replace("{lang}", Lang.getDefinedLanguages().get(locale)));
		}
		else {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.LANG_NOT_FOUND, plugin.getLang().getLocale(player)));
		}
	}
	
	@Subcommand("reload")
	@CommandPermission("aureliumskills.reload")
	@Description("Reloads the config, messages, menus, loot tables, action bars, boss bars, and health and luck stats.")
	public void reload(CommandSender sender) {
		reloadManager.reload(sender);
	}

	@Subcommand("multiplier")
	@CommandCompletion("@players")
	@CommandPermission("aureliumskills.multipliercommand")
	@Description("Shows a player's current XP multiplier based on their permissions.")
	public void onMultiplier(CommandSender sender, @Optional @Flags("other") Player player) {
		Player target;
		if (player == null) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				sender.sendMessage(AureliumSkills.getPrefix(Lang.getDefaultLanguage()) + Lang.getMessage(CommandMessage.MULTIPLIER_PLAYERS_ONLY, Lang.getDefaultLanguage()));
				return;
			}
		} else {
			target = player;
		}
		Locale locale = plugin.getLang().getLocale(target);
		double multiplier = plugin.getLeveler().getMultiplier(target);
		sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MULTIPLIER_LIST, locale),
				"{player}", target.getName(),
				"{multiplier}", NumberUtil.format2(multiplier),
				"{percent}", NumberUtil.format2((multiplier - 1) * 100)));
		// Send skill specific multipliers if different from global
		for (Skill skill : plugin.getSkillRegistry().getSkills()) {
			double skillMultiplier = plugin.getLeveler().getMultiplier(target, skill);
			if (skillMultiplier != multiplier) {
				sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.MULTIPLIER_SKILL_ENTRY, locale),
						"{skill}", skill.getDisplayName(locale),
						"{multiplier}", NumberUtil.format2(skillMultiplier),
						"{percent}", NumberUtil.format2((skillMultiplier - 1) * 100)));
			}
		}
	}

	@Subcommand("resethealth")
	@CommandPermission("aureliumskills.*")
	@Description("Removes all attribute modifiers by Aurelium Skills for easy uninstalling. This only works on offline players.")
	public void onResetHealth(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			File playerDataFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
			int successful = 0;
			int error = 0;
			int total = 0;
			for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				if (!player.isOnline()) {
					total++;
					File playerFile = new File(playerDataFolder, player.getUniqueId() + ".dat");
					if (playerFile.exists() && playerFile.canWrite()) {
						try {
							NBTFile nbtFile = new NBTFile(playerFile);
							NBTCompoundList compoundList = nbtFile.getCompoundList("Attributes");
							if (compoundList != null) {
								final AtomicBoolean save = new AtomicBoolean(false);
								for (ReadWriteNBT listCompound : compoundList.subList(0, compoundList.size())) {
									switch (listCompound.getString("Name")) {
										case "generic.maxHealth":
										case "minecraft:generic.max_health": {
											ReadWriteNBTCompoundList modifierList = listCompound.getCompoundList("Modifiers");
											if (modifierList != null) {
												modifierList.removeIf((modifier) -> {
													if (modifier.getString("Name").equals("skillsHealth")) {
														save.set(true);
														return true;
													} else {
														return false;
													}
												});
												if (modifierList.size() == 0) {
													listCompound.removeKey("Modifiers");
												}
											}
											break;
										}
										case "generic.luck":
										case "minecraft:generic.luck": {
											ReadWriteNBTCompoundList modifierList = listCompound.getCompoundList("Modifiers");
											if (modifierList != null) {
												modifierList.removeIf((modifier) -> {
													if (modifier.getString("Name").equals("AureliumSkills-Luck")) {
														save.set(true);
														return true;
													} else {
														return false;
													}
												});
												if (modifierList.size() == 0) {
													listCompound.removeKey("Modifiers");
												}
											}
											break;
										}
										case "generic.attackSpeed":
										case "minecraft:generic.attack_speed": {
											ReadWriteNBTCompoundList modifierList = listCompound.getCompoundList("Modifiers");
											if (modifierList != null) {
												modifierList.removeIf((modifier) -> {
													if (modifier.getString("Name").equals("AureliumSkills-LightningBlade")) {
														save.set(true);
														return true;
													} else {
														return false;
													}
												});
												if (modifierList.size() == 0) {
													listCompound.removeKey("Modifiers");
												}
											}
											break;
										}
									}
								}
								if (save.get()) {
									nbtFile.save();
									successful++;
								}
							}
						} catch (Exception e) {
							error++;
						}
					}
				}
			}
			sender.sendMessage("Searched " + total + " offline players. Successfully removed attributes from " + successful + " players. Failed to remove on " + error + " players.");
		} else {
			sender.sendMessage(ChatColor.RED + "Only console may execute this command!");
		}
	}

	@Subcommand("claimitems")
	@CommandPermission("aureliumskills.claimitems")
	public void onClaimItems(Player player) {
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
		Locale locale = Lang.getDefaultLanguage();
		if (playerData != null) {
			locale = playerData.getLocale();
		}
		if (playerData == null || playerData.getUnclaimedItems().size() == 0) {
			player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.CLAIMITEMS_NO_ITEMS, locale));
			return;
		}
		UnclaimedItemsMenu.getInventory(plugin, playerData).open(player);
	}

	@Subcommand("version")
	@CommandPermission("aureliumskills.version")
	public void onVersion(CommandSender sender) {
		Locale locale = plugin.getLang().getLocale(sender);
		new UpdateChecker(plugin, 81069).getVersion(latestVersion -> sender.sendMessage(AureliumSkills.getPrefix(locale) +
				TextUtil.replace(Lang.getMessage(CommandMessage.VERSION, locale),
				"{current_version}", plugin.getDescription().getVersion(),
				"{latest_version}", latestVersion)));
	}

	@Subcommand("sources")
	@CommandPermission("aureliumskills.sources")
	@CommandCompletion("@skills @sort_types")
	public void onSources(Player player, Skill skill, @Optional SorterItem.SortType sortType) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("skill", skill);
		properties.put("items_per_page", 28);
		if (sortType == null) { // Use ASCENDING as default
			sortType = SorterItem.SortType.ASCENDING;
		}
		properties.put("sort_type", sortType);
		plugin.getMenuManager().openMenu(player, "sources", properties);
	}

	@Subcommand("help")
	@CommandPermission("aureliumskills.help")
	public void onHelp(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}
}
