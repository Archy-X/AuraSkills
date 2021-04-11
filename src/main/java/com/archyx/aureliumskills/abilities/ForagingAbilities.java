package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.Treecapitator;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.stats.Stats;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.item.LoreUtil;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;
import java.util.Random;

public class ForagingAbilities extends AbilityProvider implements Listener {

	private final Random r = new Random();
	
	public ForagingAbilities(AureliumSkills plugin) {
		super(plugin, Skill.FORAGING);
	}
	
	public void lumberjack(Player player, Block block) {
		if (OptionL.isEnabled(Skill.FORAGING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.LUMBERJACK)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
					if (playerData == null) return;
					if (playerData.getAbilityLevel(Ability.LUMBERJACK) > 0) {
						if (r.nextDouble() < ((getValue(Ability.LUMBERJACK, playerData)) / 100)) {
							for (ItemStack item : block.getDrops()) {
								PlayerLootDropEvent event = new PlayerLootDropEvent(player, item.clone(), block.getLocation().add(0.5, 0.5, 0.5), LootDropCause.LUMBERJACK);
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

	public void axeMaster(EntityDamageByEntityEvent event, Player player, PlayerData playerData) {
		if (OptionL.isEnabled(Skill.FORAGING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.AXE_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.foraging")) {
					return;
				}
				if (playerData.getAbilityLevel(Ability.AXE_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (getValue(Ability.AXE_MASTER, playerData) / 100)));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void shredder(PlayerItemDamageEvent event) {
		if (blockDisabled(Ability.SHREDDER)) return;
		if (!event.isCancelled()) {
			//If is item taking durabilty damage is armor
			if (ItemUtils.isArmor(event.getItem().getType())) {
				//If last damage was from entity
				if (event.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getPlayer().getLastDamageCause();
					//If last damage was from player
					if (e.getDamager() instanceof Player) {
						Player player = (Player) e.getDamager();
						if (blockAbility(player)) return;
						//If damage was an attack
						if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
							//If item used was an axe
							Material mat = player.getInventory().getItemInMainHand().getType();
							if (mat.equals(Material.DIAMOND_AXE) || mat.equals(Material.IRON_AXE) || mat.equals(XMaterial.GOLDEN_AXE.parseMaterial())
									|| mat.equals(Material.STONE_AXE) || mat.equals(XMaterial.WOODEN_AXE.parseMaterial())) {
								PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
								if (playerData == null) return;
								//Checks if shredder is used
								if (playerData.getAbilityLevel(Ability.SHREDDER) > 0) {
									if (r.nextDouble() < (getValue(Ability.SHREDDER, playerData)) / 100) {
										event.setDamage(event.getDamage() * 3);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void applyValor(PlayerData playerData) {
		if (OptionL.isEnabled(Skill.FORAGING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.VALOR)) {
				if (playerData.getAbilityLevel(Ability.VALOR) > 0) {
					playerData.addStatModifier(new StatModifier("foraging-valor", Stats.STRENGTH, (int) getValue(Ability.VALOR, playerData)));
				}
			}
		}
	}

	public void removeValor(PlayerData playerData) {
		playerData.removeStatModifier("foraging-valor");
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void applyTreecapitator(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			//Checks if block broken is log
			Material blockMat = event.getBlock().getType();
			if (blockMat.equals(XMaterial.OAK_LOG.parseMaterial()) || blockMat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || blockMat.equals(XMaterial.SPRUCE_LOG.parseMaterial())
					|| blockMat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || blockMat.equals(XMaterial.ACACIA_LOG.parseMaterial()) || blockMat.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
				Player player = event.getPlayer();
				//Checks if treecapitator is already activated
				if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
					return;
				}
				//Checks if treecaptitator is ready
				if (plugin.getManaAbilityManager().isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
					//Checks if holding axe
					Material mat = player.getInventory().getItemInMainHand().getType();
					if (mat.name().toUpperCase().contains("_AXE")) {
						PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
						if (playerData == null) return;
						Locale locale = playerData.getLocale();
						if (playerData.getMana() >= getManaCost(MAbility.TREECAPITATOR, playerData)) {
							plugin.getManaAbilityManager().activateAbility(player, MAbility.TREECAPITATOR, (int) (getValue(MAbility.TREECAPITATOR, playerData) * 20), new Treecapitator(plugin));
							treeCapitator(event);
						}
						else {
							plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale).replace("{mana}", String.valueOf(getManaCost(MAbility.TREECAPITATOR, playerData))));plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
									,"{mana}", NumberUtil.format0(plugin.getManaAbilityManager().getManaCost(MAbility.TREECAPITATOR, playerData))
									, "{current_mana}", String.valueOf(Math.round(playerData.getMana()))
									, "{max_mana}", String.valueOf(Math.round(playerData.getMaxMana()))));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void treeCapitator(BlockBreakEvent event) {
		//Checks if block broken is ore/stone
		Material blockMat = event.getBlock().getType();
		if (blockMat.equals(XMaterial.OAK_LOG.parseMaterial()) || blockMat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || blockMat.equals(XMaterial.SPRUCE_LOG.parseMaterial())
				|| blockMat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || blockMat.equals(XMaterial.ACACIA_LOG.parseMaterial()) || blockMat.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
			Player player = event.getPlayer();
			//Checks if speed mine is already activated
			if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
				breakBlock(event.getBlock().getState(), 0);
			}
		}
	}
	
	private void breakBlock(BlockState state, int num) {
		if (num > 20) {
			return;
		}
		BlockState above = state.getBlock().getRelative(BlockFace.UP).getState();
		Material matAbove = above.getType();
		if (matAbove.equals(XMaterial.OAK_LOG.parseMaterial()) || matAbove.equals(XMaterial.SPRUCE_LOG.parseMaterial()) || matAbove.equals(XMaterial.BIRCH_LOG.parseMaterial())
				|| matAbove.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || matAbove.equals(XMaterial.ACACIA_LOG.parseMaterial()) || matAbove.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
			above.getBlock().breakNaturally();
			new BukkitRunnable() {
				@Override
				public void run() {
					breakBlock(above, num + 1);
				}
			}.runTaskLater(plugin, 1L);
		}
		else {
			checkLeaf(above);
			checkLeaf(above.getBlock().getRelative(BlockFace.NORTH).getState());
			checkLeaf(above.getBlock().getRelative(BlockFace.SOUTH).getState());
			checkLeaf(above.getBlock().getRelative(BlockFace.EAST).getState());
			checkLeaf(above.getBlock().getRelative(BlockFace.WEST).getState());

		}
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH_EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH_WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH_EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH_WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH_NORTH_EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH_NORTH_WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.EAST_NORTH_EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.EAST_SOUTH_EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.WEST_NORTH_WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.WEST_SOUTH_WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH_NORTH_EAST).getRelative(BlockFace.EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_EAST).getRelative(BlockFace.EAST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.NORTH_NORTH_WEST).getRelative(BlockFace.WEST).getState());
		checkLeaf(state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_WEST).getRelative(BlockFace.WEST).getState());
	}

	private void checkLeaf(BlockState state) {
		Material material = state.getType();
		if (material.equals(XMaterial.OAK_LEAVES.parseMaterial()) || material.equals(XMaterial.SPRUCE_LEAVES.parseMaterial()) || material.equals(XMaterial.BIRCH_LEAVES.parseMaterial())
			|| material.equals(XMaterial.JUNGLE_LEAVES.parseMaterial()) || material.equals(XMaterial.ACACIA_LEAVES.parseMaterial()) || material.equals(XMaterial.DARK_OAK_LEAVES.parseMaterial())) {
			state.getBlock().breakNaturally();
		}
	}
	
	@EventHandler
	public void readyTreecapitator(PlayerInteractEvent event) {
		if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(XMaterial.WOODEN_AXE.parseMaterial())) {
			if (plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
				return;
			}
		}
		plugin.getManaAbilityManager().getActivator().readyAbility(event, Skill.FORAGING, new String[]{"_AXE"}, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
	}
}
