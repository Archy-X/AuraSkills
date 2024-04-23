package dev.aurelium.auraskills.bukkit.skills.defense;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;

public class Absorption extends ReadiedManaAbility {

    public Absorption(AuraSkills plugin) {
        super(plugin, ManaAbilities.ABSORPTION, ManaAbilityMessage.ABSORPTION_START, ManaAbilityMessage.ABSORPTION_END,
                new String[]{"SHIELD"}, new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK});
    }

    @Override
    public void onActivate(Player player, User user) {
        user.getAbilityData(manaAbility).setData("activated", true); // Register as absorption activated
        // Play sound
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
    }

    @Override
    public void onStop(Player player, User user) {
        user.getAbilityData(manaAbility).setData("activated", false);
    }

    @EventHandler
    public void onPlayerLoad(UserLoadEvent event) {
        User user = BukkitUser.getUser(event.getUser());
        // Remove the activated ability data if the player logged out when the ability was active
        if (!isActivated(user)) {
            user.getAbilityData(manaAbility).remove("activated");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        DamageMeta meta = event.getDamageMeta();
        Player target = meta.getTargetAsPlayer();

        if (target != null) {
            User user = plugin.getUser(target);

            if (user.getAbilityData(manaAbility).getBoolean("activated") && isActivated(user)) {
                handleAbsorbedHit(event, target, user);
            } else if (checkActivation(target)) {
                handleAbsorbedHit(event, target, user);
            }
        }
    }

    private void handleAbsorbedHit(DamageEvent event, Player player, User user) {
        // Decrease mana and cancel event
        double mana = user.getMana() - event.getModifiedAttackDamage() * 2;
        if (mana <= 0) {
            return;
        }

        user.setMana(mana);
        event.setCancelled(true);
        // Particle effects and sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GUARDIAN_HURT, 1f, 1f);
        if (manaAbility.optionBoolean("enable_particles", true)) {
            spawnParticles(player.getWorld(), player.getLocation().add(0, 1, 0));
        }
    }

    private void spawnParticles(World world, Location center) {
        double radius = 1;
        double rate = Math.PI / 20;
        double limit = Math.PI * 2;
        int count = 1;

        for (double theta = 0; theta <= limit; theta += rate) {
            double x = radius * Math.cos(theta);
            double z = radius * Math.sin(theta);

            Location location = center.clone().add(x, 0, z);
            world.spawnParticle(CompatUtil.blockParticle(), location, count, new DustOptions(Color.fromRGB(255, 0, 255), 1));
        }
    }

}
