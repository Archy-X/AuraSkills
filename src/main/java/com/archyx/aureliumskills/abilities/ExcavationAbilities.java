package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.api.event.TerraformBlockBreakEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.mana.Terraform;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.sources.ExcavationSource;
import com.archyx.aureliumskills.skills.sources.SourceTag;
import com.archyx.aureliumskills.util.item.LoreUtil;
import com.archyx.aureliumskills.util.math.NumberUtil;
import me.clip.placeholderapi.PlaceholderAPI;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

public class ExcavationAbilities extends AbilityProvider implements Listener {

	private static final Random r = new Random();

	public ExcavationAbilities(AureliumSkills plugin) {
		super(plugin, Skills.EXCAVATION);
	}

	public void spadeMaster(EntityDamageByEntityEvent event, Player player, PlayerData playerData) {
		if (OptionL.isEnabled(Skills.EXCAVATION)) {
			if (plugin.getAbilityManager().isEnabled(Ability.SPADE_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.excavation")) {
					return;
				}
				if (playerData.getAbilityLevel(Ability.SPADE_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (getValue(Ability.SPADE_MASTER, playerData) / 100)));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void biggerScoop(ExcavationSource source, Block block, Player player) {
		if (!plugin.getAbilityManager().isEnabled(Ability.BIGGER_SCOOP)) return;
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
		if (playerData == null) return;
		if (player.getGameMode() != GameMode.SURVIVAL) return;
		if (r.nextDouble() < (getValue(Ability.BIGGER_SCOOP, playerData) / 100)) {
			ItemStack tool = player.getInventory().getItemInMainHand();
			Material mat =  block.getType();
			for (ItemStack item : block.getDrops(tool)) {
				// If silk touch
				if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
					PlayerLootDropEvent event;
					if (source.getLegacyData() == -1) {
						event = new PlayerLootDropEvent(player, new ItemStack(mat, 2), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
					} else {
						event = new PlayerLootDropEvent(player, new ItemStack(Material.PODZOL, 2, source.getLegacyData()), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
					}
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						block.getWorld().dropItem(event.getLocation(), event.getItemStack());
					}
				}
				// Drop regular item if not silk touch
				else {
					ItemStack drop = item.clone();
					drop.setAmount(2);
					PlayerLootDropEvent event = new PlayerLootDropEvent(player, drop, block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						block.getWorld().dropItem(event.getLocation(), event.getItemStack());
					}
				}
			}
		}

	}

	public void metalDetector(Player player, PlayerData playerData, Block block) {
		// Check if block is applicable to ability
		ExcavationSource source = ExcavationSource.getSource(block);
		if (source == null) return;
		if (!hasTag(source, SourceTag.METAL_DETECTOR_APPLICABLE)) return;

		if (r.nextDouble() < (getValue(Ability.METAL_DETECTOR, playerData) / 100)) {
			int lootTableSize = plugin.getLootTableManager().getLootTable("excavation-rare").getLoot().size();
			if (lootTableSize > 0) {
				Loot loot = plugin.getLootTableManager().getLootTable("excavation-rare").getLoot().get(r.nextInt(lootTableSize));
				// If has item
				if (loot.hasItem()) {
					ItemStack drop = loot.getDrop();
					if (drop != null) {
						PlayerLootDropEvent event = new PlayerLootDropEvent(player, drop.clone(), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.METAL_DETECTOR);
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled()) {
							block.getWorld().dropItem(event.getLocation(), event.getItemStack());
						}
					}
				}
				// If has command
				else if (loot.hasCommand()) {
					String command = loot.getCommand();
					if (plugin.isPlaceholderAPIEnabled()) {
						command = PlaceholderAPI.setPlaceholders(player, command);
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LoreUtil.replace(command, "{player}", player.getName()));
				}
			}
		}
	}

	public void luckySpades(Player player, PlayerData playerData, Block block) {
		// Check if block is applicable to ability
		ExcavationSource source = ExcavationSource.getSource(block);
		if (source == null) return;
		if (!hasTag(source, SourceTag.LUCKY_SPADES_APPLICABLE)) return;

		if (r.nextDouble() < (getValue(Ability.LUCKY_SPADES, playerData) / 100)) {
			int lootTableSize = plugin.getLootTableManager().getLootTable("excavation-epic").getLoot().size();
			if (lootTableSize > 0) {
				Loot loot = plugin.getLootTableManager().getLootTable("excavation-epic").getLoot().get(r.nextInt(lootTableSize));
				// If has item
				if (loot.hasItem()) {
					ItemStack drop = loot.getDrop();
					if (drop != null) {
						PlayerLootDropEvent event = new PlayerLootDropEvent(player, drop.clone(), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.LUCKY_SPADES);
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled()) {
							block.getWorld().dropItem(event.getLocation(), event.getItemStack());
						}
					}
				}
				// If has command
				else if (loot.hasCommand()) {
					String command = loot.getCommand();
					if (plugin.isPlaceholderAPIEnabled()) {
						command = PlaceholderAPI.setPlaceholders(player, command);
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LoreUtil.replace(command, "{player}", player.getName()));
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void excavationListener(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skills.EXCAVATION)) {
			if (!event.isCancelled()) {
				if (event.getClass() != BlockBreakEvent.class) { // Compatibility fix
					return;
				}
				Player player = event.getPlayer();
				Block block = event.getBlock();
				if (blockAbility(player)) return;
				//Applies abilities
				PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
				if (playerData == null) return;
				if (plugin.getAbilityManager().isEnabled(MAbility.TERRAFORM)) {
					if (!block.hasMetadata("AureliumSkills-Terraform")) {
						applyTerraform(player, block);
					}
				}
				//Check game mode
				if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
					return;
				}
				if (plugin.getRegionManager().isPlacedBlock(block)) return;
				if (isEnabled(Ability.METAL_DETECTOR)) {
					metalDetector(player, playerData, block);
				}
				if (isEnabled(Ability.LUCKY_SPADES)) {
					luckySpades(player, playerData, block);
				}
			}
		}
	}

	private void applyTerraform(Player player, Block block) {
		ManaAbilityManager manager = plugin.getManaAbilityManager();
		// Check if block is applicable to ability
		ExcavationSource source = ExcavationSource.getSource(block);
		if (source == null) return;
		if (!hasTag(source, SourceTag.TERRAFORM_APPLICABLE)) return;
		// Apply if activated
		if (manager.isActivated(player.getUniqueId(), MAbility.TERRAFORM)) {
			terraformBreak(player, block);
			return;
		}
		//Checks if speed mine is ready
		if (manager.isReady(player.getUniqueId(), MAbility.TERRAFORM)) {
			//Checks if holding pickaxe
			Material mat = player.getInventory().getItemInMainHand().getType();
			if (mat.name().contains("SHOVEL") || mat.name().contains("SPADE")) {
				PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
				if (playerData == null) return;
				Locale locale = playerData.getLocale();
				if (playerData.getMana() >= getManaCost(MAbility.TERRAFORM, playerData)) {
					manager.activateAbility(player, MAbility.TERRAFORM, (int) (getValue(MAbility.TERRAFORM, playerData) * 20), new Terraform(plugin));
					terraformBreak(player, block);
				}
				else {
					plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
							,"{mana}", NumberUtil.format0(manager.getManaCost(MAbility.TERRAFORM, playerData))
							, "{current_mana}", String.valueOf(Math.round(playerData.getMana()))
							, "{max_mana}", String.valueOf(Math.round(playerData.getMaxMana()))));
				}
			}
		}
	}

	@EventHandler
	private void readyTerraform(PlayerInteractEvent event) {
		plugin.getManaAbilityManager().getActivator().readyAbility(event, Skills.EXCAVATION, new String[] {"SHOVEL", "SPADE"}, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
	}

	private void terraformBreak(Player player, Block block) {
		Material material = block.getType();
		BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
		LinkedList<Block> toCheck = new LinkedList<>();
		toCheck.add(block);
		int count = 0;
		while ((block = toCheck.poll()) != null && count < 61) {
			if (block.getType() == material) {
				block.setMetadata("AureliumSkills-Terraform", new FixedMetadataValue(plugin, true));
				breakBlock(player, block);
				for (BlockFace face : faces) {
					toCheck.add(block.getRelative(face));
				}
				count++;
			}
		}
	}

	private void breakBlock(Player player, Block block) {
		if (!plugin.getTownySupport().canBreak(player, block)) {
			block.removeMetadata("AureliumSkills-Terraform", plugin);
			return;
		}
		TerraformBlockBreakEvent event = new TerraformBlockBreakEvent(block, player);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			block.breakNaturally(player.getInventory().getItemInMainHand());
		}
		block.removeMetadata("AureliumSkills-Terraform", plugin);
	}

}
