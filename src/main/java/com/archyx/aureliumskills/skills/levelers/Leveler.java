package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.api.event.SkillLevelUpEvent;
import com.archyx.aureliumskills.api.event.XpGainEvent;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.LevelerMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.RomanNumber;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class Leveler {
	
	private final AureliumSkills plugin;
	private final List<Integer> levelRequirements;
	private final StatLeveler statLeveler;
	private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public Leveler(AureliumSkills plugin) {
		this.plugin = plugin;
		this.levelRequirements = new LinkedList<>();
		this.statLeveler = new StatLeveler(plugin);
	}
	
	public void loadLevelRequirements() {
		levelRequirements.clear();
		int highestMaxLevel = OptionL.getHighestMaxLevel();
		for (int i = 0; i < highestMaxLevel - 1; i++) {
			levelRequirements.add((int) OptionL.getDouble(Option.SKILL_LEVEL_REQUIREMENTS_MULTIPLIER)*i*i + 100);
		}
	}

	public double getMultiplier(Player player) {
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
	
	//Method for adding xp with a defined amount
	public void addXp(Player player, Skill skill, double amount) {
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
		//Checks if player has a skill profile for safety
		if (playerSkill != null) {
			//Checks if xp amount is not zero
			if (amount != 0) {
				//Gets xp amount
				double xpAmount = amount * getMultiplier(player);
				//Calls event
				XpGainEvent event = new XpGainEvent(player, skill, xpAmount);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					//Adds xp
					playerSkill.addXp(skill, event.getAmount());
					//Check if player leveled up
					checkLevelUp(player, skill);
					//Sends action bar message
					plugin.getActionBar().sendXpActionBar(player, skill, event.getAmount());
					// Sends boss bar if enabled
					if (OptionL.getBoolean(Option.BOSS_BAR_ENABLED)) {
						// Check whether should update
						plugin.getBossBar().incrementAction(player, skill);
						int currentAction = plugin.getBossBar().getCurrentAction(player, skill);
						if (currentAction != -1 && currentAction % OptionL.getInt(Option.BOSS_BAR_UPDATE_EVERY) == 0) {
							boolean notMaxed = levelRequirements.size() > playerSkill.getSkillLevel(skill) - 1 && playerSkill.getSkillLevel(skill) < OptionL.getMaxLevel(skill);
							if (notMaxed) {
								plugin.getBossBar().sendBossBar(player, skill, playerSkill.getXp(skill), levelRequirements.get(playerSkill.getSkillLevel(skill) - 1), playerSkill.getSkillLevel(skill), false);
							} else {
								plugin.getBossBar().sendBossBar(player, skill, 1, 1, playerSkill.getSkillLevel(skill), true);
							}
						}
					}
				}
			}
		}
	}

	//Method for setting xp with a defined amount
	public void setXp(Player player, Skill skill, double amount) {
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
		//Checks if player has a skill profile for safety
		if (playerSkill != null) {
			double originalAmount = SkillLoader.playerSkills.get(player.getUniqueId()).getXp(skill);
			//Sets Xp
			playerSkill.setXp(skill, amount);
			//Check if player leveled up
			checkLevelUp(player, skill);
			//Sends action bar message
			plugin.getActionBar().sendXpActionBar(player, skill, amount - originalAmount);
			// Sends boss bar if enabled
			if (OptionL.getBoolean(Option.BOSS_BAR_ENABLED)) {
				// Check whether should update
				plugin.getBossBar().incrementAction(player, skill);
				int currentAction = plugin.getBossBar().getCurrentAction(player, skill);
				if (currentAction != -1 && currentAction % OptionL.getInt(Option.BOSS_BAR_UPDATE_EVERY) == 0) {
					boolean notMaxed = levelRequirements.size() > playerSkill.getSkillLevel(skill) - 1 && playerSkill.getSkillLevel(skill) < OptionL.getMaxLevel(skill);
					if (notMaxed) {
						plugin.getBossBar().sendBossBar(player, skill, playerSkill.getXp(skill), levelRequirements.get(playerSkill.getSkillLevel(skill) - 1), playerSkill.getSkillLevel(skill), false);
					} else {
						plugin.getBossBar().sendBossBar(player, skill, 1, 1, playerSkill.getSkillLevel(skill), true);
					}
				}
			}
		}
	}
	
	public void updateStats(Player player) {
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
			// Reloads modifiers
			for (String key : playerStat.getModifiers().keySet()) {
				StatModifier modifier = playerStat.getModifiers().get(key);
				playerStat.addStatLevel(modifier.getStat(), modifier.getValue());
			}
			statLeveler.reloadStat(player, Stat.HEALTH);
			statLeveler.reloadStat(player, Stat.WISDOM);
		}
	}
	
	public void checkLevelUp(Player player, Skill skill) {
		UUID id = player.getUniqueId();
		int currentLevel = SkillLoader.playerSkills.get(id).getSkillLevel(skill);
		double currentXp = SkillLoader.playerSkills.get(id).getXp(skill);
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(id);
		PlayerStat playerStat = SkillLoader.playerStats.get(id);
		if (currentLevel < OptionL.getMaxLevel(skill)) { //Check max level options
			if (levelRequirements.size() > currentLevel - 1) {
				if (currentXp >= levelRequirements.get(currentLevel - 1)) {
					Locale locale = Lang.getLanguage(player);
					// When player levels up a skill
					playerSkill.setXp(skill, currentXp - levelRequirements.get(currentLevel - 1));
					playerSkill.setSkillLevel(skill, SkillLoader.playerSkills.get(id).getSkillLevel(skill) + 1);
					playerStat.addStatLevel(skill.getPrimaryStat(), 1);
					statLeveler.reloadStat(player, skill.getPrimaryStat());
					if ((currentLevel + 1) % 2 == 0) {
						playerStat.addStatLevel(skill.getSecondaryStat(), 1);
						statLeveler.reloadStat(player, skill.getSecondaryStat());
					}
					//Adds money rewards if enabled
					if (plugin.isVaultEnabled()) {
						if (OptionL.getBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
							Economy economy = plugin.getEconomy();
							double base = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_BASE);
							double multiplier = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
							economy.depositPlayer(player, base + (multiplier * (currentLevel + 1) * (currentLevel + 1)));
						}
					}
					// Reload items and armor to check for newly met requirements
					plugin.getModifierManager().reloadPlayer(player);
					// Calls event
					SkillLevelUpEvent event = new SkillLevelUpEvent(player, skill, currentLevel + 1);
					Bukkit.getPluginManager().callEvent(event);
					// Sends messages
					if (OptionL.getBoolean(Option.LEVELER_TITLE_ENABLED)) {
						player.sendTitle(LoreUtil.replace(Lang.getMessage(LevelerMessage.TITLE, locale),"{skill}", skill.getDisplayName(locale)),
								LoreUtil.replace(Lang.getMessage(LevelerMessage.SUBTITLE, locale)
										,"{old}", RomanNumber.toRoman(currentLevel)
										,"{new}", RomanNumber.toRoman(currentLevel + 1))
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
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkLevelUp(player, skill), OptionL.getInt(Option.LEVELER_DOUBLE_CHECK_DELAY));
				}
			}
		}
	}


	private String getLevelUpMessage(Player player, PlayerSkill playerSkill, Skill skill, int newLevel, Locale locale) {
		String message = LoreUtil.replace(Lang.getMessage(LevelerMessage.LEVEL_UP, locale)
				,"{skill}", skill.getDisplayName(locale)
				,"{old}", RomanNumber.toRoman(newLevel - 1)
				,"{new}", RomanNumber.toRoman(newLevel));
		if (plugin.isPlaceholderAPIEnabled()) {
			message = PlaceholderAPI.setPlaceholders(player, message);
		}
		// Stat levels
		StringBuilder statMessage = new StringBuilder();
		statMessage.append(LoreUtil.replace(Lang.getMessage(LevelerMessage.STAT_LEVEL, locale)
				,"{color}", skill.getPrimaryStat().getColor(locale)
				,"{symbol}", skill.getPrimaryStat().getSymbol(locale)
				,"{stat}", skill.getPrimaryStat().getDisplayName(locale)));
		if (newLevel % 2 == 0) {
			statMessage.append(LoreUtil.replace(Lang.getMessage(LevelerMessage.STAT_LEVEL, locale)
					,"{color}", skill.getSecondaryStat().getColor(locale)
					,"{symbol}", skill.getSecondaryStat().getSymbol(locale)
					,"{stat}", skill.getSecondaryStat().getDisplayName(locale)));
		}
		message = LoreUtil.replace(message, "{stat_level}", statMessage.toString());
		// Ability unlocks and level ups
		StringBuilder abilityUnlockMessage = new StringBuilder();
		StringBuilder abilityLevelUpMessage = new StringBuilder();
		for (Ability ability : plugin.getAbilityManager().getAbilities(skill, newLevel)) {
			if (plugin.getAbilityManager().isEnabled(ability)) {
				if (plugin.getAbilityManager().getUnlock(ability) == newLevel) {
					abilityUnlockMessage.append(LoreUtil.replace(Lang.getMessage(LevelerMessage.ABILITY_UNLOCK, locale),"{ability}", ability.getDisplayName(locale)));
				} else {
					abilityLevelUpMessage.append(LoreUtil.replace(Lang.getMessage(LevelerMessage.ABILITY_LEVEL_UP, locale)
							,"{ability}", ability.getDisplayName(locale)
							,"{level}", RomanNumber.toRoman(playerSkill.getAbilityLevel(ability))));
				}
			}
		}
		message = LoreUtil.replace(message, "{ability_unlock}", abilityUnlockMessage.toString(), "{ability_level_up}", abilityLevelUpMessage.toString());
		// Mana ability unlocks and level ups
		StringBuilder manaAbilityUnlockMessage = new StringBuilder();
		StringBuilder manaAbilityLevelUpMessage = new StringBuilder();
		MAbility mAbility = plugin.getManaAbilityManager().getManaAbility(skill, newLevel);
		if (mAbility != null) {
			if (plugin.getAbilityManager().isEnabled(mAbility)) {
				if (plugin.getManaAbilityManager().getUnlock(mAbility) == newLevel) {
					manaAbilityUnlockMessage.append(LoreUtil.replace(Lang.getMessage(LevelerMessage.MANA_ABILITY_UNLOCK, locale), "{mana_ability}", mAbility.getDisplayName(locale)));
				} else {
					manaAbilityLevelUpMessage.append(LoreUtil.replace(Lang.getMessage(LevelerMessage.MANA_ABILITY_LEVEL_UP, locale)
							, "{mana_ability}", mAbility.getDisplayName(locale)
							, "{level}", RomanNumber.toRoman(playerSkill.getManaAbilityLevel(mAbility))));
				}
			}
		}
		message = LoreUtil.replace(message, "{mana_ability_unlock}", manaAbilityUnlockMessage.toString(), "{mana_ability_level_up}", manaAbilityLevelUpMessage.toString());
		// If money rewards are enabled
		StringBuilder moneyRewardMessage = new StringBuilder();
		if (plugin.isVaultEnabled()) {
			if (OptionL.getBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
				double base = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_BASE);
				double multiplier = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
				NumberFormat nf = new DecimalFormat("#.##");
				moneyRewardMessage.append(LoreUtil.replace(message, "{money_reward}",
						LoreUtil.replace(Lang.getMessage(LevelerMessage.MONEY_REWARD, locale), "{amount}", nf.format(base + (multiplier * newLevel * newLevel)))));
			}
		}
		message = LoreUtil.replace(message, "{money_reward}", moneyRewardMessage.toString());
		return message.replaceAll("(\\u005C\\u006E)|(\\n)", "\n");
	}

	public List<Integer> getLevelRequirements() {
		return levelRequirements;
	}

	public int getXpRequired(int level) {
		if (levelRequirements.size() > level - 2) {
			return levelRequirements.get(level - 2);
		} else {
			return 0;
		}
	}

}