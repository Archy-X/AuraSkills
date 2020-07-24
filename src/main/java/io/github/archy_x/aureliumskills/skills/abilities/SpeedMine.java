package io.github.archy_x.aureliumskills.skills.abilities;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class SpeedMine implements RightClickAbility {

	@SuppressWarnings("deprecation")
	@Override
	public void start(Player player) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
			//Apply haste
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (Ability.SPEED_MINE.getValue(skill.getAbilityLevel(Ability.SPEED_MINE)) * 20), 9, false, false), true);
			//Play sound
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
			player.sendMessage(AureliumSkills.tag + ChatColor.GOLD + "Speed Mine Activated!");
		}
	}

	@Override
	public void update(Player player) {
		
	}

	@Override
	public void stop(Player player) {
		AureliumSkills.abilityManager.setCooldown(player.getUniqueId(), Ability.SPEED_MINE, 200);
		player.sendMessage(AureliumSkills.tag + ChatColor.GOLD + "Speed Mine has worn off");
	}

}
