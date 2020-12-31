package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.AbilityMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import com.archyx.aureliumskills.util.VersionUtils;
import com.cryptomorin.xseries.ReflectionUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.Locale;

public class HealingAbilities extends AbilityProvider implements Listener {

    private Class<?> entityLivingClass;
    private Class<?> craftPlayerClass;

    public HealingAbilities(AureliumSkills plugin) {
        super(plugin, Skill.HEALING);
    }

    @EventHandler
    public void lifeEssence(EntityRegainHealthEvent event) {
        if (blockDisabled(Ability.LIFE_ESSENCE)) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (blockAbility(player)) return;
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC) {
                PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                if (playerSkill == null) return;
                if (playerSkill.getAbilityLevel(Ability.LIFE_ESSENCE) > 0) {
                    double multiplier = 1 + getValue(Ability.LIFE_ESSENCE, playerSkill) / 100;
                    event.setAmount(event.getAmount() * multiplier);
                }
            }
        }
    }

    @EventHandler
    public void lifeSteal(EntityDeathEvent event) {
        if (blockDisabled(Ability.LIFE_STEAL)) return;
        LivingEntity entity = event.getEntity();
        if (entity instanceof Monster || entity instanceof Player || entity instanceof Phantom) {
            if (entity.getKiller() == null) return;
            Player player = entity.getKiller();
            if (blockAbility(player)) return;
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill == null) return;
            if (playerSkill.getAbilityLevel(Ability.LIFE_STEAL) > 0) {
                AttributeInstance entityAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (entityAttribute == null) return;
                double maxHealth = entityAttribute.getValue();
                double percent = getValue(Ability.LIFE_STEAL, playerSkill) / 100;
                double healthRegen = maxHealth * percent;
                AttributeInstance playerAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (playerAttribute == null) return;
                player.setHealth(player.getHealth() + Math.min(healthRegen, playerAttribute.getValue() - player.getHealth()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void goldenHeart(EntityDamageEvent event) {
        if (blockDisabled(Ability.GOLDEN_HEART)) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (blockAbility(player)) return;
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill == null) return;
            if (getAbsorptionAmount(player) > 0 && playerSkill.getAbilityLevel(Ability.GOLDEN_HEART) > 0) {
                double multiplier = 1 - getValue(Ability.GOLDEN_HEART, playerSkill) / 100;
                if (multiplier < 0.01) { // Cap at 99% reduction
                    multiplier = 0.01;
                }
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }

    @EventHandler
    public void revival(PlayerRespawnEvent event) {
        if (blockDisabled(Ability.REVIVAL)) return;
        Player player = event.getPlayer();
        if (blockAbility(player)) return;
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
        if (playerSkill == null || playerStat == null) return;
        if (playerSkill.getAbilityLevel(Ability.REVIVAL) > 0) {
            double healthBonus = getValue(Ability.REVIVAL, playerSkill);
            double regenerationBonus = getValue2(Ability.REVIVAL, playerSkill);
            StatModifier healthModifier = new StatModifier("AureliumSkills.Ability.Revival.Health", Stat.HEALTH, healthBonus);
            StatModifier regenerationModifier = new StatModifier("AureliumSkills.Ability.Revival.Regeneration", Stat.REGENERATION, regenerationBonus);
            playerStat.addModifier(healthModifier);
            playerStat.addModifier(regenerationModifier);
            if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.REVIVAL, "enable_message")) {
                Locale locale = Lang.getLanguage(player);
                player.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(AbilityMessage.REVIVAL_MESSAGE, locale)
                        , "{value}", NumberUtil.format1(healthBonus)
                        , "{value_2}", NumberUtil.format1(regenerationBonus)));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerStat.removeModifier("AureliumSkills.Ability.Revival.Health");
                    playerStat.removeModifier("AureliumSkills.Ability.Revival.Regeneration");
                }
            }.runTaskLater(plugin, 30 * 20);
        }
    }

    private double getAbsorptionAmount(Player player) {
        if (VersionUtils.isAboveVersion(14)) {
            return player.getAbsorptionAmount();
        } else {
            if (entityLivingClass == null) {
                entityLivingClass = ReflectionUtils.getNMSClass("EntityLiving");
            }
            if (craftPlayerClass == null) {
                craftPlayerClass = ReflectionUtils.getCraftClass("entity.CraftPlayer");
            }
            if (craftPlayerClass != null && entityLivingClass != null) {
                try {
                    Method getHandle = craftPlayerClass.getDeclaredMethod("getHandle");
                    Object o = entityLivingClass.cast(getHandle.invoke(craftPlayerClass.cast(player)));
                    return (float) entityLivingClass.getDeclaredMethod("getAbsorptionHearts").invoke(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0.0;
    }


}
