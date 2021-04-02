package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.api.event.TerraformBlockBreakEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.mana.Terraform;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import com.cryptomorin.xseries.XMaterial;
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
	private final Material[] loadedMaterials;

	public ExcavationAbilities(AureliumSkills plugin) {
		super(plugin, Skill.EXCAVATION);
		//Load materials
		XMaterial[] materials = new XMaterial[]{
				XMaterial.DIRT, XMaterial.GRASS_BLOCK, XMaterial.COARSE_DIRT, XMaterial.PODZOL,
				XMaterial.SAND, XMaterial.RED_SAND, XMaterial.SOUL_SAND, XMaterial.SOUL_SOIL,
				XMaterial.CLAY, XMaterial.GRAVEL, XMaterial.MYCELIUM
		};
		loadedMaterials = new Material[materials.length];
		for (int i = 0; i < loadedMaterials.length; i++) {
			loadedMaterials[i] = materials[i].parseMaterial();
		}
	}

	public void spadeMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
		if (OptionL.isEnabled(Skill.EXCAVATION)) {
			if (plugin.getAbilityManager().isEnabled(Ability.SPADE_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.excavation")) {
					return;
				}
				if (playerSkill.getAbilityLevel(Ability.SPADE_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (getValue(Ability.SPADE_MASTER, playerSkill) / 100)));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void biggerScoop(PlayerSkill playerSkill, Block block, Player player) {
		if (isExcavationMaterial(block.getType())) {
			if (r.nextDouble() < (getValue(Ability.BIGGER_SCOOP, playerSkill) / 100)) {
				ItemStack tool = player.getInventory().getItemInMainHand();
				Material mat =  block.getType();
				for (ItemStack item : block.getDrops(tool)) {
					//If silk touch
					if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
						if (mat.equals(XMaterial.GRASS_BLOCK.parseMaterial())) {
							Material grassBlock = XMaterial.GRASS_BLOCK.parseMaterial();
							if (grassBlock != null) {
								PlayerLootDropEvent event = new PlayerLootDropEvent(player, new ItemStack(grassBlock, 2), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
								Bukkit.getPluginManager().callEvent(event);
								if (!event.isCancelled()) {
									block.getWorld().dropItem(event.getLocation(), event.getItemStack());
								}
							}
						}
						else if (mat.equals(XMaterial.MYCELIUM.parseMaterial())) {
							Material mycelium = XMaterial.MYCELIUM.parseMaterial();
							if (mycelium != null) {
								PlayerLootDropEvent event = new PlayerLootDropEvent(player, new ItemStack(mycelium, 2), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
								Bukkit.getPluginManager().callEvent(event);
								if (!event.isCancelled()) {
									block.getWorld().dropItem(event.getLocation(), event.getItemStack());
								}
							}
						}
						else if (mat.equals(XMaterial.CLAY.parseMaterial())) {
							Material clay = XMaterial.CLAY.parseMaterial();
							if (clay != null) {
								PlayerLootDropEvent event = new PlayerLootDropEvent(player, new ItemStack(clay, 2), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
								Bukkit.getPluginManager().callEvent(event);
								if (!event.isCancelled()) {
									block.getWorld().dropItem(event.getLocation(), event.getItemStack());
								}
							}
						}
						if (XMaterial.isNewVersion()) {
							if (mat.equals(XMaterial.PODZOL.parseMaterial())) {
								PlayerLootDropEvent event = new PlayerLootDropEvent(player, new ItemStack(Material.PODZOL, 2), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
								Bukkit.getPluginManager().callEvent(event);
								if (!event.isCancelled()) {
									block.getWorld().dropItem(event.getLocation(), event.getItemStack());
								}
							}
						}
						else {
							if (mat.equals(Material.DIRT)) {
								if (block.getData() == 2) {
									PlayerLootDropEvent event = new PlayerLootDropEvent(player, new ItemStack(Material.DIRT, 2), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.BIGGER_SCOOP);
									Bukkit.getPluginManager().callEvent(event);
									if (!event.isCancelled()) {
										block.getWorld().dropItem(event.getLocation(), event.getItemStack());
									}
								}
							}
						}
					}
					//Drop regular item if not silk touch
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
	}

	public void metalDetector(Player player, PlayerSkill playerSkill, Block block) {
		if (isExcavationMaterial(block.getType())) {
			if (r.nextDouble() < (getValue(Ability.METAL_DETECTOR, playerSkill) / 100)) {
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
	}

	public void luckySpades(Player player, PlayerSkill playerSkill, Block block) {
		if (isExcavationMaterial(block.getType())) {
			if (r.nextDouble() < (getValue(Ability.LUCKY_SPADES, playerSkill) / 100)) {
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
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void excavationListener(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skill.EXCAVATION)) {
			if (!event.isCancelled()) {
				if (event.getClass() != BlockBreakEvent.class) { // Compatibility fix
					return;
				}
				Player player = event.getPlayer();
				Block block = event.getBlock();
				if (blockAbility(player)) return;
				//Applies abilities
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (playerSkill == null) return;
				if (plugin.getAbilityManager().isEnabled(MAbility.TERRAFORM)) {
					if (!block.hasMetadata("AureliumSkills-Terraform")) {
						applyTerraform(player, block);
					}
				}
				//Check game mode
				if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
					return;
				}
				if (!block.hasMetadata("skillsPlaced")) {
					if (isEnabled(Ability.BIGGER_SCOOP)) {
						biggerScoop(playerSkill, block, player);
					}
					if (isEnabled(Ability.METAL_DETECTOR)) {
						metalDetector(player, playerSkill, block);
					}
					if (isEnabled(Ability.LUCKY_SPADES)) {
						luckySpades(player, playerSkill, block);
					}
				}
			}
		}
	}

	private boolean isExcavationMaterial(Material material) {
		for (Material checkedMaterial : loadedMaterials) {
			if (material == checkedMaterial) {
				return true;
			}
		}
		return false;
	}

	private void applyTerraform(Player player, Block block) {
		Locale locale = Lang.getLanguage(player);
		ManaAbilityManager manager = plugin.getManaAbilityManager();
		if (!isExcavationMaterial(block.getType())) return;
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
				PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (skill == null) return;
				if (plugin.getManaManager().getMana(player.getUniqueId()) >= getManaCost(MAbility.TERRAFORM, skill)) {
					manager.activateAbility(player, MAbility.TERRAFORM, (int) (getValue(MAbility.TERRAFORM, skill) * 20), new Terraform(plugin));
					terraformBreak(player, block);
				}
				else {
					plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
							,"{mana}", NumberUtil.format0(plugin.getManaAbilityManager().getManaCost(MAbility.TERRAFORM, skill))
							, "{current_mana}", String.valueOf(Math.round(plugin.getManaManager().getMana(player.getUniqueId())))
							, "{max_mana}", String.valueOf(Math.round(plugin.getManaManager().getMaxMana(player.getUniqueId())))));
				}
			}
		}
	}

	@EventHandler
	private void readyTerraform(PlayerInteractEvent event) {
		plugin.getManaAbilityManager().getActivator().readyAbility(event, Skill.EXCAVATION, new String[] {"SHOVEL", "SPADE"}, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
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
