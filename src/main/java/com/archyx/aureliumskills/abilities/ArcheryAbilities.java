package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.ChargedShot;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ArcheryAbilities extends AbilityProvider implements Listener {

    private final Random r = new Random();
    private final Set<Player> chargedShotEnabled;
    private final Map<Player, Integer> chargedShotToggleCooldown;

    public ArcheryAbilities(AureliumSkills plugin) {
        super(plugin, Skill.ARCHERY);
        this.chargedShotEnabled = new HashSet<>();
        this.chargedShotToggleCooldown = new HashMap<>();
        tickChargedShotCooldown();
    }

    public void bowMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
        if (OptionL.isEnabled(Skill.ARCHERY)) {
            if (plugin.getAbilityManager().isEnabled(Ability.BOW_MASTER)) {
                if (!player.hasPermission("aureliumskills.archery")) {
                    return;
                }
                if (playerSkill.getAbilityLevel(Ability.BOW_MASTER) > 0) {
                    double multiplier = 1 + (getValue(Ability.BOW_MASTER, playerSkill) / 100);
                    event.setDamage(event.getDamage() * multiplier);
                }
            }
        }
    }

    public void stun(PlayerSkill playerSkill, LivingEntity entity) {
        if (r.nextDouble() < (getValue(Ability.STUN, playerSkill) / 100)) {
            if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                AttributeInstance speed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (speed != null) {
                    //Applies stun
                    double reducedSpeed = speed.getValue() * 0.2;
                    AttributeModifier modifier = new AttributeModifier("AureliumSkills-Stun", -1 * reducedSpeed, AttributeModifier.Operation.ADD_NUMBER);
                    speed.addModifier(modifier);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            AttributeInstance newSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                            if (newSpeed != null) {
                                for (AttributeModifier attributeModifier : newSpeed.getModifiers()) {
                                    if (attributeModifier.getName().equals("AureliumSkills-Stun")) {
                                        newSpeed.removeModifier(attributeModifier);
                                    }
                                }
                            }
                        }
                    }.runTaskLater(plugin, 40L);
                }
            }
        }
    }

    @EventHandler
    public void removeStun(PlayerQuitEvent event) {
        //Removes stun on logout
        AttributeInstance speed = event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            for (AttributeModifier attributeModifier : speed.getModifiers()) {
                if (attributeModifier.getName().equals("AureliumSkills-Stun")) {
                    speed.removeModifier(attributeModifier);
                }
            }
        }
    }

    public void piercing(EntityDamageByEntityEvent event, PlayerSkill playerSkill, Player player, Arrow arrow) {
        if (r.nextDouble() < (getValue(Ability.PIERCING, playerSkill) / 100)) {
            arrow.setBounce(false);
            Vector velocity = arrow.getVelocity();
            Arrow newArrow = event.getEntity().getWorld().spawnArrow(arrow.getLocation(), velocity, (float) velocity.length(), 0.0f);
            newArrow.setShooter(player);
            newArrow.setKnockbackStrength(arrow.getKnockbackStrength());
            newArrow.setFireTicks(arrow.getFireTicks());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void archeryListener(EntityDamageByEntityEvent event) {
        if (OptionL.isEnabled(Skill.ARCHERY)) {
            if (!event.isCancelled()) {
                if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        Player player = (Player) arrow.getShooter();
                        if (blockAbility(player)) return;
                        //Applies abilities
                        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                            AbilityManager options = plugin.getAbilityManager();
                            if (options.isEnabled(Ability.STUN)) {
                                if (event.getEntity() instanceof LivingEntity) {
                                    LivingEntity entity = (LivingEntity) event.getEntity();
                                    stun(playerSkill, entity);
                                }
                            }
                            if (options.isEnabled(Ability.PIERCING)) {
                                piercing(event, playerSkill, player, arrow);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void chargedShotToggle(PlayerInteractEvent event) {
        if (blockDisabled(MAbility.CHARGED_SHOT)) return;
        Player player = event.getPlayer();
        if (blockAbility(player)) return;
        ItemStack item = event.getItem();
        if (item != null) {
            if (item.getType() == Material.BOW) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                    PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                    if (playerSkill == null) return;
                    if (playerSkill.getManaAbilityLevel(MAbility.CHARGED_SHOT) == 0) return;
                    Locale locale = Lang.getLanguage(player);
                    Integer cooldown = chargedShotToggleCooldown.get(player);
                    boolean ready = true;
                    if (cooldown != null) {
                        if (cooldown != 0) {
                            ready = false;
                        }
                    }
                    if (ready) {
                        if (!chargedShotEnabled.contains(player)) { // Toggle on
                            chargedShotEnabled.add(player);
                            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.CHARGED_SHOT_ENABLE, locale));
                        } else { // Toggle off
                            chargedShotEnabled.remove(player);
                            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.CHARGED_SHOT_DISABLE, locale));
                        }
                        chargedShotToggleCooldown.put(player, 8);
                    }
                }
            }
        }
    }

    private void tickChargedShotCooldown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Integer> entry : chargedShotToggleCooldown.entrySet()) {
                    if (entry.getValue() > 0) {
                        entry.setValue(entry.getValue() - 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 3L, 5L);
    }

    @EventHandler
    public void chargedShotActivate(EntityShootBowEvent event) {
        if (blockDisabled(MAbility.CHARGED_SHOT)) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (blockAbility(player)) return;
            if (chargedShotEnabled.contains(player)) {
                PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                if (playerSkill == null) return;
                if (playerSkill.getManaAbilityLevel(MAbility.CHARGED_SHOT) == 0) return;
                ManaAbilityManager manager = plugin.getManaAbilityManager();
                int cooldown = manager.getPlayerCooldown(player.getUniqueId(), MAbility.SHARP_HOOK);
                if (cooldown == 0) {
                    manager.activateAbility(player, MAbility.CHARGED_SHOT, (int) (manager.getCooldown(MAbility.CHARGED_SHOT, playerSkill) * 20)
                            , new ChargedShot(plugin, event.getProjectile(), event.getForce()));
                } else {
                    if (manager.getErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK) == 0) {
                        Locale locale = Lang.getLanguage(player);
                        plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_READY, locale), "{cooldown}", NumberUtil.format1((double) (cooldown) / 20)));
                        manager.setErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK, 2);
                    }
                }
            }
        }
    }

    public void applyChargedShot(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("ChargedShotMultiplier")) {
            double multiplier = event.getDamager().getMetadata("ChargedShotMultiplier").get(0).asDouble();
            event.setDamage(event.getDamage() * multiplier);
        }
    }

}
