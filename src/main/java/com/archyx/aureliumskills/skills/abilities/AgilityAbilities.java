package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Random;

public class AgilityAbilities implements Listener {

    private final Random r = new Random();
    private final Plugin plugin;

    public AgilityAbilities(Plugin plugin) {
        this.plugin = plugin;
    }

    public static double getModifiedXp(Player player, Source source) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = OptionL.getXp(source);
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.JUMPER)) {
            double modifier = 1;
            modifier += Ability.JUMPER.getValue(skill.getAbilityLevel(Ability.JUMPER)) / 100;
            output *= modifier;
        }
        return output;
    }

    @EventHandler
    public void lightFall(EntityDamageEvent event) {
        // If from fall damage
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntity() instanceof Player) {
                if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LIGHT_FALL) && OptionL.isEnabled(Skill.AGILITY)) {
                    Player player = (Player) event.getEntity();
                    PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                    if (playerSkill != null) {
                        if (event.getFinalDamage() > 0.0) {
                            double percentReduction = Ability.LIGHT_FALL.getValue(playerSkill.getAbilityLevel(Ability.LIGHT_FALL));
                            event.setDamage(event.getDamage() * (1 - (percentReduction / 100)));
                        }
                    }
                }
            }
        }
    }

    // For potion splashes
    @EventHandler
    public void sugarRush(PotionSplashEvent event) {

    }

    // For potion drinking
    @EventHandler
    public void sugarRushDrink(PlayerItemConsumeEvent event) {

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void fleeting(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            AttributeInstance attribute = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH));
            double maxHealth = attribute.getValue();
            // If less than 20% health
            if (player.getHealth() - event.getFinalDamage() < 0.2 * maxHealth) {

            }
        }
    }

    @EventHandler
    public void thunderFall(EntityDamageEvent event) {

    }

    @EventHandler
    public void agilityListener(EntityDamageEvent event) {
        if (OptionL.isEnabled(Skill.AGILITY)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LIGHT_FALL)) {
                // If from fall damage
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (event.getEntity() instanceof Player) {
                        Player player = (Player) event.getEntity();
                        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                        if (playerSkill != null) {
                            if (event.getFinalDamage() > 0.0) {

                            }
                        }
                    }
                }
            }
        }
    }

}
