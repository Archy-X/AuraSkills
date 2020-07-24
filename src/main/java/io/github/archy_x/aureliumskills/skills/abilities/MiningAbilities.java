package io.github.archy_x.aureliumskills.skills.abilities;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.PlayerSkill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.Source;
import io.github.archy_x.aureliumskills.util.ItemUtils;
import io.github.archy_x.aureliumskills.util.XMaterial;
import org.bukkit.ChatColor;
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
		double modifier = 1;
		modifier += Ability.MINER.getValue(skill.getAbilityLevel(Ability.MINER)) / 100;
		output *= modifier;
		return output;
	}
	
	public static void luckyMiner(Player player, Block block) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
			if (skill.getAbilityLevel(Ability.LUCKY_MINER) > 0) {
				if (r.nextDouble() < (Ability.LUCKY_MINER.getValue(skill.getAbilityLevel(Ability.LUCKY_MINER)) / 100)) {
					for (ItemStack item : block.getDrops()) {
						block.getWorld().dropItemNaturally(block.getLocation(), item);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void pickMaster(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
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
	
	@EventHandler(priority = EventPriority.HIGH)
	public void hardenedArmor(PlayerItemDamageEvent event) {
		//Checks if item damaged is armor
		if (ItemUtils.isArmor(event.getItem().getType())) {
			if (SkillLoader.playerSkills.containsKey(event.getPlayer().getUniqueId())) {
				PlayerSkill skill = SkillLoader.playerSkills.get(event.getPlayer().getUniqueId());
				//Applies ability
				if (r.nextDouble() < (Ability.HARDENED_ARMOR.getValue(skill.getAbilityLevel(Ability.HARDENED_ARMOR)) / 100)) {
					event.setCancelled(true);
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
			if (AureliumSkills.abilityManager.isActivated(player.getUniqueId(), Ability.SPEED_MINE)) {
				return;
			}
			//Checks if speed mine is ready
			if (AureliumSkills.abilityManager.isReady(player.getUniqueId(), Ability.SPEED_MINE)) {
				//Checks if holding pickaxe
				Material mat = player.getInventory().getItemInMainHand().getType();
				if (mat.equals(Material.DIAMOND_PICKAXE) || mat.equals(Material.IRON_PICKAXE) || mat.equals(XMaterial.GOLDEN_PICKAXE.parseMaterial())
						|| mat.equals(Material.STONE_PICKAXE) || mat.equals(XMaterial.WOODEN_PICKAXE.parseMaterial())) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						AureliumSkills.abilityManager.activateAbility(player, Ability.SPEED_MINE, (int) (Ability.SPEED_MINE.getValue(skill.getAbilityLevel(Ability.SPEED_MINE)) * 20), new SpeedMine());
					}
				}
				
			}
		}
	}

	@EventHandler
	public void readySpeedMine(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
			if (mat.equals(Material.DIAMOND_PICKAXE) || mat.equals(Material.IRON_PICKAXE) || mat.equals(XMaterial.GOLDEN_PICKAXE.parseMaterial())
					|| mat.equals(Material.STONE_PICKAXE) || mat.equals(XMaterial.WOODEN_PICKAXE.parseMaterial())) {
				Player player = event.getPlayer();
				if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
					if (SkillLoader.playerSkills.get(player.getUniqueId()).getAbilityLevel(Ability.SPEED_MINE) > 0) {
						//Checks if speed mine is already activated
						if (AureliumSkills.abilityManager.isActivated(player.getUniqueId(), Ability.SPEED_MINE)) {
							return;
						}
						//Checks if speed mine is already ready
						if (AureliumSkills.abilityManager.isReady(player.getUniqueId(), Ability.SPEED_MINE)) {
							return;
						}
						//Checks if cooldown is reached
						if (AureliumSkills.abilityManager.getCooldown(player.getUniqueId(), Ability.SPEED_MINE) == 0) {
							AureliumSkills.abilityManager.setReady(player.getUniqueId(), Ability.SPEED_MINE, true);
							player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "You raise your pickaxe");
							new BukkitRunnable() {
								@Override
								public void run() {
									if (!AureliumSkills.abilityManager.isActivated(player.getUniqueId(), Ability.SPEED_MINE)) {
										if (AureliumSkills.abilityManager.isReady(player.getUniqueId(), Ability.SPEED_MINE)) {
											AureliumSkills.abilityManager.setReady(player.getUniqueId(), Ability.SPEED_MINE, false);
											player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + "You lower your pickaxe");
										}
									}
								}
							}.runTaskLater(plugin, 50L);
						}
						else {
							if (AureliumSkills.abilityManager.getErrorTimer(player.getUniqueId(), Ability.SPEED_MINE) == 0) {
								player.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + "Ability not ready! " + ChatColor.GRAY + "(" + AureliumSkills.abilityManager.getCooldown(player.getUniqueId(), Ability.SPEED_MINE) + "s)");
								AureliumSkills.abilityManager.setErrorTimer(player.getUniqueId(), Ability.SPEED_MINE, 2);
							}
						}
					}
				}
			}
		}
	}
}
