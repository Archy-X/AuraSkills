package io.github.archy_x.aureliumskills.skills.abilities;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.lang.Lang;
import io.github.archy_x.aureliumskills.lang.Message;
import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.Source;
import io.github.archy_x.aureliumskills.skills.abilities.mana_abilities.MAbility;
import io.github.archy_x.aureliumskills.skills.abilities.mana_abilities.Treecapitator;
import io.github.archy_x.aureliumskills.util.ItemUtils;
import io.github.archy_x.aureliumskills.util.XMaterial;
import org.bukkit.ChatColor;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ForagingAbilities implements Listener {

	private static final Random r = new Random();
	
	private final Plugin plugin;
	
	public ForagingAbilities(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public static double getModifiedXp(Player player, Source source) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		double output = Options.getXpAmount(source);
		double modifier = 1;
		modifier += Ability.FORAGER.getValue(skill.getAbilityLevel(Ability.FORAGER)) / 100;
		output *= modifier;
		return output;
	}
	
	public static void lumberjack(Player player, Block block) {
		if (player.getGameMode().equals(GameMode.SURVIVAL)) {
			PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
			if (skill.getAbilityLevel(Ability.LUMBERJACK) > 0) {
				if (r.nextDouble() < ((Ability.LUMBERJACK.getValue(skill.getAbilityLevel(Ability.LUMBERJACK))) / 100)) {
					for (ItemStack item : block.getDrops()) {
						player.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
					}
				}
			}
		}
	}

	
	@EventHandler(priority = EventPriority.HIGH)
	public void axeMaster(EntityDamageByEntityEvent event) {
		if (!event.isCancelled()) {
			if (event.getDamager() instanceof Player) {
				Player player = (Player) event.getDamager();
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
						Material mat = player.getInventory().getItemInMainHand().getType();
						if (mat.equals(Material.DIAMOND_AXE) || mat.equals(Material.IRON_AXE) || mat.equals(XMaterial.GOLDEN_AXE.parseMaterial())
								|| mat.equals(Material.STONE_AXE) || mat.equals(XMaterial.WOODEN_AXE.parseMaterial())) {
							PlayerSkill s = SkillLoader.playerSkills.get(player.getUniqueId());
							event.setDamage(event.getDamage() * (1 + (Ability.AXE_MASTER.getValue(s.getAbilityLevel(Ability.AXE_MASTER)) / 100)));
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void shredder(PlayerItemDamageEvent event) {
		if (!event.isCancelled()) {
			//If is item taking durabilty damage is armor
			if (ItemUtils.isArmor(event.getItem().getType())) {
				//If last damage was from entity
				if (event.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getPlayer().getLastDamageCause();
					//If last damage was from player
					if (e.getDamager() instanceof Player) {
						Player player = (Player) e.getDamager();
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							//If damage was an attack
							if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
								//If item used was an axe
								Material mat = player.getInventory().getItemInMainHand().getType();
								if (mat.equals(Material.DIAMOND_AXE) || mat.equals(Material.IRON_AXE) || mat.equals(XMaterial.GOLDEN_AXE.parseMaterial())
										|| mat.equals(Material.STONE_AXE) || mat.equals(XMaterial.WOODEN_AXE.parseMaterial())) {
									PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
									//Checks if shredder is used
									if (skill.getAbilityLevel(Ability.SHREDDER) > 0) {
										if (r.nextDouble() < (Ability.SHREDDER.getValue(skill.getAbilityLevel(Ability.SHREDDER))) / 100) {
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
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void applyTreecapitator(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			//Checks if block broken is log
			Material blockMat = event.getBlock().getType();
			if (blockMat.equals(XMaterial.OAK_LOG.parseMaterial()) || blockMat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || blockMat.equals(XMaterial.SPRUCE_LOG.parseMaterial())
					|| blockMat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || blockMat.equals(XMaterial.ACACIA_LOG.parseMaterial()) || blockMat.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
				Player player = event.getPlayer();
				//Checks if treecapitator is already activated
				if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
					return;
				}
				//Checks if treecaptitator is ready
				if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
					//Checks if holding axe
					Material mat = player.getInventory().getItemInMainHand().getType();
					if (mat.equals(Material.DIAMOND_AXE) || mat.equals(Material.IRON_AXE) || mat.equals(XMaterial.GOLDEN_AXE.parseMaterial())
							|| mat.equals(Material.STONE_AXE) || mat.equals(XMaterial.WOODEN_AXE.parseMaterial())) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {

							PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
							if (AureliumSkills.manaManager.getMana(player.getUniqueId()) >= MAbility.TREECAPITATOR.getManaCost(skill.getAbilityLevel(Ability.TREECAPITATOR))) {
								AureliumSkills.manaAbilityManager.activateAbility(player, MAbility.TREECAPITATOR, (int) (MAbility.TREECAPITATOR.getValue(skill.getAbilityLevel(Ability.TREECAPITATOR)) * 20), new Treecapitator());
								treeCapitator(event);
							}
							else {
								player.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.NOT_ENOUGH_MANA) + " " + ChatColor.GRAY + "(" + Lang.getMessage(Message.MANA_REQUIRED).replace("_", "" + MAbility.TREECAPITATOR.getManaCost(skill.getAbilityLevel(Ability.TREECAPITATOR))) + ")");
							}
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
			if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
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
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
			if (mat.name().toUpperCase().contains("_AXE")) {
				Player player = event.getPlayer();
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					if (SkillLoader.playerSkills.get(player.getUniqueId()).getAbilityLevel(Ability.TREECAPITATOR) > 0) {
						//Checks if speed mine is already activated
						if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
							return;
						}
						//Checks if speed mine is already ready
						if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
							return;
						}
						//Checks if cooldown is reached
						if (AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), MAbility.TREECAPITATOR) == 0) {
							AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), MAbility.TREECAPITATOR, true);
							player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + Lang.getMessage(Message.TREECAPITATOR_RAISE));
							new BukkitRunnable() {
								@Override
								public void run() {
									if (!AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
										if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
											AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), MAbility.TREECAPITATOR, false);
											player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + Lang.getMessage(Message.TREECAPITATOR_LOWER));
										}
									}
								}
							}.runTaskLater(plugin, 50L);
						}
						else {
							if (AureliumSkills.manaAbilityManager.getErrorTimer(player.getUniqueId(), MAbility.TREECAPITATOR) == 0) {
								player.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.ABILITY_NOT_READY) + " " + ChatColor.GRAY + "(" + AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), MAbility.TREECAPITATOR) + "s)");
								AureliumSkills.manaAbilityManager.setErrorTimer(player.getUniqueId(), MAbility.TREECAPITATOR, 2);
							}
						}
					}
				}
			}
		}
	}
}
