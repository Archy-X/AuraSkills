package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.mana.SpeedMine;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.item.LoreUtil;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Locale;
import java.util.Random;

public class MiningAbilities extends AbilityProvider implements Listener {

	private final Random r = new Random();

	public MiningAbilities(AureliumSkills plugin) {
		super(plugin, Skill.MINING);
	}

	public void luckyMiner(Player player, Block block) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.LUCKY_MINER)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
					if (playerData == null) return;
					if (playerData.getAbilityLevel(Ability.LUCKY_MINER) > 0) {
						if (r.nextDouble() < (getValue(Ability.LUCKY_MINER, playerData) / 100)) {
							ItemStack tool = player.getInventory().getItemInMainHand();
							Material mat = block.getType();
							if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
								if (mat.equals(Material.DIAMOND_ORE) || mat.equals(Material.LAPIS_ORE) ||
									mat.equals(Material.REDSTONE_ORE) || mat.name().equals("GLOWING_REDSTONE_ORE") ||
									mat.equals(Material.EMERALD_ORE) || mat.equals(Material.COAL_ORE) ||
									mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial()) || mat.equals(XMaterial.NETHER_GOLD_ORE.parseMaterial())) {
									return;
								}
							}
							Collection<ItemStack> drops = block.getDrops(tool);
							for (ItemStack item : drops) {
								PlayerLootDropEvent event = new PlayerLootDropEvent(player, item.clone(), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.LUCKY_MINER);
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

	public void pickMaster(EntityDamageByEntityEvent event, Player player, PlayerData playerData) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.PICK_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.mining")) {
					return;
				}
				if (playerData.getAbilityLevel(Ability.PICK_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (getValue(Ability.PICK_MASTER, playerData) / 100)));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void hardenedArmor(PlayerItemDamageEvent event) {
		if (blockDisabled(Ability.HARDENED_ARMOR)) return;
		Player player = event.getPlayer();
		if (blockAbility(player)) return;
		//Checks if item damaged is armor
		if (ItemUtils.isArmor(event.getItem().getType())) {
			PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
			if (playerData == null) return;
			//Applies ability
			if (r.nextDouble() < (getValue(Ability.HARDENED_ARMOR, playerData) / 100)) {
				event.setCancelled(true);
			}
		}
	}

	public void applyStamina(PlayerData playerData) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.STAMINA)) {
				if (playerData.getAbilityLevel(Ability.STAMINA) > 0) {
					playerData.addStatModifier(new StatModifier("mining-stamina", Stat.TOUGHNESS, (int) getValue(Ability.STAMINA, playerData)));
				}
			}
		}
	}

	public void removeStamina(PlayerData playerData) {
		playerData.removeStatModifier("mining-stamina");
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void applySpeedMine(BlockBreakEvent event) {
		//Checks if block broken is ore/stone
		Material blockMat = event.getBlock().getType();
		if (blockMat.equals(Material.STONE) || blockMat.equals(Material.COBBLESTONE) || blockMat.equals(Material.COAL_ORE) 
				|| blockMat.equals(Material.IRON_ORE) || blockMat.equals(Material.GOLD_ORE) || blockMat.equals(Material.DIAMOND_ORE)
				|| blockMat.equals(Material.EMERALD_ORE) || blockMat.equals(Material.REDSTONE_ORE) || blockMat.equals(Material.LAPIS_ORE)
				|| blockMat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial()) || blockMat.equals(XMaterial.GRANITE.parseMaterial())
				|| blockMat.equals(XMaterial.DIORITE.parseMaterial()) || blockMat.equals(XMaterial.ANDESITE.parseMaterial())
				|| blockMat.equals(Material.NETHERRACK) || blockMat.equals(XMaterial.BASALT.parseMaterial()) || blockMat.equals(XMaterial.BLACKSTONE.parseMaterial())) {
			Player player = event.getPlayer();
			//Checks if speed mine is already activated
			ManaAbilityManager manager = plugin.getManaAbilityManager();
			if (manager.isActivated(player.getUniqueId(), MAbility.SPEED_MINE)) {
				return;
			}
			//Checks if speed mine is ready
			if (manager.isReady(player.getUniqueId(), MAbility.SPEED_MINE)) {
				//Checks if holding pickaxe
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (mat.name().toUpperCase().contains("PICKAXE")) {
					PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
					if (playerData == null) return;
					Locale locale = playerData.getLocale();
					if (playerData.getMana() >= getManaCost(MAbility.SPEED_MINE, playerData)) {
						manager.activateAbility(player, MAbility.SPEED_MINE, (int) (getValue(MAbility.SPEED_MINE, playerData) * 20), new SpeedMine(plugin));
					}
					else {
						plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
								,"{mana}", NumberUtil.format0(plugin.getManaAbilityManager().getManaCost(MAbility.SPEED_MINE, playerData))
								, "{current_mana}", String.valueOf(Math.round(playerData.getMana()))
								, "{max_mana}", String.valueOf(Math.round(playerData.getMaxMana()))));
					}
				}
			}
		}
	}

	@EventHandler
	public void readySpeedMine(PlayerInteractEvent event) {
		plugin.getManaAbilityManager().getActivator().readyAbility(event, Skill.MINING, new String[] {"PICKAXE"}, Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR);
	}
}
