package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.mana.Treecapitator;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.NumberUtil;
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
					PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
					if (skill.getAbilityLevel(Ability.LUMBERJACK) > 0) {
						if (r.nextDouble() < ((getValue(Ability.LUMBERJACK, skill)) / 100)) {
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

	public void axeMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
		if (OptionL.isEnabled(Skill.FORAGING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.AXE_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.foraging")) {
					return;
				}
				if (playerSkill.getAbilityLevel(Ability.AXE_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (getValue(Ability.AXE_MASTER, playerSkill) / 100)));
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
										if (r.nextDouble() < (getValue(Ability.SHREDDER, skill)) / 100) {
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

	public void applyValor(Player player, PlayerStat playerStat) {
		if (OptionL.isEnabled(Skill.FORAGING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.VALOR)) {
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (playerSkill != null) {
					if (playerSkill.getAbilityLevel(Ability.VALOR) > 0) {
						playerStat.addModifier(new StatModifier("foraging-valor", Stat.STRENGTH, (int) getValue(Ability.VALOR, playerSkill)));
					}
				}
			}
		}
	}

	public void removeValor(PlayerStat playerStat) {
		playerStat.removeModifier("foraging-valor");
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void applyTreecapitator(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			//Checks if block broken is log
			Material blockMat = event.getBlock().getType();
			if (blockMat.equals(XMaterial.OAK_LOG.parseMaterial()) || blockMat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || blockMat.equals(XMaterial.SPRUCE_LOG.parseMaterial())
					|| blockMat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || blockMat.equals(XMaterial.ACACIA_LOG.parseMaterial()) || blockMat.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
				Player player = event.getPlayer();
				Locale locale = Lang.getLanguage(player);
				//Checks if treecapitator is already activated
				if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
					return;
				}
				//Checks if treecaptitator is ready
				if (plugin.getManaAbilityManager().isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
					//Checks if holding axe
					Material mat = player.getInventory().getItemInMainHand().getType();
					if (mat.name().toUpperCase().contains("_AXE")) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
							if (plugin.getManaManager().getMana(player.getUniqueId()) >= getManaCost(MAbility.TREECAPITATOR, skill)) {
								plugin.getManaAbilityManager().activateAbility(player, MAbility.TREECAPITATOR, (int) (getValue(MAbility.TREECAPITATOR, skill) * 20), new Treecapitator(plugin));
								treeCapitator(event);
							}
							else {
								plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale).replace("{mana}", String.valueOf(getManaCost(MAbility.TREECAPITATOR, skill))));
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
		if (OptionL.isEnabled(Skill.FORAGING)) {
			if (plugin.getAbilityManager().isEnabled(MAbility.TREECAPITATOR)) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
					if (mat.name().toUpperCase().contains("_AXE")) {
						Player player = event.getPlayer();
						Locale locale = Lang.getLanguage(player);
						// Check WorldEdit wand
						if (mat.equals(XMaterial.WOODEN_AXE.parseMaterial())) {
							if (plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
								return;
							}
						}
						if (blockAbility(player)) return;
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							if (SkillLoader.playerSkills.get(player.getUniqueId()).getManaAbilityLevel(MAbility.TREECAPITATOR) > 0) {
								ManaAbilityManager manager = plugin.getManaAbilityManager();
								//Checks if speed mine is already activated
								if (manager.isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
									return;
								}
								//Checks if speed mine is already ready
								if (manager.isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
									return;
								}
								//Checks if cooldown is reached
								if (manager.getPlayerCooldown(player.getUniqueId(), MAbility.TREECAPITATOR) == 0) {
									manager.setReady(player.getUniqueId(), MAbility.TREECAPITATOR, true);
									plugin.getAbilityManager().sendMessage(player,  Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_RAISE, locale));
									new BukkitRunnable() {
										@Override
										public void run() {
											if (!manager.isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
												if (manager.isReady(player.getUniqueId(), MAbility.TREECAPITATOR)) {
													manager.setReady(player.getUniqueId(), MAbility.TREECAPITATOR, false);
													plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.TREECAPITATOR_LOWER, locale));
												}
											}
										}
									}.runTaskLater(plugin, 50L);
								} else {
									if (manager.getErrorTimer(player.getUniqueId(), MAbility.TREECAPITATOR) == 0) {
										plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}", NumberUtil.format0((double) manager.getPlayerCooldown(player.getUniqueId(), MAbility.TREECAPITATOR) / 20)));
										manager.setErrorTimer(player.getUniqueId(), MAbility.TREECAPITATOR, 2);
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
