package dev.aurelium.auraskills.bukkit.skills.fishing;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Locale;

public class SharpHook extends ManaAbilityProvider {

    public SharpHook(AuraSkills plugin) {
        super(plugin, ManaAbilities.SHARP_HOOK, ManaAbilityMessage.SHARP_HOOK_USE, null);
    }

    @Override
    public void onActivate(Player player, User user) {
        if (manaAbility.optionBoolean("enable_sound", true)) {
            player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 1.5f);
        }
    }

    @Override
    public void onStop(Player player, User user) {

    }

    @EventHandler
    public void sharpHook(PlayerInteractEvent event) {
        if (isDisabled()) return;
        // If left click with fishing rod
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FISHING_ROD) return;

        if (shouldIgnoreItem(item)) return;

        Player player = event.getPlayer();
        if (failsChecks(player)) return;

        User user = plugin.getUser(player);

        // Check for player just casting rod
        for (Entity entity : player.getNearbyEntities(0.1, 0.1, 0.1)) {
            if (entity instanceof FishHook fishHook) {
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
            if (!(entity instanceof FishHook fishHook)) continue;
            ProjectileSource source = fishHook.getShooter();
            if (!fishHook.isValid() || !(source instanceof Player)) {
                continue;
            }
            if (!source.equals(player)) continue;

            for (Entity hooked : fishHook.getNearbyEntities(0.1, 0.1, 0.1)) {
                if (!(hooked instanceof LivingEntity livingEntity)) {
                    continue;
                }
                if (livingEntity.isDead() || !livingEntity.isValid()) {
                    continue;
                }
                ManaAbilityData data = user.getManaAbilityData(manaAbility);
                int cooldown = data.getCooldown();
                if (cooldown == 0) {
                    if (areValidLocations(player, livingEntity)) { // Check that the locations of the entities are valid
                        activateSharpHook(player, user, livingEntity);
                    }
                } else {
                    if (data.getErrorTimer() == 0) {
                        Locale locale = user.getLocale();
                        plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(ManaAbilityMessage.NOT_READY, locale), "{cooldown}", NumberUtil.format1((double) (cooldown) / 20)));
                        data.setErrorTimer(2);
                    }
                }
                break;
            }
            break;
        }
    }

    private void activateSharpHook(Player player, User user, LivingEntity caught) {
        if (insufficientMana(user, getManaCost(user))) return;

        double damage = manaAbility.getValue(user.getManaAbilityLevel(manaAbility));
        double healthBefore = caught.getHealth();
        caught.damage(damage, player);
        double healthAfter = caught.getHealth();

        // Check that entity was damaged
        if (!manaAbility.optionBoolean("disable_health_check", false) && healthBefore == healthAfter) {
            return;
        }

        checkActivation(player);
    }

    private boolean areValidLocations(Player damager, LivingEntity hooked) {
        Location damagerLocation = damager.getLocation();
        Location hookedLocation = hooked.getLocation();
        // Disallow if in different worlds
        World damagerWorld = damagerLocation.getWorld();
        World hookedWorld = hookedLocation.getWorld();
        if (damagerWorld != null && hookedWorld != null) {
            if (!damagerWorld.equals(hookedWorld)) {
                return false;
            }
        }
        // Disallow if more than 33 blocks away
        return !(damagerLocation.distanceSquared(hookedLocation) > 1089);
    }

    @Override
    protected int getDuration(User user) {
        return 0;
    }

}
