package com.archyx.aureliumskills.skills.farming;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.ability.AbilityProvider;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.Replenish;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.block.BlockUtil;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
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
		super(plugin, Skills.FARMING);
	}

	public void bountifulHarvest(Player player, Block block) {
		if (OptionL.isEnabled(Skills.FARMING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.BOUNTIFUL_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
					if (playerData == null) return;
					if (playerData.getAbilityLevel(Ability.BOUNTIFUL_HARVEST) > 0) {
						if (r.nextDouble() < (getValue(Ability.BOUNTIFUL_HARVEST, playerData)) / 100) {
							for (ItemStack item : block.getDrops()) {
								checkMelonSilkTouch(player, block, item);
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
		if (OptionL.isEnabled(Skills.FARMING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.TRIPLE_HARVEST)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
					if (playerData == null) return;
					if (playerData.getAbilityLevel(Ability.TRIPLE_HARVEST) > 0) {
						if (r.nextDouble() < (getValue(Ability.TRIPLE_HARVEST, playerData)) / 100) {
							for (ItemStack item : block.getDrops()) {
								checkMelonSilkTouch(player, block, item);
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

	private void checkMelonSilkTouch(Player player, Block block, ItemStack item) {
		if (block.getType() == XMaterial.MELON.parseMaterial()) {
			if (player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
				Material melon = XMaterial.MELON.parseMaterial();
				if (melon != null) {
					item.setType(melon);
					item.setAmount(1);
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
			PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
			if (playerData == null) return;
			float amount = (float) getValue(Ability.GENETICIST, playerData) / 10;
			player.setSaturation(player.getSaturation() + amount);
		}
	}

	public void scytheMaster(EntityDamageByEntityEvent event, Player player, PlayerData playerData) {
		if (blockDisabled(Ability.SCYTHE_MASTER)) return;
			//Check permission
			if (!player.hasPermission("aureliumskills.farming")) {
				return;
			}
			if (playerData.getAbilityLevel(Ability.SCYTHE_MASTER) > 0) {
				event.setDamage(event.getDamage() * (1 + (getValue(Ability.SCYTHE_MASTER, playerData) / 100)));
			}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void applyReplenish(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		Material blockMat = event.getBlock().getType();
		if (BlockUtil.isReplenishable(blockMat)) {
			Player player = event.getPlayer();
			if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.REPLENISH)) {
				return;
			}
			if (plugin.getManaAbilityManager().isReady(player.getUniqueId(), MAbility.REPLENISH)) {
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (mat.name().toUpperCase().contains("HOE")) {
					PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
					if (playerData == null) return;
					Locale locale = playerData.getLocale();
					if (playerData.getMana() >= plugin.getManaAbilityManager().getManaCost(MAbility.REPLENISH, playerData)) {
						plugin.getManaAbilityManager().activateAbility(player, MAbility.REPLENISH, (int) (getValue(MAbility.REPLENISH, playerData) * 20), new Replenish(plugin));
						Block block = event.getBlock();
						if (BlockUtil.isFullyGrown(block) && isHoldingHoe(player) && BlockUtil.isReplenishable(blockMat)) {
							replenishReplant(block, blockMat);
						}
					}
					else {
						plugin.getAbilityManager().sendMessage(player, TextUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
								,"{mana}", NumberUtil.format0(plugin.getManaAbilityManager().getManaCost(MAbility.REPLENISH, playerData))
								, "{current_mana}", String.valueOf(Math.round(playerData.getMana()))
								, "{max_mana}", String.valueOf(Math.round(playerData.getMaxMana()))));
					}
				}

			}
		}
	}

	@EventHandler
	public void readyReplenish(PlayerInteractEvent event) {
		plugin.getManaAbilityManager().getActivator().readyAbility(event, Skills.FARMING, new String[] {"HOE"}, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
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
		}.runTaskLater(plugin, plugin.getManaAbilityManager().getOptionAsInt(MAbility.REPLENISH, "replant_delay", 4));
	}

	private boolean isHoldingHoe(Player player) {
		return player.getInventory().getItemInMainHand().getType().name().contains("HOE");
	}

}
