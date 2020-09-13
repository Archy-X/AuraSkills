package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.magic.ManaManager;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.stats.*;
import com.archyx.aureliumskills.util.BigNumber;
import com.archyx.aureliumskills.util.RomanNumber;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
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
			levelReqs.add((int) Options.skillLevelRequirementsMultiplier*i*i + 100);
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
			if (Options.getXpAmount(source) != 0) {
				//Adds Xp
				double amount = Options.getXpAmount(source) * getMultiplier(player);
				SkillLoader.playerSkills.get(player.getUniqueId()).addXp(skill, amount);
				//Plays a sound if turned on
				Leveler.playSound(player);
				//Check if player leveled up
				Leveler.checkLevelUp(player, skill);
				//Sends action bar message
				Leveler.sendActionBarMessage(player, skill, amount);
			}
		}
	}
	
	//Method for adding xp with a defined amount
	public static void addXp(Player player, Skill skill, double amount) {
		//Checks if player has a skill profile for safety
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			//Checks if xp amount is not zero
			if (amount != 0) {
				//Adds Xp
				double xpAmount = amount * getMultiplier(player);
				SkillLoader.playerSkills.get(player.getUniqueId()).addXp(skill, xpAmount);
				//Plays a sound if turned on
				Leveler.playSound(player);
				//Check if player leveled up
				Leveler.checkLevelUp(player, skill);
				//Sends action bar message
				Leveler.sendActionBarMessage(player, skill, xpAmount);
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
				playerStat.addStatLevel(skill.getPrimaryStat(), 1);
				StatLeveler.reloadStat(player, skill.getPrimaryStat());
				if ((currentLevel + 1) % 2 == 0) {
					playerStat.addStatLevel(skill.getSecondaryStat(), 1);
					StatLeveler.reloadStat(player, skill.getSecondaryStat());
				}
				//Adds money rewards if enabled
				if (AureliumSkills.vaultEnabled) {
					if (Options.skillMoneyRewardsEnabled) {
						Economy economy = AureliumSkills.getEconomy();
						double base = Options.skillMoneyRewards[0];
						double multiplier = Options.skillMoneyRewards[1];
						economy.depositPlayer(player, base + (multiplier * (currentLevel + 1) * (currentLevel + 1)));
					}
				}
				player.sendTitle(ChatColor.GREEN + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.LEVEL_UP), ChatColor.GOLD + "" + RomanNumber.toRoman(currentLevel) + " ➜ " + RomanNumber.toRoman(currentLevel + 1), 5, 100, 5);
				player.playSound(player.getLocation(), "entity.player.levelup", SoundCategory.MASTER, 1f, 0.5f);
				player.sendMessage(getLevelUpMessage(player, playerSkill, skill, currentLevel + 1));
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> checkLevelUp(player, skill), 20L);
			}
		}
	}


	private static String getLevelUpMessage(Player player, PlayerSkill playerSkill, Skill skill, int newLevel) {
		StringBuilder message = new StringBuilder();
		List<String> messageList = Options.skillLevelUpMessage;
		for (String originalLine : messageList) {
			boolean hasSecondaryStat = false;
			boolean hasAbilityUnlock = false;
			boolean hasAbilityLevelUp = false;
			boolean hasMoneyReward = false;
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
				if (playerSkill.getAbilityLevel(ability) > 1) {
					String abilityLevelUp = Lang.getMessage(Message.ABILITY_LEVEL_UP);
					abilityLevelUp = abilityLevelUp.replace("&", "§").replace("_", ability.getDisplayName()).replace("$", RomanNumber.toRoman(playerSkill.getAbilityLevel(ability)));
					line = line.replace("$ability_level_up$", abilityLevelUp);
					hasAbilityLevelUp = true;
				}
				else {
					String abilityUnlockMessage = Lang.getMessage(Message.ABILITY_UNLOCK_MESSAGE);
					abilityUnlockMessage = abilityUnlockMessage.replace("&", "§").replace("_", ability.getDisplayName());
					line = line.replace("$ability_unlock$", abilityUnlockMessage);
					hasAbilityUnlock = true;
				}
			}
			if (AureliumSkills.vaultEnabled) {
				if (Options.skillMoneyRewardsEnabled) {
					double base = Options.skillMoneyRewards[0];
					double multiplier = Options.skillMoneyRewards[1];
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
		}
	}

	public static void sendActionBarMessage(Player player, Skill skill, double xpAmount) {
		if (Options.enable_action_bar) {
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
							if (Leveler.levelReqs.size() > playerSkill.getSkillLevel(skill) - 1) {
								if (xpAmount >= 0) {
									if (Options.enable_health_on_action_bar && Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
									} else if (Options.enable_health_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")"));
									} else if (Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
									} else {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")"));
									}
								}
								else {
									if (Options.enable_health_on_action_bar && Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
									} else if (Options.enable_health_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")"));
									} else if (Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
									} else {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " " + Lang.getMessage(Message.XP) + ")"));
									}
								}
							}
							else {
								if (xpAmount >= 0) {
									if (Options.enable_health_on_action_bar && Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));

									} else if (Options.enable_health_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")"));
									} else if (Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
									} else {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "+" + nf.format(xpAmount) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")"));
									}
								}
								else {
									if (Options.enable_health_on_action_bar && Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));

									} else if (Options.enable_health_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + "   " +
												Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")"));
									} else if (Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")" +
												"   " + Options.mana_text_color + mana.getMana(player.getUniqueId()) + "/" + mana.getMaxMana(player.getUniqueId()) + " " + Lang.getMessage(Message.MANA)));
									} else {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.skill_xp_text_color + "-" + nf.format(Math.abs(xpAmount)) + " " + Lang.getMessage(Message.valueOf(skill.toString().toUpperCase() + "_NAME")) + " " + Lang.getMessage(Message.XP) + " " + Options.xp_progress_text_color + "(" + Lang.getMessage(Message.MAXED) + ")"));
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
			}.runTaskTimer(plugin, 0L, Options.actionBarUpdatePeriod);
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
	
	public static void playSound(Player player) {
		//player.playSound(player.getLocation(), "entity.experience_orb.pickup", SoundCategory.BLOCKS, 0.2f, r.nextFloat() + 0.5f);
	}
}
