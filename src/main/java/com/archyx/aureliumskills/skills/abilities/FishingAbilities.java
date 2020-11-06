package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.levelers.Leveler;
import com.archyx.aureliumskills.util.LoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class FishingAbilities implements Listener {

	private final Random r = new Random();
	
	public static double getModifiedXp(Player player, Source source) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		if (skill != null) {
			double output = OptionL.getXp(source);
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FISHER)) {
				double modifier = 1;
				modifier += Ability.FISHER.getValue(skill.getAbilityLevel(Ability.FISHER)) / 100;
				output *= modifier;
			}
			return output;
		}
		return 0.0;
	}
	
	@EventHandler
	public void luckyCatch(PlayerFishEvent event) {
		if (OptionL.isEnabled(Skill.FISHING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LUCKY_CATCH)) {
				Player player = event.getPlayer();
				//Check disabled worlds
				if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
					return;
				}
				//Checks if in blocked world
				if (AureliumSkills.worldManager.isInBlockedWorld(player.getLocation())) {
					return;
				}
				//Checks if in blocked region
				if (AureliumSkills.worldGuardEnabled) {
					if (AureliumSkills.worldGuardSupport.isInBlockedRegion(player.getLocation())) {
						return;
					}
				}
				//Check permission
				if (!player.hasPermission("aureliumskills.fishing")) {
					return;
				}
				if (event.getCaught() instanceof Item) {
					if (event.getExpToDrop() > 0) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
							if (r.nextDouble() < (Ability.LUCKY_CATCH.getValue(skill.getAbilityLevel(Ability.LUCKY_CATCH)) / 100)) {
								Item item = (Item) event.getCaught();
								ItemStack drop = item.getItemStack();
								if (drop.getMaxStackSize() > 1) {
									drop.setAmount(drop.getAmount() * 2);
									item.setItemStack(drop);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void treasureHunterAndEpicCatch(PlayerFishEvent event) {
		if (OptionL.isEnabled(Skill.FISHING)) {
			Player player = event.getPlayer();
			//Check disabled worlds
			if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
				return;
			}
			//Checks if in blocked world
			if (AureliumSkills.worldManager.isInBlockedWorld(player.getLocation())) {
				return;
			}
			//Checks if in blocked region
			if (AureliumSkills.worldGuardEnabled) {
				if (AureliumSkills.worldGuardSupport.isInBlockedRegion(player.getLocation())) {
					return;
				}
			}
			//Check permission
			if (!player.hasPermission("aureliumskills.fishing")) {
				return;
			}
			if (event.getCaught() instanceof Item) {
				if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
					if (event.getExpToDrop() > 0) {
						if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
							PlayerSkill skill = SkillLoader.playerSkills.get(event.getPlayer().getUniqueId());
							if (r.nextDouble() < (Ability.EPIC_CATCH.getValue(skill.getAbilityLevel(Ability.EPIC_CATCH)) / 100)) {
								if (AureliumSkills.abilityOptionManager.isEnabled(Ability.EPIC_CATCH)) {
									Item item = (Item) event.getCaught();
									int lootTableSize = AureliumSkills.lootTableManager.getLootTable("fishing-epic").getLoot().size();
									if (lootTableSize > 0) {
										Loot loot = AureliumSkills.lootTableManager.getLootTable("fishing-epic").getLoot().get(r.nextInt(lootTableSize));
										// If has item
										if (loot.hasItem()) {
											ItemStack drop = loot.getDrop();
											if (drop != null) {
												item.setItemStack(drop);
												Leveler.addXp(event.getPlayer(), Skill.FISHING, getModifiedXp(event.getPlayer(), Source.FISHING_EPIC));
											}
										}
										// If has command
										else if (loot.hasCommand()) {
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LoreUtil.replace(loot.getCommand(), "{player}", player.getName()));
										}
									}
								}
							} else if (r.nextDouble() < (Ability.TREASURE_HUNTER.getValue(skill.getAbilityLevel(Ability.TREASURE_HUNTER)) / 100)) {
								if (AureliumSkills.abilityOptionManager.isEnabled(Ability.TREASURE_HUNTER)) {
									Item item = (Item) event.getCaught();
									int lootTableSize = AureliumSkills.lootTableManager.getLootTable("fishing-rare").getLoot().size();
									if (lootTableSize > 0) {
										Loot loot = AureliumSkills.lootTableManager.getLootTable("fishing-rare").getLoot().get(r.nextInt(lootTableSize));
										// If has item
										if (loot.hasItem()) {
											ItemStack drop = loot.getDrop();
											if (drop != null) {
												item.setItemStack(drop);
												Leveler.addXp(event.getPlayer(), Skill.FISHING, getModifiedXp(event.getPlayer(), Source.FISHING_RARE));
											}
										}
										// If has commaand
										else if (loot.hasCommand()) {
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LoreUtil.replace(loot.getCommand(), "{player}", player.getName()));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void grappler(PlayerFishEvent event) {
		if (OptionL.isEnabled(Skill.FISHING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.GRAPPLER)) {
				if (event.getCaught() != null) {
					if (!(event.getCaught() instanceof Item)) {
						if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
							PlayerSkill skill = SkillLoader.playerSkills.get(event.getPlayer().getUniqueId());
							Player player = event.getPlayer();
							//Check disabled worlds
							if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
								return;
							}
							//Check permission
							if (!player.hasPermission("aureliumskills.fishing")) {
								return;
							}
							Vector vector = player.getLocation().toVector().subtract(event.getCaught().getLocation().toVector());
							event.getCaught().setVelocity(vector.multiply(0.004 + (Ability.GRAPPLER.getValue(skill.getAbilityLevel(Ability.GRAPPLER)) / 25000)));
						}
					}
				}
			}
		}
	}
}
