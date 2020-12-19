package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.XpGainEvent;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EnchantingAbilities extends AbilityProvider implements Listener {

    private final Random random = new Random();

    public EnchantingAbilities(AureliumSkills plugin) {
        super(plugin, Skill.ENCHANTING);
        enchantedStrength();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void xpConvert(XpGainEvent event) {
        if (blockDisabled(Ability.XP_CONVERT)) return;
        Player player = event.getPlayer();
        if (blockAbility(player)) return;
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill != null) {
            if (playerSkill.getAbilityLevel(Ability.XP_CONVERT) > 0 && event.getAmount() > 0) {
                player.giveExp((int) (Ability.XP_CONVERT.getValue(playerSkill) / 100 * event.getAmount()));
            }
        }
    }

    @EventHandler
    public void xpWarrior(EntityDeathEvent event) {
        if (blockDisabled(Ability.XP_WARRIOR)) return;
        LivingEntity entity = event.getEntity();
        if (entity.getKiller() != null) {
            Player player = entity.getKiller();
            if (blockAbility(player)) return;
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                if (playerSkill.getAbilityLevel(Ability.XP_WARRIOR) > 0 && event.getDroppedExp() > 0) {
                    if (random.nextDouble() < Ability.XP_WARRIOR.getValue(playerSkill) / 100) {
                        event.setDroppedExp(event.getDroppedExp() * 2);
                    }
                }
            }
        }
    }

    private void enchantedStrength() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blockDisabled(Ability.ENCHANTED_STRENGTH)) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
                    PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                    if (playerStat != null && playerSkill != null) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getEnchantments().size() > 0) {
                            if (!blockAbility(player)) {
                                // Apply modifier
                                double strengthPerType = Ability.ENCHANTED_STRENGTH.getValue(playerSkill);
                                StatModifier modifier = new StatModifier("AbilityModifier-EnchantedStrength", Stat.STRENGTH, strengthPerType * item.getEnchantments().size());
                                playerStat.addModifier(modifier, false);
                            }
                        } else {
                            playerStat.removeModifier("AbilityModifier-EnchantedStrength");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 10L);
    }

}
