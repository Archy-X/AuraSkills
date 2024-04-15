package dev.aurelium.auraskills.api.damage;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DamageMeta {

    private final DamageType damageType;
    private final EntityDamageEvent.DamageCause damageCause;
    private final List<DamageModifier> attackModifiers = new ArrayList<>();
    private final List<DamageModifier> defenseModifiers = new ArrayList<>();
    private final Entity attacker;
    private final Entity target;
    private final String source;
    private double damage;

    public DamageMeta(@Nullable Entity attacker, Entity target, DamageType damageType, EntityDamageEvent.DamageCause damageCause, double damage, String source) {
        this.attacker = attacker;
        this.target = target;
        this.damageType = damageType;
        this.damageCause = damageCause;
        this.damage = damage;
        this.source = source;
    }

    public double getBaseDamage() {
        return damage;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

    public List<DamageModifier> getAttackModifiers() {
        return attackModifiers;
    }

    public void addAttackModifier(DamageModifier modifier) {
        this.attackModifiers.add(modifier);
    }

    public List<DamageModifier> getDefenseModifiers() {
        return defenseModifiers;
    }

    public void addDefenseModifier(DamageModifier modifier) {
        this.defenseModifiers.add(modifier);
    }

    public DamageType getDamageType() {
        return damageType;
    }

    @Nullable
    public Entity getAttacker() {
        return attacker;
    }

    public Entity getTarget() {
        return target;
    }

    @Nullable
    public Player getAttackerAsPlayer() {
        if (attacker instanceof Player) {
            return (Player) attacker;
        }
        if (attacker instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) attacker).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }

    @Nullable
    public Player getTargetAsPlayer() {
        if (target instanceof Player) {
            return (Player) target;
        }
        return null;
    }

    @Nullable
    public SkillsUser getAttackerAsUser() {
        Player player = getAttackerAsPlayer();
        if (player != null) {
            return AuraSkillsApi.get().getUser(player.getUniqueId());
        }
        return null;
    }

    @Nullable
    public SkillsUser getTargetAsUser() {
        Player player = getTargetAsPlayer();
        if (player != null) {
            return AuraSkillsApi.get().getUser(player.getUniqueId());
        }
        return null;
    }

    public void clearAttackModifiers() {
        this.attackModifiers.clear();
    }

    public void clearDefenseModifiers() {
        this.defenseModifiers.clear();
    }

    public String getSource() {
        return source;
    }
}
