package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.Replenish;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.BlockUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;
import java.util.Random;

public class FarmingAbilities extends AbilityProvider implements Listener {

	private static final Random r = new Random();

	public FarmingAbilities(AureliumSkills plugin) {
		super(plugin, Skill.FARMING);
	}

	public void bountifulHarvest(Player player, Block block) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.BOUNTIFUL_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
					if (skill.getAbilityLevel(Ability.BOUNTIFUL_HARVEST) > 0) {
						if (r.nextDouble() < (getValue(Ability.BOUNTIFUL_HARVEST, skill)) / 100) {
							for (ItemStack item : block.getDrops()) {
								PlayerLootDropEvent event = new PlayerLootDropEvent(player, item.clone(), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BOUNTIFUL_HARVEST);
								Bukkit.getPluginManager().callEvent(event);
								if (!event.isCancelled()) {
									block.getWorld().dropItem(event.getLocation(), event.getItemStack());
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void tripleHarvest(Player player, Block block) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.TRIPLE_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
					if (playerSkill != null) {
						if (playerSkill.getAbilityLevel(Ability.TRIPLE_HARVEST) > 0) {
							if (r.nextDouble() < (getValue(Ability.TRIPLE_HARVEST, playerSkill)) / 100) {
								for (ItemStack item : block.getDrops()) {
									ItemStack droppedItem = item.clone();
									droppedItem.setAmount(2);
									PlayerLootDropEvent event = new PlayerLootDropEvent(player, droppedItem, block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.TRIPLE_HARVEST);
									Bukkit.getPluginManager().callEvent(event);
									if (!event.isCancelled()) {
										block.getWorld().dropItem(event.getLocation(), event.getItemStack());
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
	public void geneticist(PlayerItemConsumeEvent event) {
		if (blockDisabled(Ability.GENETICIST)) return;
		Player player = event.getPlayer();
		if (blockAbility(player)) return;
		Material mat = event.getItem().getType();
		if (mat.equals(Material.BREAD) || mat.equals(Material.APPLE) || mat.equals(Material.GOLDEN_APPLE) || mat.equals(XMaterial.POTATO.parseMaterial())
				|| mat.equals(Material.BAKED_POTATO) || mat.equals(XMaterial.CARROT.parseMaterial()) || mat.equals(Material.GOLDEN_CARROT) || mat.equals(Material.MELON)
				|| mat.equals(Material.PUMPKIN_PIE) || mat.equals(Material.BEETROOT) || mat.equals(Material.BEETROOT_SOUP) || mat.equals(XMaterial.MUSHROOM_STEW.parseMaterial())
				|| mat.equals(Material.POISONOUS_POTATO)) {
			PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
			if (playerSkill != null) {
				float amount = (float) getValue(Ability.GENETICIST, playerSkill) / 10;
				player.setSaturation(player.getSaturation() + amount);
			}
		}
	}

	public void scytheMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
		if (blockDisabled(Ability.SCYTHE_MASTER)) return;
			//Check permission
			if (!player.hasPermission("aureliumskills.farming")) {
				return;
			}
			if (playerSkill.getAbilityLevel(Ability.SCYTHE_MASTER) > 0) {
				event.setDamage(event.getDamage() * (1 + (getValue(Ability.SCYTHE_MASTER, playerSkill) / 100)));
			}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void applyReplenish(BlockBreakEvent event) {
		Material blockMat = event.getBlock().getType();
		if (BlockUtil.isReplenishable(blockMat)) {
			Player player = event.getPlayer();
			Locale locale = Lang.getLanguage(player);
			if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.REPLENISH)) {
				return;
			}
			if (plugin.getManaAbilityManager().isReady(player.getUniqueId(), MAbility.REPLENISH)) {
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (mat.name().toUpperCase().contains("HOE")) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (plugin.getManaManager().getMana(player.getUniqueId()) >= plugin.getManaAbilityManager().getManaCost(MAbility.REPLENISH, skill)) {
							plugin.getManaAbilityManager().activateAbility(player, MAbility.REPLENISH, (int) (getValue(MAbility.REPLENISH, skill) * 20), new Replenish(plugin));
						}
						else {
							plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
									.replace("{mana}", String.valueOf(plugin.getManaAbilityManager().getManaCost(MAbility.REPLENISH, skill))));
						}
					}
				}

			}
		}
	}

	@EventHandler
	public void readyReplenish(PlayerInteractEvent event) {
		plugin.getManaAbilityManager().activator.readyAbility(event, Skill.FARMING, new String[] {"HOE"}, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
	}

	@EventHandler
	public void replenishBreakBlock(BlockBreakEvent event) {
		//Checks if block broken is ore/stone
		Material mat = event.getBlock().getType();
		if (BlockUtil.isReplenishable(mat)) {
			Player player = event.getPlayer();
			Block block = event.getBlock();
			//Checks if ability is already activated
			if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.REPLENISH)) {
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
