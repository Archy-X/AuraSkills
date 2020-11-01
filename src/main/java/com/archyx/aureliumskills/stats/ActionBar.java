package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.ActionBarMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.magic.ManaManager;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.util.BigNumber;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.ProtocolUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

public class ActionBar {

	private final Plugin plugin;
	private ManaManager mana;
	private static final NumberFormat nf = new DecimalFormat("##.#");

	public ActionBar(Plugin plugin) {
		this.plugin = plugin;
	}
	
	private final HashSet<Player> isGainingXp = new HashSet<>();
	private final HashMap<Player, Integer> timer = new HashMap<>();
	private final HashMap<Player, Integer> currentAction = new HashMap<>();
	private final HashSet<UUID> actionBarDisabled = new HashSet<>();

	public void startUpdateActionBar() {
		mana = AureliumSkills.manaManager;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (OptionL.getBoolean(Option.ACTION_BAR_ENABLED) && OptionL.getBoolean(Option.ACTION_BAR_IDLE)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					Locale locale = Lang.getLanguage(player);
					//Check disabled worlds
					if (!actionBarDisabled.contains(player.getUniqueId())) {
						if (!AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							if (!currentAction.containsKey(player)) {
								currentAction.put(player, 0);
							}
							if (!isGainingXp.contains(player)) {
								AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
								if (attribute != null) {
									sendActionBar(player, Lang.getMessage(ActionBarMessage.IDLE, locale)
											.replace("{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
											.replace("{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
											.replace("{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
											.replace("{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
								}
							}
						}
					}
				}
			}
		}, 0L, OptionL.getInt(Option.ACTION_BAR_UPDATE_PERIOD));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (OptionL.getBoolean(Option.ACTION_BAR_ENABLED)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (timer.containsKey(player)) {
						if (timer.get(player) != 0) {
							timer.put(player, timer.get(player) - 1);
						}
					} else {
						timer.put(player, 0);
					}
				}
			}
		}, 0L, 2L);
	}

	public void sendXpActionBar(Player player, Skill skill, double xpAmount) {
		if (OptionL.getBoolean(Option.ACTION_BAR_ENABLED)) { // If action bar enabled
			if (!actionBarDisabled.contains(player.getUniqueId())) { // If the player's action bar is enabled
				// Get player skill data
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				Locale locale = Lang.getLanguage(player);
				if (playerSkill != null) {
					// Check enabled/disabled for max
					boolean notMaxed = Leveler.levelReqs.size() > playerSkill.getSkillLevel(skill) - 1 && playerSkill.getSkillLevel(skill) < OptionL.getMaxLevel(skill);
					if (notMaxed && !OptionL.getBoolean(Option.ACTION_BAR_XP)) {
						return;
					}
					if (!notMaxed && !OptionL.getBoolean(Option.ACTION_BAR_MAXED)) {
						return;
					}
					// Set timer and is gaining xp
					isGainingXp.add(player);
					timer.put(player, 20);
					// Put current action if not present
					if (!currentAction.containsKey(player)) {
						currentAction.put(player, 0);
					}
					//Add to current action
					currentAction.put(player, currentAction.get(player) + 1);
					int thisAction = this.currentAction.get(player);
					new BukkitRunnable() {
						@Override
						public void run() {
							// If is gaining xp
							if (isGainingXp.contains(player)) {
								// If latest action
								Integer actionBarCurrentAction = currentAction.get(player);
								if (actionBarCurrentAction != null) {
									if (thisAction == actionBarCurrentAction) {
										// Get health attribute
										AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
										if (attribute != null) {
											// Not maxed
											if (notMaxed) {
												if (OptionL.getBoolean(Option.ACTION_BAR_XP)) {
													// Xp gained
													if (xpAmount >= 0) {
														if (!OptionL.getBoolean(Option.ACTION_BAR_ROUND_XP)) {
															sendActionBar(player, LoreUtil.replace(LoreUtil.replace(Lang.getMessage(ActionBarMessage.XP, locale)
																	, "{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{xp_gained}", nf.format(xpAmount)
																	, "{skill}", skill.getDisplayName(locale)
																	, "{current_xp}", nf.format(playerSkill.getXp(skill)))
																	, "{level_xp}", BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1))
																	, "{mana}", String.valueOf(mana.getMana(player.getUniqueId()))
																	, "{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
														}
														else {
															sendActionBar(player, LoreUtil.replace(LoreUtil.replace(Lang.getMessage(ActionBarMessage.XP, locale)
																	, "{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{xp_gained}", nf.format(xpAmount)
																	, "{skill}", skill.getDisplayName(locale)
																	, "{current_xp}", String.valueOf((int) playerSkill.getXp(skill)))
																	, "{level_xp}", BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1))
																	, "{mana}", String.valueOf(mana.getMana(player.getUniqueId()))
																	, "{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
														}
													}
													// Xp removed
													else {
														if (!OptionL.getBoolean(Option.ACTION_BAR_ROUND_XP)) {
															sendActionBar(player, LoreUtil.replace(LoreUtil.replace(Lang.getMessage(ActionBarMessage.XP_REMOVED, locale)
																	, "{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{xp_removed}", nf.format(xpAmount)
																	, "{skill}", skill.getDisplayName(locale)
																	, "{current_xp}", nf.format(playerSkill.getXp(skill)))
																	, "{level_xp}", BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1))
																	, "{mana}", String.valueOf(mana.getMana(player.getUniqueId()))
																	, "{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
														}
														else {
															sendActionBar(player, LoreUtil.replace(LoreUtil.replace(Lang.getMessage(ActionBarMessage.XP, locale)
																	, "{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																	, "{xp_gained}", nf.format(xpAmount)
																	, "{skill}", skill.getDisplayName(locale)
																	, "{current_xp}", String.valueOf((int) playerSkill.getXp(skill)))
																	, "{level_xp}", BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1))
																	, "{mana}", String.valueOf(mana.getMana(player.getUniqueId()))
																	, "{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
														}
													}
												}
											}
											// Maxed
											else {
												if (OptionL.getBoolean(Option.ACTION_BAR_MAXED)) {
													// Xp gained
													if (xpAmount >= 0) {
														sendActionBar(player, LoreUtil.replace(LoreUtil.replace(Lang.getMessage(ActionBarMessage.MAXED, locale)
																, "{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																, "{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																, "{xp_gained}", nf.format(xpAmount)
																, "{skill}", skill.getDisplayName(locale)
																, "{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
																,  "{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
													}
													// Xp removed
													else {
														sendActionBar(player, LoreUtil.replace(LoreUtil.replace(Lang.getMessage(ActionBarMessage.MAXED_REMOVED, locale)
																, "{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																, "{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)))
																, "{xp_removed}", nf.format(xpAmount)
																, "{skill}", skill.getDisplayName(locale)
																, "{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
																,  "{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
													}
												}
											}
										}
										else {
											cancel();
										}
									}
									else {
										cancel();
									}
								}
								else {
									cancel();
								}
							}
							else {
								cancel();
							}
						}
					}.runTaskTimer(plugin, 0L, OptionL.getInt(Option.ACTION_BAR_UPDATE_PERIOD));
					// Schedule stop gaining xp
					new BukkitRunnable() {
						@Override
						public void run() {
							Integer timerNum = timer.get(player);
							if (timerNum != null) {
								if (timerNum.equals(0)){
									isGainingXp.remove(player);
								}
							}
						}
					}.runTaskLater(plugin, 41L);
				}
			}
		}
	}

	private void sendActionBar(Player player, String message) {
		if (AureliumSkills.protocolLibEnabled) {
			if (OptionL.getBoolean(Option.ACTION_BAR_PLACEHOLDER_API) && AureliumSkills.placeholderAPIEnabled) {
				ProtocolUtil.sendActionBar(player, PlaceholderAPI.setPlaceholders(player, message));
			}
			else {
				ProtocolUtil.sendActionBar(player, message);
			}
		}
		else {
			if (OptionL.getBoolean(Option.ACTION_BAR_PLACEHOLDER_API) && AureliumSkills.placeholderAPIEnabled) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(PlaceholderAPI.setPlaceholders(player, message)));
			}
			else {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
			}
		}
	}

	public void resetActionBars() {
		isGainingXp.clear();
		timer.clear();
		currentAction.clear();
	}

	public void resetActionBar(Player player) {
		isGainingXp.remove(player);
		timer.remove(player);
		currentAction.remove(player);
	}

	public HashSet<UUID> getActionBarDisabled() {
		return actionBarDisabled;
	}

}
