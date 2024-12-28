package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.UnclaimedItemsMenu;
import dev.aurelium.auraskills.bukkit.menus.SourcesMenu.SortType;
import dev.aurelium.auraskills.bukkit.storage.Uninstaller;
import dev.aurelium.auraskills.bukkit.util.UpdateChecker;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.leaderboard.SkillValue;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.ui.ActionBarType;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserState;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@CommandAlias("%skills_alias")
public class SkillsRootCommand extends BaseCommand {
 
	private final AuraSkills plugin;

	public SkillsRootCommand(AuraSkills plugin) {
		this.plugin = plugin;
	}

	@Default
	@CommandPermission("auraskills.command.skills")
	@Description("Opens the Skills menu, where you can browse skills, progress, and abilities.")
	public void onSkills(Player player) {
		if (plugin.getUserManager().hasUser(player.getUniqueId())) {
			plugin.getSlate().openMenu(player, "skills");
		} else {
			player.sendMessage(plugin.getMsg(CommandMessage.NO_PROFILE, plugin.getDefaultLanguage()));
		}
	}

	@Subcommand("reload")
	@CommandPermission("auraskills.command.reload")
	@Description("Reloads the config, messages, menus, loot tables, action bars, boss bars, and health and luck stats.")
	public void reload(CommandSender sender) {
		new ReloadExecutor(plugin).reload(sender);
	}

	@Subcommand("help")
	@CommandPermission("auraskills.command.help")
	public void onHelp(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("top")
	@CommandAlias("skilltop")
	@CommandCompletion("@skill_top")
	@CommandPermission("auraskills.command.top")
	@Description("Shows the top players in a skill")
	@Syntax("Usage: /sk top <page> or /sk top [skill] <page>")
	public void onTop(CommandSender sender, String[] args) {
		Locale locale = plugin.getLocale(sender);
		if (args.length == 0) {
			List<SkillValue> lb = plugin.getLeaderboardManager().getPowerLeaderboard(1, 10);
			sender.sendMessage(plugin.getMsg(CommandMessage.TOP_POWER_HEADER, locale));
			for (SkillValue skillValue : lb) {
				String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
				sender.sendMessage(plugin.getMsg(CommandMessage.TOP_POWER_ENTRY, locale)
						.replace("{rank}", String.valueOf(lb.indexOf(skillValue) + 1))
						.replace("{player}", name != null ? name : "?")
						.replace("{level}", String.valueOf(skillValue.level())));
			}
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("average")) {
				List<SkillValue> lb = plugin.getLeaderboardManager().getAverageLeaderboard(1, 10);
				sender.sendMessage(plugin.getMsg(CommandMessage.TOP_AVERAGE_HEADER, locale));
				sendLeaderboardEntries(sender, locale, lb);
			} else {
				try {
					int page = Integer.parseInt(args[0]);
					List<SkillValue> lb = plugin.getLeaderboardManager().getPowerLeaderboard(page, 10);
					sender.sendMessage(plugin.getMsg(CommandMessage.TOP_POWER_HEADER_PAGE, locale).replace("{page}", String.valueOf(page)));
					for (SkillValue skillValue : lb) {
						String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
						sender.sendMessage(plugin.getMsg(CommandMessage.TOP_POWER_ENTRY, locale)
								.replace("{rank}", String.valueOf((page - 1) * 10 + lb.indexOf(skillValue) + 1))
								.replace("{player}", name != null ? name : "?")
								.replace("{level}", String.valueOf(skillValue.level())));
					}
				} catch (Exception e) {
					String skillName = args[0].toLowerCase(Locale.ROOT);
					Skill skill = plugin.getSkillRegistry().getFromKey(skillName);
					if (skill == null) {
						skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillName));
					}
					if (skill != null && skill.isEnabled()) {
						List<SkillValue> lb = plugin.getLeaderboardManager().getLeaderboard(skill, 1, 10);
						sender.sendMessage(plugin.getMsg(CommandMessage.TOP_SKILL_HEADER, locale).replace("{skill}", skill.getDisplayName(locale)));
						for (SkillValue skillValue : lb) {
							String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
							sender.sendMessage(plugin.getMsg(CommandMessage.TOP_SKILL_ENTRY, locale)
									.replace("{rank}", String.valueOf(lb.indexOf(skillValue) + 1))
									.replace("{player}", name != null ? name : "?")
									.replace("{level}", String.valueOf(skillValue.level())));
						}
					} else {
						sender.sendMessage(plugin.getMsg(CommandMessage.TOP_USAGE, locale));
					}
				}
			}
		}
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("average")) {
				try {
					int page = Integer.parseInt(args[1]);
					List<SkillValue> lb = plugin.getLeaderboardManager().getAverageLeaderboard(page, 10);
					sender.sendMessage(TextUtil.replace(plugin.getMsg(CommandMessage.TOP_AVERAGE_HEADER_PAGE, locale),
							"{page}", String.valueOf(page)));
					sendLeaderboardEntries(sender, locale, lb);
				} catch (Exception e) {
					sender.sendMessage(plugin.getMsg(CommandMessage.TOP_USAGE, locale));
				}
			} else {
				Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(args[0]));
				if (skill != null) {
					try {
						int page = Integer.parseInt(args[1]);
						List<SkillValue> lb = plugin.getLeaderboardManager().getLeaderboard(skill, page, 10);
						sender.sendMessage(plugin.getMsg(CommandMessage.TOP_SKILL_HEADER_PAGE, locale).replace("{page}", String.valueOf(page)).replace("{skill}", skill.getDisplayName(locale)));
						for (SkillValue skillValue : lb) {
							String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
							sender.sendMessage(plugin.getMsg(CommandMessage.TOP_SKILL_ENTRY, locale)
									.replace("{rank}", String.valueOf((page - 1) * 10 + lb.indexOf(skillValue) + 1))
									.replace("{player}", name != null ? name : "?")
									.replace("{level}", String.valueOf(skillValue.level())));
						}
					} catch (Exception e) {
						sender.sendMessage(plugin.getMsg(CommandMessage.TOP_USAGE, locale));
					}
				} else {
					sender.sendMessage(plugin.getMsg(CommandMessage.TOP_USAGE, locale));
				}
			}
		}
	}

	private void sendLeaderboardEntries(CommandSender sender, Locale locale, List<SkillValue> lb) {
		for (SkillValue skillValue : lb) {
			String name = Bukkit.getOfflinePlayer(skillValue.id()).getName();
			sender.sendMessage(TextUtil.replace(plugin.getMsg(CommandMessage.TOP_AVERAGE_ENTRY, locale),
					"{rank}", String.valueOf(lb.indexOf(skillValue) + 1),
					"{player}", name != null ? name : "?",
					"{level}", NumberUtil.format2(skillValue.xp())));
		}
	}

	@Subcommand("save")
	@CommandPermission("auraskills.command.save")
	@Description("Saves skill data")
	public void onSave(CommandSender sender) {
		plugin.getScheduler().executeAsync(() -> {
			Locale locale = plugin.getLocale(sender);
			for (User user : plugin.getUserManager().getOnlineUsers()) {
				try {
					plugin.getStorageProvider().saveSafely(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			plugin.getScheduler().executeSync(() -> sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.SAVE_SAVED, locale)));
		});
	}

	@Subcommand("updateleaderboards")
	@CommandPermission("auraskills.command.updateleaderboards")
	@Description("Updates and sorts the leaderboards")
	public void onUpdateLeaderboards(CommandSender sender) {
		Locale locale = plugin.getLocale(sender);
		if (plugin.getLeaderboardManager().isNotSorting()) {
			plugin.getScheduler().executeAsync(() -> {
				plugin.getLeaderboardManager().updateLeaderboards();

				plugin.getScheduler().executeSync(() -> sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.UPDATELEADERBOARDS_UPDATED, locale)));
			});
		} else {
			sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.UPDATELEADERBOARDS_ALREADY_UPDATING, locale));
		}
	}

	@Subcommand("toggle")
	@CommandAlias("abtoggle")
	@CommandPermission("auraskills.command.abtoggle")
	@Description("Toggle your own action bar")
	public void onActionBarToggle(Player player) {
		User user = plugin.getUser(player);
		Locale locale = user.getLocale();
		if (plugin.configBoolean(Option.ACTION_BAR_ENABLED)) {
			if (!user.isActionBarEnabled(ActionBarType.IDLE)) {
				user.setActionBarSetting(ActionBarType.IDLE, true);
				player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.TOGGLE_ENABLED, locale));
			} else {
				user.setActionBarSetting(ActionBarType.IDLE, false);
				player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.TOGGLE_DISABLED, locale));
			}
		} else {
			player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.TOGGLE_NOT_ENABLED, locale));
		}
	}

	@Subcommand("rank")
	@CommandAlias("skillrank")
	@CommandPermission("auraskills.command.rank")
	@Description("Shows your skill rankings")
	public void onRank(Player player) {
		Locale locale = plugin.getLocale(player);
		player.sendMessage(plugin.getMsg(CommandMessage.RANK_HEADER, locale));
		player.sendMessage(plugin.getMsg(CommandMessage.RANK_POWER, locale)
				.replace("{rank}", String.valueOf(plugin.getLeaderboardManager().getPowerRank(player.getUniqueId())))
				.replace("{total}", String.valueOf(plugin.getLeaderboardManager().getPowerLeaderboard().size())));
		for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
			player.sendMessage(plugin.getMsg(CommandMessage.RANK_ENTRY, locale)
					.replace("{skill}", String.valueOf(skill.getDisplayName(locale)))
					.replace("{rank}", String.valueOf(plugin.getLeaderboardManager().getSkillRank(skill, player.getUniqueId())))
					.replace("{total}", String.valueOf(plugin.getLeaderboardManager().getLeaderboard(skill).size())));
		}
	}

	@Subcommand("lang")
	@CommandCompletion("@lang")
	@CommandPermission("auraskills.command.lang")
	@Description("Changes your player language")
	public void onLanguage(Player player, String language) {
		Locale locale = new Locale(language.toLowerCase(Locale.ROOT));
		if (plugin.getMessageProvider().hasLocale(locale)) {
			User user = plugin.getUser(player);
			user.setLocale(locale);
			plugin.getCommandManager().setPlayerLocale(player, locale);

			String code = plugin.getMessageProvider().getLanguageCode(locale);
			player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.LANG_SET, locale).replace("{lang}", code));
		} else {
			player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.LANG_NOT_FOUND, plugin.getLocale(player)));
		}
	}

	@Subcommand("multiplier")
	@CommandCompletion("@players")
	@CommandPermission("auraskills.command.multiplier")
	@Description("Shows a player's current XP multiplier based on their permissions.")
	public void onMultiplier(CommandSender sender, @Optional @Flags("other") Player player) {
		Player target;
		if (player == null) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				sender.sendMessage(plugin.getPrefix(plugin.getDefaultLanguage()) + plugin.getMsg(CommandMessage.MULTIPLIER_PLAYERS_ONLY, plugin.getDefaultLanguage()));
				return;
			}
		} else {
			target = player;
		}
		User user = plugin.getUser(target);
		Locale locale = user.getLocale();
		double multiplier = plugin.getLevelManager().getPermissionMultiplier(user, null);
		sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.MULTIPLIER_LIST, locale),
				"{player}", target.getName(),
				"{multiplier}", NumberUtil.format2(1 + multiplier),
				"{percent}", NumberUtil.format2(multiplier * 100)));
		// Send skill specific multipliers if different from global
		for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
			double skillMultiplier = plugin.getLevelManager().getPermissionMultiplier(user, skill);
			if (skillMultiplier != multiplier) {
				sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.MULTIPLIER_SKILL_ENTRY, locale),
						"{skill}", skill.getDisplayName(locale),
						"{multiplier}", NumberUtil.format2(1 + skillMultiplier),
						"{percent}", NumberUtil.format2(skillMultiplier * 100)));
			}
		}
	}

	@Subcommand("resethealth")
	@CommandPermission("auraskills.command.resethealth")
	@Description("Removes all attribute modifiers by Aurelium Skills for easy uninstalling. This only works on offline players.")
	public void onResetHealth(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
			Uninstaller uninstaller = new Uninstaller();
			uninstaller.removeAttributes(sender);
		} else {
			sender.sendMessage(ChatColor.RED + "Only console may execute this command!");
		}
	}

	@Subcommand("claimitems")
	@CommandPermission("auraskills.command.claimitems")
	public void onClaimItems(Player player) {
		User user = plugin.getUser(player);
		Locale locale = user.getLocale();
		if (user.getUnclaimedItems().isEmpty()) {
			player.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.CLAIMITEMS_NO_ITEMS, locale));
			return;
		}
		UnclaimedItemsMenu.getInventory(plugin, user).open(player);
	}

	@Subcommand("version")
	@CommandPermission("auraskills.command.version")
	public void onVersion(CommandSender sender) {
		Locale locale = plugin.getLocale(sender);
		new UpdateChecker(plugin).getVersion((latestVersion, id) -> sender.sendMessage(plugin.getPrefix(locale) +
				TextUtil.replace(plugin.getMsg(CommandMessage.VERSION, locale),
						"{current_version}", plugin.getDescription().getVersion(),
						"{latest_version}", latestVersion)));
	}

	@Subcommand("sources")
	@CommandPermission("auraskills.command.sources")
	@CommandCompletion("@skills @sort_types")
	public void onSources(Player player, Skill skill, @Optional SortType sortType) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("skill", skill);
		properties.put("items_per_page", 28);
		if (sortType == null) { // Use ASCENDING as default
			sortType = SortType.ASCENDING;
		}
		properties.put("sort_type", sortType);
		plugin.getSlate().openMenu(player, "sources", properties);
	}

	@Subcommand("transfer")
	@CommandPermission("auraskills.command.transfer")
	public void onTransfer(CommandSender sender, UUID playerFrom, UUID playerTo) {
		OfflinePlayer offPlayerFrom = Bukkit.getOfflinePlayer(playerFrom);
		OfflinePlayer offPlayerTo = Bukkit.getOfflinePlayer(playerTo);

		Locale locale = plugin.getLocale(sender);

		CompletableFuture<UserState> future = getPlayerDataState(offPlayerFrom);
		future.thenAcceptAsync((oldState) -> {
			if (oldState == null) {
				Bukkit.getLogger().warning("Error transferring player data: Player data not found for player " + playerFrom);
				sender.sendMessage(plugin.getPrefix(locale) +
						TextUtil.replace(plugin.getMsg(CommandMessage.TRANSFER_SUCCESS, locale),
								"{from}", playerFrom.toString(), "{to}", playerTo.toString()));
				return;
			}
			// Create a new PlayerDataStat with the UUID changed to the playerTo UUID
			UserState newState = new UserState(playerTo, oldState.skillLevels(), oldState.skillXp(), oldState.statModifiers(), oldState.traitModifiers(), oldState.mana());

			if (offPlayerTo.isOnline()) { // Handle online transfer
				User user = plugin.getUserManager().getUser(playerTo);
				if (user != null) {
					user.applyState(newState);
					// Successful
					sender.sendMessage(plugin.getPrefix(locale) +
							TextUtil.replace(plugin.getMsg(CommandMessage.TRANSFER_SUCCESS, locale),
									"{from}", playerFrom.toString(), "{to}", playerTo.toString()));
				} else {
					plugin.logger().warn("Error transferring player data: Player data not found for player " + playerTo);
					sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.TRANSFER_ERROR, locale));
				}
			} else { // Handle offline transfer
				try {
					plugin.getStorageProvider().applyState(newState);
					sender.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.TRANSFER_SUCCESS, locale),
							"{from}", playerFrom.toString(), "{to}", playerTo.toString()));
				} catch (Exception e) {
					sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.TRANSFER_ERROR, locale));
				}
			}
		});
	}

	// PlayerDataState may return null
	private CompletableFuture<UserState> getPlayerDataState(OfflinePlayer player) {
		CompletableFuture<UserState> future = new CompletableFuture<>();
		if (player.isOnline()) {
			User user = plugin.getUserManager().getUser(player.getUniqueId());
			if (user != null) {
				future.complete(user.getState());
			} else {
				future.complete(null);
			}
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						future.complete(plugin.getStorageProvider().loadState(player.getUniqueId()));
					} catch (Exception e) {
						future.complete(null);
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(plugin);
		}
		return future;
	}
	
}
