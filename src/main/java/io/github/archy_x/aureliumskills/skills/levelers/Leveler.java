package io.github.archy_x.aureliumskills.skills.levelers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.stats.ActionBar;
import io.github.archy_x.aureliumskills.stats.PlayerStat;
import io.github.archy_x.aureliumskills.stats.Stat;
import io.github.archy_x.aureliumskills.stats.StatLeveler;
import io.github.archy_x.aureliumskills.util.BigNumber;
import io.github.archy_x.aureliumskills.util.RomanNumber;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Leveler {

	public static List<Integer> levelReqs = new LinkedList<Integer>();
	public static List<Integer> skillPointRewards = new LinkedList<Integer>();
	public static Plugin plugin;
	
	public static void loadLevelReqs() {
		for (int i = 0; i < 72; i++) {
			levelReqs.add(10*i*10*i + 100);
			skillPointRewards.add((int) Math.pow(1.032, i));
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
			StatLeveler.reloadStat(player, Stat.HEALTH);
			StatLeveler.reloadStat(player, Stat.TOUGHNESS);
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
				playerSkill.setXp(skill, currentXp - levelReqs.get(currentLevel - 1));
				playerSkill.setSkillLevel(skill, SkillLoader.playerSkills.get(id).getSkillLevel(skill) + 1);
				playerSkill.addSkillPoints(skill, skillPointRewards.get(currentLevel - 1));
				if (skillPointRewards.get(currentLevel - 1) == 1) {
					player.sendMessage(AureliumSkills.tag + ChatColor.AQUA + "+" + skillPointRewards.get(currentLevel - 1) + " " + skill.getDisplayName() + " Skill Point");
				}
				else {
					player.sendMessage(AureliumSkills.tag + ChatColor.AQUA + "+" + skillPointRewards.get(currentLevel - 1) + " " + skill.getDisplayName() + " Skill Points");
				}
				playerStat.addStatLevel(skill.getPrimaryStat(), 1);
				StatLeveler.reloadStat(player, skill.getPrimaryStat());
				player.sendMessage(AureliumSkills.tag + skill.getPrimaryStat().getColor() + "+1 " + skill.getPrimaryStat().getSymbol() + " " + skill.getPrimaryStat().getDisplayName());
				if ((currentLevel + 1) % 2 == 0) {
					playerStat.addStatLevel(skill.getSecondaryStat(), 1);
					StatLeveler.reloadStat(player, skill.getSecondaryStat());
					player.sendMessage(AureliumSkills.tag + skill.getSecondaryStat().getColor() + "+1 " + skill.getSecondaryStat().getSymbol() + " " + skill.getSecondaryStat().getDisplayName());
				}
				player.sendTitle(ChatColor.GREEN + StringUtils.capitalize(skill.getName()) + " Level Up", ChatColor.GOLD + "" + RomanNumber.toRoman(currentLevel) + " ➜ " + RomanNumber.toRoman(currentLevel + 1), 5, 100, 5);
				player.playSound(player.getLocation(), "entity.player.levelup", SoundCategory.MASTER, 1f, 0.5f);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						checkLevelUp(player, skill);
					}
				}, 20L);
			}
		}
	}
	
	public static void sendActionBarMessage(Player player, Skill skill, double xpAmount) {
		PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
		NumberFormat nf = new DecimalFormat("##.#");
		ActionBar.isGainingXp.put(player.getUniqueId(), true);
		ActionBar.timer.put(player.getUniqueId(), 20);
		if (ActionBar.currentAction.containsKey(player.getUniqueId()) == false) {
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
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "" + (int) (player.getHealth() * 5) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 5) + " HP" + "   " + 
									ChatColor.GOLD + "+" + nf.format(xpAmount) + " " +  skill.getDisplayName() + " XP " + ChatColor.GRAY + "(" + nf.format(playerSkill.getXp(skill)) + "/" + BigNumber.withSuffix(Leveler.levelReqs.get(playerSkill.getSkillLevel(skill) - 1)) + " XP)" +
									"   " + ChatColor.AQUA + 20 + "/" + (20 + 2 * SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.WISDOM)) + " Mana"));
						}
						else {
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "" + (int) (player.getHealth() * 5) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 5) + " HP" + "   " + 
									ChatColor.GOLD + "+" + nf.format(xpAmount) + " " + StringUtils.capitalize(skill.getName()) + " XP " + ChatColor.GRAY + "(MAXED)" +
									"   " + ChatColor.AQUA + 20 + "/" + (20 + 2 * SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.WISDOM)) + " Mana"));
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
		}.runTaskTimer(plugin, 0L, 2L);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (ActionBar.timer.get(player.getUniqueId()).equals(0)) {
					ActionBar.isGainingXp.put(player.getUniqueId(), false);
				}
			}
		}.runTaskLater(plugin, 41L);
	}
	
	public static void playSound(Player player) {
		//player.playSound(player.getLocation(), "entity.experience_orb.pickup", SoundCategory.BLOCKS, 0.2f, r.nextFloat() + 0.5f);
	}
}
