package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.Treecapitator;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.cryptomorin.xseries.XMaterial;
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
		double output = OptionL.getXp(source);
		if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FORAGER)) {
			double modifier = 1;
			modifier += Ability.FORAGER.getValue(skill.getAbilityLevel(Ability.FORAGER)) / 100;
			output *= modifier;
		}
		return output;
	}
	
	public static void lumberjack(Player player, Block block) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LUMBERJACK)) {
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
		}
	}

	public static void axeMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.AXE_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.foraging")) {
					return;
				}
				if (playerSkill.getAbilityLevel(Ability.AXE_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (Ability.AXE_MASTER.getValue(playerSkill.getAbilityLevel(Ability.AXE_MASTER)) / 100)));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void shredder(PlayerItemDamageEvent event) {
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SHREDDER)) {
				if (!event.isCancelled()) {
					//If is item taking durabilty damage is armor
					if (ItemUtils.isArmor(event.getItem().getType())) {
						//If last damage was from entity
						if (event.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
							EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getPlayer().getLastDamageCause();
							//If last damage was from player
							if (e.getDamager() instanceof Player) {
								Player player = (Player) e.getDamager();
								//Check permission
								if (!player.hasPermission("aureliumskills.foraging")) {
									return;
								}
								//Check disabled worlds
								if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
									return;
								}
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
		}
	}

	public static void applyValor(Player player, PlayerStat playerStat) {
		if (OptionL.isEnabled(Skill.FORAGING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.VALOR)) {
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (playerSkill != null) {
					if (playerSkill.getAbilityLevel(Ability.VALOR) > 0) {
						playerStat.addModifier(new StatModifier("foraging-valor", Stat.STRENGTH, (int) Ability.VALOR.getValue(playerSkill.getAbilityLevel(Ability.VALOR))));
					}
				}
			}
		}
	}

	public static void removeValor(PlayerStat playerStat) {
		playerStat.removeModifier("foraging-valor");
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
					if (mat.name().toUpperCase().contains("_AXE")) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
							if (AureliumSkills.manaManager.getMana(player.getUniqueId()) >= MAbility.TREECAPITATOR.getManaCost(skill.getManaAbilityLevel(MAbility.TREECAPITATOR))) {
								AureliumSkills.manaAbilityManager.activateAbility(player, MAbility.TREECAPITATOR, (int) (MAbility.TREECAPITATOR.getValue(skill.getManaAbilityLevel(MAbility.TREECAPITATOR)) * 20), new Treecapitator());
								treeCapitator(event);
							}
							else {
								player.sendMessage(AureliumSkills.tag + Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA).replace("{mana}", String.valueOf(MAbility.TREECAPITATOR.getManaCost(skill.getManaAbilityLevel(MAbility.TREECAPITATOR)))));
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
		if (OptionL.isEnabled(Skill.FARMING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(MAbility.TREECAPITATOR)) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
					if (mat.name().toUpperCase().contains("_AXE")) {
						Player player = event.getPlayer();
						//Check permission
						if (!player.hasPermission("aureliumskills.foraging")) {
							return;
						}
						//Check disabled worlds
						if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							return;
						}
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							if (SkillLoader.playerSkills.get(player.getUniqueId()).getManaAbilityLevel(MAbility.TREECAPITATOR) > 0) {
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
									player.sendMessage(AureliumSkills.tag + Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_RAISE));
									new BukkitRunnable() {
										@Override
										public void run() {
											if (!AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
												if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
													AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), MAbility.TREECAPITATOR, false);
													player.sendMessage(AureliumSkills.tag + Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_LOWER));
												}
											}
										}
									}.runTaskLater(plugin, 50L);
								} else {
									if (AureliumSkills.manaAbilityManager.getErrorTimer(player.getUniqueId(), MAbility.TREECAPITATOR) == 0) {
										player.sendMessage(AureliumSkills.tag + Lang.getMessage(ManaAbilityMessage.NOT_READY).replace("{cooldown}", String.valueOf(AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), MAbility.TREECAPITATOR))));
										AureliumSkills.manaAbilityManager.setErrorTimer(player.getUniqueId(), MAbility.TREECAPITATOR, 2);
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
