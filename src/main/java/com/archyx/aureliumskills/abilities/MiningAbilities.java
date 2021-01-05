package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.mana.SpeedMine;
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
import org.bukkit.scheduler.BukkitRunnable;

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
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (skill.getAbilityLevel(Ability.LUCKY_MINER) > 0) {
							if (r.nextDouble() < (getValue(Ability.LUCKY_MINER, skill) / 100)) {
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
	}

	public void pickMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.PICK_MASTER)) {
				//Check permission
				if (!player.hasPermission("aureliumskills.mining")) {
					return;
				}
				if (playerSkill.getAbilityLevel(Ability.PICK_MASTER) > 0) {
					event.setDamage(event.getDamage() * (1 + (getValue(Ability.PICK_MASTER, playerSkill) / 100)));
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
			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
				PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
				//Applies ability
				if (r.nextDouble() < (getValue(Ability.HARDENED_ARMOR, skill) / 100)) {
					event.setCancelled(true);
				}
			}
		}
	}

	public void applyStamina(Player player, PlayerStat playerStat) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (plugin.getAbilityManager().isEnabled(Ability.STAMINA)) {
				PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
				if (playerSkill != null) {
					if (playerSkill.getAbilityLevel(Ability.STAMINA) > 0) {
						playerStat.addModifier(new StatModifier("mining-stamina", Stat.TOUGHNESS, (int) getValue(Ability.STAMINA, playerSkill)));
					}
				}
			}
		}
	}

	public void removeStamina(PlayerStat playerStat) {
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
				|| blockMat.equals(XMaterial.DIORITE.parseMaterial()) || blockMat.equals(XMaterial.ANDESITE.parseMaterial())
				|| blockMat.equals(Material.NETHERRACK) || blockMat.equals(XMaterial.BASALT.parseMaterial()) || blockMat.equals(XMaterial.BLACKSTONE.parseMaterial())) {
			Player player = event.getPlayer();
			Locale locale = Lang.getLanguage(player);
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
					if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
						PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
						if (plugin.getManaManager().getMana(player.getUniqueId()) >= getManaCost(MAbility.SPEED_MINE, skill)) {
							manager.activateAbility(player, MAbility.SPEED_MINE, (int) (getValue(MAbility.SPEED_MINE, skill) * 20), new SpeedMine(plugin));
						}
						else {
							player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale).replace("{mana}", String.valueOf(getManaCost(MAbility.SPEED_MINE, skill))));
						}
					}
				}
				
			}
		}
	}

	@EventHandler
	public void readySpeedMine(PlayerInteractEvent event) {
		if (OptionL.isEnabled(Skill.MINING)) {
			if (plugin.getAbilityManager().isEnabled(MAbility.SPEED_MINE)) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
					if (mat.name().toUpperCase().contains("PICKAXE")) {
						Player player = event.getPlayer();
						Locale locale = Lang.getLanguage(player);
						if (blockAbility(player)) return;
						if (plugin.getManaAbilityManager().getOptionAsBooleanElseFalse(MAbility.SPEED_MINE, "require_sneak")) {
							if (!player.isSneaking()) {
								return;
							}
						}
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							if (SkillLoader.playerSkills.get(player.getUniqueId()).getManaAbilityLevel(MAbility.SPEED_MINE) > 0) {
								ManaAbilityManager manager = plugin.getManaAbilityManager();
								//Checks if speed mine is already activated
								if (manager.isActivated(player.getUniqueId(), MAbility.SPEED_MINE)) {
									return;
								}
								//Checks if speed mine is already ready
								if (manager.isReady(player.getUniqueId(), MAbility.SPEED_MINE)) {
									return;
								}
								//Checks if cooldown is reached
								if (manager.getPlayerCooldown(player.getUniqueId(), MAbility.SPEED_MINE) == 0) {
									manager.setReady(player.getUniqueId(), MAbility.SPEED_MINE, true);
									player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.SPEED_MINE_RAISE, locale));
									new BukkitRunnable() {
										@Override
										public void run() {
											if (!manager.isActivated(player.getUniqueId(), MAbility.SPEED_MINE)) {
												if (manager.isReady(player.getUniqueId(), MAbility.SPEED_MINE)) {
													manager.setReady(player.getUniqueId(), MAbility.SPEED_MINE, false);
													player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.SPEED_MINE_LOWER, locale));
												}
											}
										}
									}.runTaskLater(plugin, 50L);
								} else {
									if (manager.getErrorTimer(player.getUniqueId(), MAbility.SPEED_MINE) == 0) {
										player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}", NumberUtil.format1((double) manager.getPlayerCooldown(player.getUniqueId(), MAbility.SPEED_MINE) / 20)));
										manager.setErrorTimer(player.getUniqueId(), MAbility.SPEED_MINE, 2);
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
