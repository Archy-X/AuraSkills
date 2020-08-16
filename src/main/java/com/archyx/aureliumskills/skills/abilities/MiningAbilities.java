package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.SpeedMine;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

public class MiningAbilities implements Listener {

	private static final Random r = new Random();
	private static Plugin plugin;
	
	public MiningAbilities(Plugin plugin) {
		MiningAbilities.plugin = plugin;
	}
	
	public static double getModifiedXp(Player player, Source source) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		double output = Options.getXpAmount(source);
		if (AureliumSkills.abilityOptionManager.isEnabled(Ability.MINER)) {
			double modifier = 1;
			modifier += Ability.MINER.getValue(skill.getAbilityLevel(Ability.MINER)) / 100;
			output *= modifier;
		}
		return output;
	}
	
	public static void luckyMiner(Player player, Block block) {
		if (Options.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LUCKY_MINER)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (skill.getAbilityLevel(Ability.LUCKY_MINER) > 0) {
							if (r.nextDouble() < (Ability.LUCKY_MINER.getValue(skill.getAbilityLevel(Ability.LUCKY_MINER)) / 100)) {
								for (ItemStack item : block.getDrops()) {
									block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void pickMaster(EntityDamageByEntityEvent event) {
		if (Options.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.PICK_MASTER)) {
				if (event.getDamager() instanceof Player) {
					Player player = (Player) event.getDamager();
					//Check disabled worlds
					if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
						return;
					}
					//Check permission
					if (!player.hasPermission("aureliumskills.mining")) {
						return;
					}
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
							Material mat = player.getInventory().getItemInMainHand().getType();
							if (mat.equals(Material.DIAMOND_PICKAXE) || mat.equals(Material.IRON_PICKAXE) || mat.equals(XMaterial.GOLDEN_PICKAXE.parseMaterial())
									|| mat.equals(Material.STONE_PICKAXE) || mat.equals(XMaterial.WOODEN_PICKAXE.parseMaterial())) {
								PlayerSkill s = SkillLoader.playerSkills.get(player.getUniqueId());
								event.setDamage(event.getDamage() * (1 + (Ability.PICK_MASTER.getValue(s.getAbilityLevel(Ability.PICK_MASTER)) / 100)));
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void hardenedArmor(PlayerItemDamageEvent event) {
		if (Options.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.HARDENED_ARMOR)) {
				Player player = event.getPlayer();
				//Check disabled worlds
				if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
					return;
				}
				//Check permission
				if (!player.hasPermission("aureliumskills.mining")) {
					return;
				}
				//Checks if item damaged is armor
				if (ItemUtils.isArmor(event.getItem().getType())) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						//Applies ability
						if (r.nextDouble() < (Ability.HARDENED_ARMOR.getValue(skill.getAbilityLevel(Ability.HARDENED_ARMOR)) / 100)) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void applySpeedMine(BlockBreakEvent event) {
		//Checks if block broken is ore/stone
		Material blockMat = event.getBlock().getType();
		if (blockMat.equals(Material.STONE) || blockMat.equals(Material.COBBLESTONE) || blockMat.equals(Material.COAL_ORE) 
				|| blockMat.equals(Material.IRON_ORE) || blockMat.equals(Material.GOLD_ORE) || blockMat.equals(Material.DIAMOND_ORE)
				|| blockMat.equals(Material.EMERALD_ORE) || blockMat.equals(Material.REDSTONE_ORE) || blockMat.equals(Material.LAPIS_ORE)
				|| blockMat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial()) || blockMat.equals(XMaterial.GRANITE.parseMaterial())
				|| blockMat.equals(XMaterial.DIORITE.parseMaterial()) || blockMat.equals(XMaterial.ANDESITE.parseMaterial())) {
			Player player = event.getPlayer();
			//Checks if speed mine is already activated
			if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.SPEED_MINE)) {
				return;
			}
			//Checks if speed mine is ready
			if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.SPEED_MINE)) {
				//Checks if holding pickaxe
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (mat.name().toUpperCase().contains("PICKAXE")) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (AureliumSkills.manaManager.getMana(player.getUniqueId()) >= MAbility.SPEED_MINE.getManaCost(skill.getAbilityLevel(Ability.SPEED_MINE))) {
							AureliumSkills.manaAbilityManager.activateAbility(player, MAbility.SPEED_MINE, (int) (Ability.SPEED_MINE.getValue(skill.getAbilityLevel(Ability.SPEED_MINE)) * 20), new SpeedMine());
						}
						else {
							player.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.NOT_ENOUGH_MANA) + " " + ChatColor.GRAY + "(" + Lang.getMessage(Message.MANA_REQUIRED).replace("_", "" + MAbility.SPEED_MINE.getManaCost(skill.getAbilityLevel(Ability.SPEED_MINE))) + ")");
						}
					}
				}
				
			}
		}
	}

	@EventHandler
	public void readySpeedMine(PlayerInteractEvent event) {
		if (Options.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SPEED_MINE)) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
					if (mat.name().toUpperCase().contains("PICKAXE")) {
						Player player = event.getPlayer();
						//Check disabled worlds
						if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							return;
						}
						//Check permission
						if (!player.hasPermission("aureliumskills.mining")) {
							return;
						}
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							if (SkillLoader.playerSkills.get(player.getUniqueId()).getAbilityLevel(Ability.SPEED_MINE) > 0) {
								//Checks if speed mine is already activated
								if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.SPEED_MINE)) {
									return;
								}
								//Checks if speed mine is already ready
								if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.SPEED_MINE)) {
									return;
								}
								//Checks if cooldown is reached
								if (AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), MAbility.SPEED_MINE) == 0) {
									AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), MAbility.SPEED_MINE, true);
									player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + Lang.getMessage(Message.SPEED_MINE_RAISE));
									new BukkitRunnable() {
										@Override
										public void run() {
											if (!AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.SPEED_MINE)) {
												if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.SPEED_MINE)) {
													AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), MAbility.SPEED_MINE, false);
													player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + Lang.getMessage(Message.SPEED_MINE_LOWER));
												}
											}
										}
									}.runTaskLater(plugin, 50L);
								} else {
									if (AureliumSkills.manaAbilityManager.getErrorTimer(player.getUniqueId(), MAbility.SPEED_MINE) == 0) {
										player.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(Message.ABILITY_NOT_READY) + " " + ChatColor.GRAY + "(" + AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), MAbility.SPEED_MINE) + "s)");
										AureliumSkills.manaAbilityManager.setErrorTimer(player.getUniqueId(), MAbility.SPEED_MINE, 2);
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
