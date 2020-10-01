package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.SkillLevelUpEvent;
import com.archyx.aureliumskills.api.XpGainEvent;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
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
import org.bukkit.ChatColor;
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
import java.util.UUID;
import java.util.regex.Pattern;

public class Leveler {

	public static List<Integer> levelReqs = new LinkedList<>();
	public static Plugin plugin;

	private static ManaManager mana;
	private static final NumberFormat nf = new DecimalFormat("#,###.##");
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
					//Plays a sound if turned on
					Leveler.playSound(player);
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
					//Plays a sound if turned on
					Leveler.playSound(player);
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
			//Plays a sound if turned on
			Leveler.playSound(player);
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
					// When player levels up a skill
					playerSkill.setXp(skill, currentXp - levelReqs.get(currentLevel - 1));
					playerSkill.setSkillLevel(skill, SkillLoader.playerSkills.get(id).getSkillLevel(skill) + 1);
					//Levels up ability
					if (skill.getAbilities().length == 5) {
						Ability ability = skill.getAbilities()[(currentLevel + 4) % 5];
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
					player.sendTitle(ChatColor.GREEN + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.LEVEL_UP), ChatColor.GOLD + "" + RomanNumber.toRoman(currentLevel) + " ➜ " + RomanNumber.toRoman(currentLevel + 1), 5, 100, 5);
					player.playSound(player.getLocation(), "entity.player.levelup", SoundCategory.MASTER, 1f, 0.5f);
					player.sendMessage(getLevelUpMessage(player, playerSkill, skill, currentLevel + 1));
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkLevelUp(player, skill), 20L);
				}
			}
		}
	}


	private static String getLevelUpMessage(Player player, PlayerSkill playerSkill, Skill skill, int newLevel) {
		StringBuilder message = new StringBuilder();
		List<String> messageList = OptionL.getList(Option.SKILL_LEVEL_UP_MESSAGE);
		for (String originalLine : messageList) {
			boolean hasSecondaryStat = false;
			boolean hasAbilityUnlock = false;
			boolean hasAbilityLevelUp = false;
			boolean hasMoneyReward = false;
			boolean hasManaAbilityUnlock = false;
			boolean hasManaAbilityLevelUp = false;
			String line = originalLine.replace("$skill_name$", skill.getDisplayName());
			line = line.replace("&", "§");
			line = line.replace("$level_old$", RomanNumber.toRoman(newLevel - 1));
			line = line.replace("$level_new$", RomanNumber.toRoman(newLevel));
			line = line.replace("$primary_stat_color$", skill.getPrimaryStat().getColor());
			line = line.replace("$primary_stat_symbol$", skill.getPrimaryStat().getSymbol());
			line = line.replace("$primary_stat_name$", Lang.getMessage(Message.valueOf(skill.getPrimaryStat().toString().toUpperCase() + "_NAME")));
			if (newLevel %2 == 0) {
				line = line.replace("$secondary_stat_color$", skill.getSecondaryStat().getColor());
				line = line.replace("$secondary_stat_symbol$", skill.getSecondaryStat().getSymbol());
				line = line.replace("$secondary_stat_name$", Lang.getMessage(Message.valueOf(skill.getSecondaryStat().toString().toUpperCase() + "_NAME")));
				hasSecondaryStat = true;
			}
			if (skill.getAbilities().length == 5) {
				Ability ability = skill.getAbilities()[(newLevel + 3) % 5];
				if (AureliumSkills.abilityOptionManager.isEnabled(ability)) {
					if (playerSkill.getAbilityLevel(ability) > 1) {
						String abilityLevelUp = Lang.getMessage(Message.ABILITY_LEVEL_UP);
						abilityLevelUp = abilityLevelUp.replace("&", "§").replace("_", ability.getDisplayName()).replace("$", RomanNumber.toRoman(playerSkill.getAbilityLevel(ability)));
						line = line.replace("$ability_level_up$", abilityLevelUp);
						hasAbilityLevelUp = true;
					} else {
						String abilityUnlockMessage = Lang.getMessage(Message.ABILITY_UNLOCK_MESSAGE);
						abilityUnlockMessage = abilityUnlockMessage.replace("&", "§").replace("_", ability.getDisplayName());
						line = line.replace("$ability_unlock$", abilityUnlockMessage);
						hasAbilityUnlock = true;
					}
				}
			}
			if (newLevel % 7 == 0) {
				MAbility mAbility = skill.getManaAbility();
				if (AureliumSkills.abilityOptionManager.isEnabled(mAbility)) {
					if (playerSkill.getManaAbilityLevel(mAbility) > 1) {
						String levelUp = Lang.getMessage(Message.MANA_ABILITY_LEVEL_UP);
						levelUp = levelUp.replace("&", "§").replace("_", mAbility.getName()).replace("$", RomanNumber.toRoman(playerSkill.getManaAbilityLevel(mAbility)));
						line = line.replace("$mana_ability_level_up$", levelUp);
						hasManaAbilityLevelUp = true;
					} else {
						String unlock = Lang.getMessage(Message.MANA_ABILITY_UNLOCK_MESSAGE);
						unlock = unlock.replace("&", "§").replace("_", mAbility.getName());
						line = line.replace("$mana_ability_unlock$", unlock);
						hasManaAbilityUnlock = true;
					}
				}
			}
			if (AureliumSkills.vaultEnabled) {
				if (OptionL.getBoolean(Option.SKILL_MONEY_REWARDS_ENABLED)) {
					double base = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_BASE);
					double multiplier = OptionL.getDouble(Option.SKILL_MONEY_REWARDS_MULTIPLIER);
					double amount = base + (multiplier * newLevel * newLevel);
					line = line.replace("$money_amount$", nf.format(amount));
					hasMoneyReward = true;
				}
			}
			line += "\n";
			if (AureliumSkills.placeholderAPIEnabled) {
				line = PlaceholderAPI.setPlaceholders(player, line);
			}
			if (line.contains("$secondary_stat_name$")) {
				if (hasSecondaryStat) {
					message.append(line);
				}
			}
			else if (line.contains("$ability_unlock$")) {
				if (hasAbilityUnlock) {
					message.append(line);
				}
			}
			else if (line.contains("$ability_level_up$")) {
				if (hasAbilityLevelUp) {
					message.append(line);
				}
			}
			else if (line.contains("$mana_ability_unlock$")) {
				if (hasManaAbilityUnlock) {
					message.append(line);
				}
			}
			else if (line.contains("$mana_ability_level_up$")) {
				if (hasManaAbilityLevelUp) {
					message.append(line);
				}
			}
			else if (line.contains("$money_amount$")) {
				if (hasMoneyReward) {
					message.append(line);
				}
			}
			else {
				message.append(line);
			}
		}
		return message.toString();
	}


	public static void updateAbilities(Player player, Skill skill) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
			for (int i = 0; i < skill.getAbilities().length; i++) {
				playerSkill.setAbilityLevel(skill.getAbilities()[i], (playerSkill.getSkillLevel(skill) + 3 - i) / 5);
			}
			playerSkill.setManaAbilityLevel(skill.getManaAbility(), playerSkill.getSkillLevel(skill) / 7);
		}
	}

	public static void sendActionBarMessage(Player player, Skill skill, double xpAmount) {
		if (OptionL.getBoolean(Option.ENABLE_ACTION_BAR)) {
			if (!ActionBar.actionBarDisabled.contains(player.getUniqueId())) {
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				NumberFormat nf = new DecimalFormat("##.#");
				ActionBar.isGainingXp.put(player.getUniqueId(), true);
				ActionBar.timer.put(player.getUniqueId(), 20);
				if (!ActionBar.currentAction.containsKey(player.getUniqueId())) {
					ActionBar.currentAction.put(player.getUniqueId(), 0);
				}
				ActionBar.currentAction.put(player.getUniqueId(), ActionBar.currentAction.get(player.getUniqueId()) + 1);
				int currentAction = ActionBar.currentAction.get(player.getUniqueId());
				new BukkitRunnable() {
					@Override
					public void run() {
						if (ActionBar.isGainingXp.get(player.getUniqueId())) {
							if (currentAction == ActionBar.currentAction.get(player.getUniqueId())) {
								if (Leveler.levelReqs.size() > playerSkill.getSkillLevel(skill) - 1 && playerSkill.getSkillLevel(skill) < OptionL.getMaxLevel(skill)) {
									if (xpAmount >= 0) {
										if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR) && OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
														"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
											}
										} else if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")");
											}
										} else if (OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
													"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
										} else {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")");
										}
									} else {
										if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR) && OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
														"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
											}
										} else if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")");
											}
										} else if (OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
													"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
										} else {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")");
										}
									}
								} else {
									if (xpAmount >= 0) {
										if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR) && OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")" +
														"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
											}
										} else if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")");
											}
										} else if (OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")" +
													"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
										} else {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")");
										}
									} else {
										if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR) && OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")" +
														"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
											}
										} else if (OptionL.getBoolean(Option.ENABLE_HEALTH_ON_ACTION_BAR)) {
											AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
											if (attribute != null) {
												handleActionBarSend(player, OptionL.getColor(Option.HEALTH_TEXT_COLOR) + "" + (int) (player.getHealth() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + "/" + (int) (attribute.getValue() * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
														OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")");
											}
										} else if (OptionL.getBoolean(Option.ENABLE_MANA_ON_ACTION_BAR)) {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")" +
													"   " + OptionL.getColor(Option.MANA_TEXT_COLOR) + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA));
										} else {
											handleActionBarSend(player, OptionL.getColor(Option.SKILL_XP_TEXT_COLOR) + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + OptionL.getColor(Option.XP_PROGRESS_TEXT_COLOR) + "(" + Lang.getMessage(Message.MAXED) + ")");
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

	private static void handleActionBarSend(Player player, String message) {
		if (AureliumSkills.protocolLibEnabled) {
			ProtocolUtil.sendActionBar(player, message);
		}
		else {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
		}
	}

	public static void playSound(Player player) {
		//player.playSound(player.getLocation(), "entity.experience_orb.pickup", SoundCategory.BLOCKS, 0.2f, r.nextFloat() + 0.5f);
	}
}
