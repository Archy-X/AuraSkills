package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.Replenish;
import com.archyx.aureliumskills.util.BlockUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;
import java.util.Random;

public class FarmingAbilities implements Listener {

	private static final Random r = new Random();
	private final Plugin plugin;

	public FarmingAbilities(Plugin plugin) {
		this.plugin = plugin;
	}

	public static double getModifiedXp(Player player, Source source) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		double output = OptionL.getXp(source);
		if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FARMER)) {
			double modifier = 1;
			modifier += Ability.FARMER.getValue(skill.getAbilityLevel(Ability.FARMER)) / 100;
			output *= modifier;
		}
		return output;
	}
	
	public static void bountifulHarvest(Player player, Block block) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.BOUNTIFUL_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
					if (skill.getAbilityLevel(Ability.BOUNTIFUL_HARVEST) > 0) {
						if (r.nextDouble() < (Ability.BOUNTIFUL_HARVEST.getValue(skill.getAbilityLevel(Ability.BOUNTIFUL_HARVEST))) / 100) {
							for (ItemStack item : block.getDrops()) {
								player.getWorld().dropItemNaturally(block.getLocation(), item);
							}
						}
					}
				}
			}
		}
	}
	
	public static void tripleHarvest(Player player, Block block) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.TRIPLE_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
					if (skill.getAbilityLevel(Ability.TRIPLE_HARVEST) > 0) {
						if (r.nextDouble() < (Ability.TRIPLE_HARVEST.getValue(skill.getAbilityLevel(Ability.TRIPLE_HARVEST))) / 100) {
							for (ItemStack item : block.getDrops()) {
								player.getWorld().dropItemNaturally(block.getLocation(), item);
								player.getWorld().dropItemNaturally(block.getLocation(), item);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void geneticist(PlayerItemConsumeEvent event) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.GENETICIST)) {
				Player player = event.getPlayer();
				//Check permission
				if (!player.hasPermission("aureliumskills.farming")) {
					return;
				}
				//Check disabled worlds
				if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
					return;
				}
				Material mat = event.getItem().getType();
				if (mat.equals(Material.BREAD) || mat.equals(Material.APPLE) || mat.equals(Material.GOLDEN_APPLE) || mat.equals(XMaterial.POTATO.parseMaterial())
						|| mat.equals(Material.BAKED_POTATO) || mat.equals(XMaterial.CARROT.parseMaterial()) || mat.equals(Material.GOLDEN_CARROT) || mat.equals(Material.MELON)
						|| mat.equals(Material.PUMPKIN_PIE) || mat.equals(Material.BEETROOT) || mat.equals(Material.BEETROOT_SOUP) || mat.equals(XMaterial.MUSHROOM_STEW.parseMaterial())
						|| mat.equals(Material.POISONOUS_POTATO)) {
					float amount = (float) Ability.GENETICIST.getValue(SkillLoader.playerSkills.get(player.getUniqueId()).getAbilityLevel(Ability.GENETICIST)) / 10;
					player.setSaturation(player.getSaturation() + amount);
				}
			}
		}
	}

	public static void scytheMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SCYTHE_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.farming")) {
					return;
				}
				if (playerSkill.getAbilityLevel(Ability.SCYTHE_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (Ability.SCYTHE_MASTER.getValue(playerSkill.getAbilityLevel(Ability.SCYTHE_MASTER)) / 100)));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void applyReplenish(BlockBreakEvent event) {
		Material blockMat = event.getBlock().getType();
		if (BlockUtil.isReplenishable(blockMat)) {
			Player player = event.getPlayer();
			Locale locale = Lang.getLanguage(player);
			if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.REPLENISH)) {
				return;
			}
			if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.REPLENISH)) {
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (mat.name().toUpperCase().contains("HOE")) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (AureliumSkills.manaManager.getMana(player.getUniqueId()) >= MAbility.REPLENISH.getManaCost(skill.getManaAbilityLevel(MAbility.REPLENISH))) {
							AureliumSkills.manaAbilityManager.activateAbility(player, MAbility.REPLENISH, (int) (MAbility.REPLENISH.getValue(skill.getManaAbilityLevel(MAbility.REPLENISH)) * 20), new Replenish());
						}
						else {
							player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
									.replace("{mana}", String.valueOf(MAbility.REPLENISH.getManaCost(skill.getManaAbilityLevel(MAbility.REPLENISH)))));
						}
					}
				}

			}
		}
	}

	@EventHandler
	public void readyReplenish(PlayerInteractEvent event) {
		AureliumSkills.manaAbilityManager.activator.readyAbility(event, Skill.FARMING, "HOE");
	}

	@EventHandler
	public void replenishBreakBlock(BlockBreakEvent event) {
		//Checks if block broken is ore/stone
		Material mat = event.getBlock().getType();
		if (BlockUtil.isReplenishable(mat)) {
			Player player = event.getPlayer();
			Block block = event.getBlock();
			//Checks if ability is already activated
			if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.REPLENISH)) {
				if (BlockUtil.isFullyGrown(block) && isHoldingHoe(player) && BlockUtil.isReplenishable(mat)) {
					replenishReplant(block, mat);
				}
			}
		}
	}

	private void replenishReplant(Block block, Material material) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!BlockUtil.isNetherWart(material)) {
					if (block.getRelative(BlockFace.DOWN).getType().equals(XMaterial.FARMLAND.parseMaterial())) {
						block.setType(material);
					}
				}
				else {
					if (block.getRelative(BlockFace.DOWN).getType().equals(XMaterial.SOUL_SAND.parseMaterial())) {
						block.setType(material);
					}
				}
			}
		}.runTaskLater(plugin, 4L);
	}

	private boolean isHoldingHoe(Player player) {
		return player.getInventory().getItemInMainHand().getType().name().contains("HOE");
	}

}
