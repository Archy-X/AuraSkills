package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.mana.SharpHook;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.Random;

public class FishingAbilities extends AbilityProvider implements Listener {

	private final Random r = new Random();

	public FishingAbilities(AureliumSkills plugin) {
		super(plugin, Skill.FISHING);
	}
	
	@EventHandler
	public void luckyCatch(PlayerFishEvent event) {
		if (blockDisabled(Ability.LUCKY_CATCH)) return;
		Player player = event.getPlayer();
		if (blockAbility(player)) return;
		if (event.getCaught() instanceof Item) {
			if (event.getExpToDrop() > 0) {
				PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
				if (playerData == null) return;
				if (r.nextDouble() < (getValue(Ability.LUCKY_CATCH, playerData) / 100)) {
					Item item = (Item) event.getCaught();
					ItemStack drop = item.getItemStack();
					if (drop.getMaxStackSize() > 1) {
						drop.setAmount(drop.getAmount() * 2);
						PlayerLootDropEvent dropEvent = new PlayerLootDropEvent(player, drop, item.getLocation(), LootDropCause.LUCKY_CATCH);
						Bukkit.getPluginManager().callEvent(dropEvent);
						if (!event.isCancelled()) {
							item.setItemStack(dropEvent.getItemStack());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void treasureHunterAndEpicCatch(PlayerFishEvent event) {
		if (OptionL.isEnabled(Skill.FISHING)) {
			Player player = event.getPlayer();
			if (blockAbility(player)) return;
			if (plugin.getWorldManager().isInBlockedWorld(player.getLocation())) {
				return;
			}
			if (plugin.isWorldGuardEnabled()) {
				if (plugin.getWorldGuardSupport().isInBlockedRegion(player.getLocation())) {
					return;
				}
			}
			if (event.getCaught() instanceof Item) {
				if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
					if (event.getExpToDrop() > 0) {
						PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
						if (playerData == null) return;
						if (r.nextDouble() < (getValue(Ability.EPIC_CATCH, playerData) / 100)) {
							if (plugin.getAbilityManager().isEnabled(Ability.EPIC_CATCH)) {
								Item item = (Item) event.getCaught();
								int lootTableSize = plugin.getLootTableManager().getLootTable("fishing-epic").getLoot().size();
								if (lootTableSize > 0) {
									Loot loot = plugin.getLootTableManager().getLootTable("fishing-epic").getLoot().get(r.nextInt(lootTableSize));
									// If has item
									if (loot.hasItem()) {
										ItemStack drop = loot.getDrop();
										if (drop != null) {
											PlayerLootDropEvent dropEvent = new PlayerLootDropEvent(player, drop, item.getLocation(), LootDropCause.EPIC_CATCH);
											Bukkit.getPluginManager().callEvent(dropEvent);
											if (!event.isCancelled()) {
												item.setItemStack(dropEvent.getItemStack());
												plugin.getLeveler().addXp(event.getPlayer(), Skill.FISHING, getXp(event.getPlayer(), Source.FISHING_EPIC, Ability.FISHER));
											}
										}
									}
									// If has command
									else if (loot.hasCommand()) {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LoreUtil.replace(loot.getCommand(), "{player}", player.getName()));
									}
								}
							}
						} else if (r.nextDouble() < (getValue(Ability.TREASURE_HUNTER, playerData) / 100)) {
							if (plugin.getAbilityManager().isEnabled(Ability.TREASURE_HUNTER)) {
								Item item = (Item) event.getCaught();
								int lootTableSize = plugin.getLootTableManager().getLootTable("fishing-rare").getLoot().size();
								if (lootTableSize > 0) {
									Loot loot = plugin.getLootTableManager().getLootTable("fishing-rare").getLoot().get(r.nextInt(lootTableSize));
									// If has item
									if (loot.hasItem()) {
										ItemStack drop = loot.getDrop();
										if (drop != null) {
											PlayerLootDropEvent dropEvent = new PlayerLootDropEvent(player, drop, item.getLocation(), LootDropCause.TREASURE_HUNTER);
											Bukkit.getPluginManager().callEvent(dropEvent);
											if (!event.isCancelled()) {
												item.setItemStack(dropEvent.getItemStack());
												plugin.getLeveler().addXp(event.getPlayer(), Skill.FISHING, getXp(event.getPlayer(), Source.FISHING_RARE, Ability.FISHER));
											}
										}
									}
									// If has commaand
									else if (loot.hasCommand()) {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LoreUtil.replace(loot.getCommand(), "{player}", player.getName()));
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void grappler(PlayerFishEvent event) {
		if (blockDisabled(Ability.GRAPPLER)) return;
		if (event.getCaught() != null) {
			if (!(event.getCaught() instanceof Item)) {
				Player player = event.getPlayer();
				PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
				if (playerData == null) return;
				if (blockAbility(player)) return;
				Vector vector = player.getLocation().toVector().subtract(event.getCaught().getLocation().toVector());
				event.getCaught().setVelocity(vector.multiply(0.004 + (getValue(Ability.GRAPPLER, playerData) / 25000)));
			}
		}
	}

	@EventHandler
	public void sharpHook(PlayerInteractEvent event) {
		if (OptionL.isEnabled(Skill.FISHING) && plugin.getAbilityManager().isEnabled(MAbility.SHARP_HOOK)) {
			Player player = event.getPlayer();
			if (blockAbility(player)) return;
			PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
			if (playerData == null) return;
			if (playerData.getManaAbilityLevel(MAbility.SHARP_HOOK) <= 0) {
				return;
			}
			// If left click
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				ItemStack item = event.getItem();
				if (item != null) {
					if (item.getType() == Material.FISHING_ROD) {
						// Check for player just casting rod
						for (Entity entity : player.getNearbyEntities(0.1, 0.1, 0.1)) {
							if (entity instanceof FishHook) {
								FishHook fishHook = (FishHook) entity;
								ProjectileSource source = fishHook.getShooter();
								if (fishHook.isValid() && source instanceof Player) {
									if (source.equals(player)) {
										return;
									}
								}
							}
						}
						// Check entities
						for (Entity entity : player.getNearbyEntities(33, 33 ,33)) {
							if (entity instanceof FishHook) {
								FishHook fishHook = (FishHook) entity;
								ProjectileSource source = fishHook.getShooter();
								if (fishHook.isValid() && source instanceof Player) {
									if (source.equals(player)) {
										for (Entity hooked : fishHook.getNearbyEntities(0.1, 0.1, 0.1)) {
											if (hooked instanceof LivingEntity) {
												LivingEntity livingEntity = (LivingEntity) hooked;
												if (!livingEntity.isDead() && livingEntity.isValid()) {
													int cooldown = plugin.getManaAbilityManager().getPlayerCooldown(player.getUniqueId(), MAbility.SHARP_HOOK);
													if (cooldown == 0) {
														activateSharpHook(player, playerData, livingEntity);
													} else {
														if (plugin.getManaAbilityManager().getErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK) == 0) {
															Locale locale = Lang.getLanguage(player);
															plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_READY, locale), "{cooldown}", NumberUtil.format1((double) (cooldown) / 20)));
															plugin.getManaAbilityManager().setErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK, 2);
														}
													}
													break;
												}
											}
										}
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void activateSharpHook(Player player, PlayerData playerData, LivingEntity caught) {
		Locale locale = Lang.getLanguage(player);
		ManaAbilityManager manager = plugin.getManaAbilityManager();
		if (playerData.getMana() >= plugin.getManaAbilityManager().getManaCost(MAbility.SHARP_HOOK, playerData)) {
			double damage = plugin.getManaAbilityManager().getValue(MAbility.SHARP_HOOK, playerData);
			caught.damage(damage, player);
			manager.activateAbility(player, MAbility.SHARP_HOOK, 1, new SharpHook(plugin));
		}
		else {
			if (manager.getErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK) == 0) {
				plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale).replace("{mana}", String.valueOf(manager.getManaCost(MAbility.SHARP_HOOK, playerData))));
				manager.setErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK, 2);
			}
		}
	}

}
