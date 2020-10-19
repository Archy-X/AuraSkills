package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.SkillLevelUpEvent;
import com.archyx.aureliumskills.api.XpGainEvent;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.ActionBarMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.LevelerMessage;
import com.archyx.aureliumskills.magic.ManaManager;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.stats.ActionBar;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import com.archyx.aureliumskills.util.BigNumber;
import com.archyx.aureliumskills.util.ProtocolUtil;
import com.archyx.aureliumskills.util.RomanNumber;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class Leveler {

	public static List<Integer> levelReqs = new LinkedList<>();
	public static Plugin plugin;

	private static ManaManager mana;
	private static final NumberFormat nf = new DecimalFormat("##.#");
	private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static void loadLevelReqs() {
		mana = AureliumSkills.manaManager;
		levelReqs.clear();
		for (int i = 0; i < 96; i++) {
			levelReqs.add((int) OptionL.getDouble(Option.SKILL_LEVEL_REQUIREMENTS_MULTIPLIER)*i*i + 100);
		}
	}

	public static double getMultiplier(Player player) {
		return 1 + player.getEffectivePermissions().stream()
				.map(PermissionAttachmentInfo::getPermission)
				.map(String::toLowerCase)
				.filter(value -> value.startsWith("aureliumskills.multiplier."))
				.map(value -> value.replace("aureliumskills.multiplier.", ""))
				.filter(value -> pattern.matcher(value).matches())
				.mapToDouble(Double::parseDouble)
				.map(it -> it/100)
				.sum();
	}

	//Method for adding xp
	public static void addXp(Player player, Skill skill, Source source) {
		//Checks if player has a skill profile for safety
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			//Checks if xp amount is not zero
			if (OptionL.getXp(source) != 0) {
				//Gets amount
				double amount = OptionL.getXp(source) * getMultiplier(player);
				//Calls event
				XpGainEvent event = new XpGainEvent(player, skill, amount);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					//Adds Xp
					SkillLoader.playerSkills.get(player.getUniqueId()).addXp(skill, event.getAmount());
					//Check if player leveled up
					Leveler.checkLevelUp(player, skill);
					//Sends action bar message
					Leveler.sendActionBarMessage(player, skill, event.getAmount());
				}
			}
		}
	}
	
	//Method for adding xp with a defined amount
	public static void addXp(Player player, Skill skill, double amount) {
		//Checks if player has a skill profile for safety
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			//Checks if xp amount is not zero
			if (amount != 0) {
				//Gets xp amount
				double xpAmount = amount * getMultiplier(player);
				//Calls event
				XpGainEvent event = new XpGainEvent(player, skill, xpAmount);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					//Adds xp
					SkillLoader.playerSkills.get(player.getUniqueId()).addXp(skill, event.getAmount());
					//Check if player leveled up
					Leveler.checkLevelUp(player, skill);
					//Sends action bar message
					Leveler.sendActionBarMessage(player, skill, event.getAmount());
				}
			}
		}
	}

	//Method for adding xp with a defined amount
	public static void setXp(Player player, Skill skill, double amount) {
		//Checks if player has a skill profile for safety
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			double originalAmount = SkillLoader.playerSkills.get(player.getUniqueId()).getXp(skill);
			//Sets Xp
			SkillLoader.playerSkills.get(player.getUniqueId()).setXp(skill, amount);
			//Check if player leveled up
			Leveler.checkLevelUp(player, skill);
			//Sends action bar message
			Leveler.sendActionBarMessage(player, skill, amount - originalAmount);
		}
	}
	
	public static void updateStats(Player player) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId()) && SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
			PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
			for (Stat stat : Stat.values()) {
				playerStat.setStatLevel(stat, 0);
			}
			for (Skill skill : Skill.values()) {
				playerStat.addStatLevel(skill.getPrimaryStat(), playerSkill.getSkillLevel(skill) - 1);
				playerStat.addStatLevel(skill.getSecondaryStat(), playerSkill.getSkillLevel(skill) / 2);
			}
			//Reloads modifiers
			for (String key : playerStat.getModifiers().keySet()) {
				StatModifier modifier = playerStat.getModifiers().get(key);
				playerStat.addStatLevel(modifier.getStat(), modifier.getValue());
			}
			StatLeveler.reloadStat(player, Stat.HEALTH);
			StatLeveler.reloadStat(player, Stat.WISDOM);
		}
	}
	
	public static void checkLevelUp(Player player, Skill skill) {
		UUID id = player.getUniqueId();
		int currentLevel = SkillLoader.playerSkills.get(id).getSkillLevel(skill);
		double currentXp = SkillLoader.playerSkills.get(id).getXp(skill);
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(id);
		PlayerStat playerStat = SkillLoader.playerStats.get(id);
		if (currentLevel < OptionL.getMaxLevel(skill)) { //Check max level options
			if (levelReqs.size() > currentLevel - 1) {
				if (currentXp >= levelReqs.get(currentLevel - 1)) {
					Locale locale = Lang.getLanguage(player);
					// When player levels up a skill
					playerSkill.setXp(skill, currentXp - levelReqs.get(currentLevel - 1));
					playerSkill.setSkillLevel(skill, SkillLoader.playerSkills.get(id).getSkillLevel(skill) + 1);
					//Levels up ability
					if (skill.getAbilities().size() == 5) {
						Ability ability = skill.getAbilities().get((currentLevel + 4) % 5).get();
						playerSkill.levelUpAbility(ability);
					}
					if ((currentLevel + 1) % 7 == 0) {
						MAbility mAbility = skill.getManaAbility();
						playerSkill.levelUpManaAbility(mAbility);
					}
					playerStat.addStatLevel(skill.getPrimaryStat(), 1);
					StatLeveler.reloadStat(player, skill.getPrimaryStat());
					if ((currentLevel + 1) % 2 == 0) {
						playerStat.addStatLevel(skill.getSecondaryStat(), 1);
						StatLeveler.reloadStat(player, skill.getSecondaryStat());
					}
					//Adds money rewards if enabled
					if (AureliumSkills.vaultEnabled) {
						if (OptionL.getBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
							Economy economy = AureliumSkills.getEconomy();
							double base = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_BASE);
							double multiplier = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
							economy.depositPlayer(player, base + (multiplier * (currentLevel + 1) * (currentLevel + 1)));
						}
					}
					//Calls event
					SkillLevelUpEvent event = new SkillLevelUpEvent(player, skill, currentLevel + 1);
					Bukkit.getPluginManager().callEvent(event);
					//Sends messages
					if (OptionL.getBoolean(Option.LEVELER_TITLE_ENABLED)) {
						player.sendTitle(Lang.getMessage(LevelerMessage.TITLE, locale).replace("{skill}", skill.getDisplayName(locale)),
								Lang.getMessage(LevelerMessage.SUBTITLE, locale)
										.replace("{old}", RomanNumber.toRoman(currentLevel))
										.replace("{new}", RomanNumber.toRoman(currentLevel + 1))
								, OptionL.getInt(Option.LEVELER_TITLE_FADE_IN), OptionL.getInt(Option.LEVELER_TITLE_STAY), OptionL.getInt(Option.LEVELER_TITLE_FADE_OUT));
					}
					if (OptionL.getBoolean(Option.LEVELER_SOUND_ENABLED)) {
						try {
							player.playSound(player.getLocation(), Sound.valueOf(OptionL.getString(Option.LEVELER_SOUND_TYPE))
									, SoundCategory.valueOf(OptionL.getString(Option.LEVELER_SOUND_CATEGORY))
									, (float) OptionL.getDouble(Option.LEVELER_SOUND_VOLUME), (float) OptionL.getDouble(Option.LEVELER_SOUND_PITCH));
						}
						catch (Exception e) {
							Bukkit.getLogger().warning("[AureliumSkills] Error playing level up sound (Check config) Played the default sound instead");
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 0.5f);
						}
					}
					player.sendMessage(getLevelUpMessage(player, playerSkill, skill, currentLevel + 1, locale));
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkLevelUp(player, skill), 20L);
				}
			}
		}
	}


	private static String getLevelUpMessage(Player player, PlayerSkill playerSkill, Skill skill, int newLevel, Locale locale) {
		//Build original message with placeholders that are always there
		String originalMessage = PlaceholderAPI.setPlaceholders(player,Lang.getMessage(LevelerMessage.LEVEL_UP, locale)
				.replace("{skill}", skill.getDisplayName(locale))
				.replace("{old}", RomanNumber.toRoman(newLevel - 1))
				.replace("{new}", RomanNumber.toRoman(newLevel))
				.replace("{stat_level_1}", Lang.getMessage(LevelerMessage.STAT_LEVEL, locale)
						.replace("{color}", skill.getPrimaryStat().getColor(locale))
						.replace("{symbol}", skill.getPrimaryStat().getSymbol(locale))
						.replace("{stat}", skill.getPrimaryStat().getDisplayName(locale))));
		StringBuilder message = new StringBuilder();
		//For every line
		for (String line : originalMessage.split("(\\u005C\\u006E)|(\\n)")) {
			if (line.contains("{stat_level_2}")) {
				//If level has secondary stat
				if (newLevel % 2 == 0) {
					message.append("\n").append(line.replace("{stat_level_2}", Lang.getMessage(LevelerMessage.STAT_LEVEL, locale)
							.replace("{color}", skill.getSecondaryStat().getColor(locale))
							.replace("{symbol}", skill.getSecondaryStat().getSymbol(locale))
							.replace("{stat}", skill.getSecondaryStat().getDisplayName(locale))));
				}
			}
			else if (line.contains("{ability_unlock}")) {
				//If skill has 5 abilities
				if (skill.getAbilities().size() == 5) {
					Ability ability = skill.getAbilities().get((newLevel + 3) % 5).get();
					//Check ability is enabled
					if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
						//If ability is unlocked
						if (!(playerSkill.getAbilityLevel(ability) > 1)) {
							message.append("\n").append(line.replace("{ability_unlock}", Lang.getMessage(LevelerMessage.ABILITY_UNLOCK, locale)
									.replace("{ability}", ability.getDisplayName(locale))));
						}
					}
				}
			}
			else if (line.contains("{ability_level_up}")) {
				//If skill has 5 abilities
				if (skill.getAbilities().size() == 5) {
					Ability ability = skill.getAbilities().get((newLevel + 3) % 5).get();
					//Check ability is enabled
					if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
						//If ability is leveled up
						if (playerSkill.getAbilityLevel(ability) > 1) {
							message.append("\n").append(line.replace("{ability_level_up}", Lang.getMessage(LevelerMessage.ABILITY_LEVEL_UP, locale)
									.replace("{ability}", ability.getDisplayName(locale))
									.replace("{level}", RomanNumber.toRoman(playerSkill.getAbilityLevel(ability)))));
						}
					}
				}
			}
			else if (line.contains("{money_reward}")) {
				//If money rewards are enabled
				if (AureliumSkills.vaultEnabled) {
					if (OptionL.getBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
						double base = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_BASE);
						double multiplier = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
						message.append("\n").append(line.replace("{amount}", String.valueOf(base + (multiplier * newLevel * newLevel))));
					}
				}
			}
			else {
				message.append("\n").append(line);
			}
		}
		message.delete(0, 1); //Delete the first new line
		return message.toString();
	}


	public static void updateAbilities(Player player, Skill skill) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
			for (int i = 0; i < skill.getAbilities().size(); i++) {
				playerSkill.setAbilityLevel(skill.getAbilities().get(i).get(), (playerSkill.getSkillLevel(skill) + 3 - i) / 5);
			}
			playerSkill.setManaAbilityLevel(skill.getManaAbility(), playerSkill.getSkillLevel(skill) / 7);
		}
	}

	public static void sendActionBarMessage(Player player, Skill skill, double xpAmount) {
		if (OptionL.getBoolean(Option.ENABLE_ACTION_BAR)) { //If action bar enabled
			if (!ActionBar.actionBarDisabled.contains(player.getUniqueId())) { //If the player's action bar is enabled
				//Get player skill data
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				Locale locale = Lang.getLanguage(player);
				if (playerSkill != null) {
					//Set timer and is gaining xp
					ActionBar.isGainingXp.put(player.getUniqueId(), true);
					ActionBar.timer.put(player.getUniqueId(), 20);
					//Put current action if not present
					if (!ActionBar.currentAction.containsKey(player.getUniqueId())) {
						ActionBar.currentAction.put(player.getUniqueId(), 0);
					}
					//Add to current action
					ActionBar.currentAction.put(player.getUniqueId(), ActionBar.currentAction.get(player.getUniqueId()) + 1);
					int currentAction = ActionBar.currentAction.get(player.getUniqueId());
					new BukkitRunnable() {
						@Override
						public void run() {
							//If is gaining xp
							if (ActionBar.isGainingXp.get(player.getUniqueId())) {
								//If latest action
								if (currentAction == ActionBar.currentAction.get(player.getUniqueId())) {
									//Get health attribute
									AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
									if (attribute != null) {
										//Not maxed
										if (Leveler.levelReqs.size() > playerSkill.getSkillLevel(skill) - 1 && playerSkill.getSkillLevel(skill) < OptionL.getMaxLevel(skill)) {
											//Xp gained
											if (xpAmount >= 0) {
												handleActionBarSend(player, Lang.getMessage(ActionBarMessage.XP, locale)
														.replace("{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{xp_gained}", nf.format(xpAmount))
														.replace("{skill}", skill.getDisplayName(locale))
														.replace("{current_xp}", nf.format(playerSkill.getXp(skill)))
														.replace("{level_xp}", BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)))
														.replace("{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
														.replace("{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
											}
											//Xp removed
											else {
												handleActionBarSend(player, Lang.getMessage(ActionBarMessage.XP_REMOVED, locale)
														.replace("{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{xp_removed}", nf.format(xpAmount))
														.replace("{skill}", skill.getDisplayName(locale))
														.replace("{current_xp}", nf.format(playerSkill.getXp(skill)))
														.replace("{level_xp}", BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)))
														.replace("{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
														.replace("{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
											}
										}
										//Maxed
										else {
											//Xp gained
											if (xpAmount >= 0) {
												handleActionBarSend(player, Lang.getMessage(ActionBarMessage.MAXED, locale)
														.replace("{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{xp_gained}", nf.format(xpAmount))
														.replace("{skill}", skill.getDisplayName(locale))
														.replace("{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
														.replace("{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
											}
											//Xp removed
											else {
												handleActionBarSend(player, Lang.getMessage(ActionBarMessage.MAXED_REMOVED, locale)
														.replace("{hp}", String.valueOf((int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{max_hp}", String.valueOf((int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING))))
														.replace("{xp_removed}", nf.format(xpAmount))
														.replace("{skill}", skill.getDisplayName(locale))
														.replace("{mana}", String.valueOf(mana.getMana(player.getUniqueId())))
														.replace("{max_mana}", String.valueOf(mana.getMaxMana(player.getUniqueId()))));
											}
										}
									}
								} else {
									cancel();
								}
							} else {
								cancel();
							}
						}
					}.runTaskTimer(plugin, 0L, OptionL.getInt(Option.ACTION_BAR_UPDATE_PERIOD));
					//Schedule stop gaining xp
					new BukkitRunnable() {
						@Override
						public void run() {
							if (ActionBar.timer.get(player.getUniqueId()).equals(0)) {
								ActionBar.isGainingXp.put(player.getUniqueId(), false);
							}
						}
					}.runTaskLater(plugin, 41L);
				}
			}
		}
	}

	private static void handleActionBarSend(Player player, String message) {
		if (AureliumSkills.protocolLibEnabled) {
			ProtocolUtil.sendActionBar(player, message);
		}
		else {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
		}
	}

}