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
import com.archyx.aureliumskills.skills.abilities.mana_abilities.SpeedMine;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Locale;
import java.util.Random;

public class MiningAbilities implements Listener {

	private static final Random r = new Random();
	private static Plugin plugin;
	
	public MiningAbilities(Plugin plugin) {
		MiningAbilities.plugin = plugin;
	}
	
	public static double getModifiedXp(Player player, Source source) {
		PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
		double output = OptionL.getXp(source);
		if (AureliumSkills.abilityOptionManager.isEnabled(Ability.MINER)) {
			double modifier = 1;
			modifier += Ability.MINER.getValue(skill.getAbilityLevel(Ability.MINER)) / 100;
			output *= modifier;
		}
		return output;
	}
	
	public static void luckyMiner(Player player, Block block) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LUCKY_MINER)) {
				if (player.getGameMode().equals(GameMode.SURVIVAL)) {
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (skill.getAbilityLevel(Ability.LUCKY_MINER) > 0) {
							if (r.nextDouble() < (Ability.LUCKY_MINER.getValue(skill.getAbilityLevel(Ability.LUCKY_MINER)) / 100)) {
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
									World world = block.getWorld();
									world.dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void pickMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.PICK_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.mining")) {
					return;
				}
				if (playerSkill.getAbilityLevel(Ability.PICK_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (Ability.PICK_MASTER.getValue(playerSkill.getAbilityLevel(Ability.PICK_MASTER)) / 100)));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void hardenedArmor(PlayerItemDamageEvent event) {
		if (OptionL.isEnabled(Skill.MINING)) {
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

	public static void applyStamina(Player player, PlayerStat playerStat) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(Ability.STAMINA)) {
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (playerSkill != null) {
					if (playerSkill.getAbilityLevel(Ability.STAMINA) > 0) {
						playerStat.addModifier(new StatModifier("mining-stamina", Stat.TOUGHNESS, (int) Ability.STAMINA.getValue(playerSkill.getAbilityLevel(Ability.STAMINA))));
					}
				}
			}
		}
	}

	public static void removeStamina(PlayerStat playerStat) {
		playerStat.removeModifier("mining-stamina");
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void applySpeedMine(BlockBreakEvent event) {
		//Checks if block broken is ore/stone
		Material blockMat = event.getBlock().getType();
		if (blockMat.equals(Material.STONE) || blockMat.equals(Material.COBBLESTONE) || blockMat.equals(Material.COAL_ORE) 
				|| blockMat.equals(Material.IRON_ORE) || blockMat.equals(Material.GOLD_ORE) || blockMat.equals(Material.DIAMOND_ORE)
				|| blockMat.equals(Material.EMERALD_ORE) || blockMat.equals(Material.REDSTONE_ORE) || blockMat.equals(Material.LAPIS_ORE)
				|| blockMat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial()) || blockMat.equals(XMaterial.GRANITE.parseMaterial())
				|| blockMat.equals(XMaterial.DIORITE.parseMaterial()) || blockMat.equals(XMaterial.ANDESITE.parseMaterial())) {
			Player player = event.getPlayer();
			Locale locale = Lang.getLanguage(player);
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
						if (AureliumSkills.manaManager.getMana(player.getUniqueId()) >= MAbility.SPEED_MINE.getManaCost(skill.getManaAbilityLevel(MAbility.SPEED_MINE))) {
							AureliumSkills.manaAbilityManager.activateAbility(player, MAbility.SPEED_MINE, (int) (MAbility.SPEED_MINE.getValue(skill.getManaAbilityLevel(MAbility.SPEED_MINE)) * 20), new SpeedMine());
						}
						else {
							player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale).replace("{mana}", String.valueOf(MAbility.SPEED_MINE.getManaCost(skill.getManaAbilityLevel(MAbility.SPEED_MINE)))));
						}
					}
				}
				
			}
		}
	}

	@EventHandler
	public void readySpeedMine(PlayerInteractEvent event) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (AureliumSkills.abilityOptionManager.isEnabled(MAbility.SPEED_MINE)) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
					if (mat.name().toUpperCase().contains("PICKAXE")) {
						Player player = event.getPlayer();
						Locale locale = Lang.getLanguage(player);
						//Check disabled worlds
						if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
							return;
						}
						//Check permission
						if (!player.hasPermission("aureliumskills.mining")) {
							return;
						}
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							if (SkillLoader.playerSkills.get(player.getUniqueId()).getManaAbilityLevel(MAbility.SPEED_MINE) > 0) {
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
									player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.SPEED_MINE_RAISE, locale));
									new BukkitRunnable() {
										@Override
										public void run() {
											if (!AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), MAbility.SPEED_MINE)) {
												if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), MAbility.SPEED_MINE)) {
													AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), MAbility.SPEED_MINE, false);
													player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.SPEED_MINE_LOWER, locale));
												}
											}
										}
									}.runTaskLater(plugin, 50L);
								} else {
									if (AureliumSkills.manaAbilityManager.getErrorTimer(player.getUniqueId(), MAbility.SPEED_MINE) == 0) {
										player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}", String.valueOf(AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), MAbility.SPEED_MINE))));
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
