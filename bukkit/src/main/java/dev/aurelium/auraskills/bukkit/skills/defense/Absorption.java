package dev.aurelium.auraskills.bukkit.skills.defense;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import dev.aurelium.auraskills.api.event.AuraSkillsEventHandler;
import dev.aurelium.auraskills.api.event.AuraSkillsListener;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.awt.*;

public class Absorption extends ReadiedManaAbility implements AuraSkillsListener {

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

    @AuraSkillsEventHandler
    public void onPlayerLoad(UserLoadEvent event) {
        User user = BukkitUser.getUser(event.getUser());
        // Remove the activated ability data if the player logged out when the ability was active
        if (!isActivated(user)) {
            user.getAbilityData(manaAbility).remove("activated");
        }
    }

    public void handleAbsorption(EntityDamageByEntityEvent event, Player player, User user) {
        if (user.getAbilityData(manaAbility).getBoolean("activated") && isActivated(user)) {
            handleAbsorbedHit(event, player, user);
        } else if (checkActivation(player)) {
            handleAbsorbedHit(event, player, user);
        }
    }

    private void handleAbsorbedHit(EntityDamageByEntityEvent event, Player player, User user) {
        // Decrease mana and cancel event
        double mana = user.getMana() - event.getDamage() * 2;
        if (mana <= 0) {
            return;
        }

        user.setMana(mana);
        event.setCancelled(true);
        // Particle effects and sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GUARDIAN_HURT, 1f, 1f);
        if (manaAbility.optionBoolean("enable_particles", true)) {
            XParticle.circle(1, 1, 1, 20, 0, ParticleDisplay.colored(player.getLocation().add(0, 1, 0), Color.MAGENTA, 1));
        }
    }

}
