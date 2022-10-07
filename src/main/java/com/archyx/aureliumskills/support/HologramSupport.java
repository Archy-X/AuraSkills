package com.archyx.aureliumskills.support;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class HologramSupport implements Listener {

    private final AureliumSkills plugin;
    private final Random random = new Random();
    private final NumberFormat numberFormat;

    private DecentHologramsSupport decentHologramsSupport;
    private HolographicDisplaysSupport holographicDisplaysSupport;

    public HologramSupport(AureliumSkills plugin) {
        this.plugin = plugin;
        numberFormat = new DecimalFormat("#." + TextUtil.repeat("#", OptionL.getInt(Option.DAMAGE_HOLOGRAMS_DECIMAL_MAX)));
        if (plugin.isDecentHologramsEnabled()) {
            decentHologramsSupport = new DecentHologramsSupport(plugin);
        } else if (plugin.isHolographicDisplaysEnabled()) {
            holographicDisplaysSupport = new HolographicDisplaysSupport(plugin);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (!(plugin.isHolographicDisplaysEnabled() || plugin.isDecentHologramsEnabled())) return;
        if (!OptionL.getBoolean(Option.DAMAGE_HOLOGRAMS)) return;
        if (plugin.getWorldManager().isInDisabledWorld(event.getEntity().getLocation())) return;
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.hasMetadata("skillsCritical")) {
                //If only critical
                createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), true));
            } else {
                //If none
                createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), false));
            }
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                Player player = (Player) projectile.getShooter();
                if (player.equals(event.getEntity())) { // Don't display self damage
                    return;
                }
                if (event.getFinalDamage() <= 0) return; // Don't display 0 damage
                if (player.hasMetadata("skillsCritical")) {
                    createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), true));
                } else {
                    createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), false));
                }
            }
        }
    }

    private Location getLocation(Entity entity) {
        Location location = entity.getLocation();
        if (OptionL.getBoolean(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_ENABLED)) {
            //Calculate random holograms
            double xMin = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MIN);
            double xMax = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MAX);
            double x = xMin + (xMax - xMin) * random.nextDouble();
            double yMin = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MIN);
            double yMax = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MAX);
            double y = yMin + (yMax - yMin) * random.nextDouble();
            double zMin = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MIN);
            double zMax = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MAX);
            double z = zMin + (zMax - zMin) * random.nextDouble();
            location.add(x, (entity.getHeight() - entity.getHeight() * 0.1) + y, z);
        }
        else {
            double x = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_X);
            double y = (entity.getHeight() - entity.getHeight() * 0.1) + OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_Y);
            double z = OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_Z);
            location.add(x, y, z);
        }
        return location;
    }

    private String getText(double damage, boolean critical) {
        StringBuilder text = new StringBuilder(ChatColor.GRAY + "");
        String damageText;
        if (OptionL.getBoolean(Option.DAMAGE_HOLOGRAMS_SCALING)) {
            double damageScaling = damage * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
            if (damageScaling < OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_DECIMAL_LESS_THAN)) {
                damageText = numberFormat.format(damageScaling);
            }
            else {
                damageText = "" + Math.round(damageScaling);
            }
        }
        else {
            if (damage < OptionL.getDouble(Option.DAMAGE_HOLOGRAMS_DECIMAL_LESS_THAN)) {
                damageText = numberFormat.format(damage);
            }
            else {
                damageText = "" + Math.round(damage);
            }
        }
        if (critical) {
            text.append(getCriticalText(damageText));
        }
        else {
            text.append(damageText);
        }
        return text.toString();
    }

    private String getCriticalText(String damageText) {
        StringBuilder text = new StringBuilder(ChatColor.GRAY + "");
        for (int i = 0; i < damageText.length(); i++) {
            int j = Math.abs(i - (damageText.length() - 1));
            switch (j) {
                case 0:
                    text.append(ChatColor.GRAY).append(damageText.charAt(i));
                    break;
                case 1:
                    text.append(ChatColor.WHITE).append(damageText.charAt(i));
                    break;
                case 2:
                    text.append(ChatColor.YELLOW).append(damageText.charAt(i));
                    break;
                case 3:
                    text.append(ChatColor.GOLD).append(damageText.charAt(i));
                    break;
                case 4:
                    text.append(ChatColor.RED).append(damageText.charAt(i));
                    break;
                case 5:
                    text.append(ChatColor.DARK_RED).append(damageText.charAt(i));
                    break;
                case 6:
                    text.append(ChatColor.DARK_PURPLE).append(damageText.charAt(i));
                    break;
                case 7:
                    text.append(ChatColor.LIGHT_PURPLE).append(damageText.charAt(i));
                    break;
                case 8:
                    text.append(ChatColor.BLUE).append(damageText.charAt(i));
                    break;
                default:
                    text.append(ChatColor.DARK_BLUE).append(damageText.charAt(i));
                    break;
            }
        }
        return text.toString();
    }

    private void createHologram(Location location, String text) {
        if (plugin.isHolographicDisplaysEnabled() && holographicDisplaysSupport != null) {
            holographicDisplaysSupport.createHologram(location, text);
        }
        if (plugin.isDecentHologramsEnabled() && decentHologramsSupport != null) {
            decentHologramsSupport.createHologram(location, text);
        }
    }
}
