package com.archyx.aureliumskills.util;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class HologramSupport implements Listener {

    private final Plugin plugin;
    private final Random r = new Random();
    private NumberFormat nf;

    public HologramSupport(Plugin plugin) {
        this.plugin = plugin;
        nf = new DecimalFormat("#." + StringUtils.repeat("#", Options.hologramsDecimalAmount));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity() instanceof LivingEntity) {
                if (AureliumSkills.holographicDisplaysEnabled) {
                    if (Options.getBooleanOption(Setting.DAMAGE_HOLOGRAMS)) {
                        if (AureliumSkills.worldManager.isInDisabledWorld(event.getEntity().getLocation())) {
                            return;
                        }
                        if (event.getDamager() instanceof Player) {
                            Player player = (Player) event.getDamager();
                            if (player.hasMetadata("skillsCritical")) {
                                //If only critical
                                createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), true));
                            } else {
                                //If none
                                createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), false));

                            }
                        } else if (event.getDamager() instanceof Arrow) {
                            Arrow arrow = (Arrow) event.getDamager();
                            if (arrow.getShooter() instanceof Player) {
                                Player player = (Player) arrow.getShooter();
                                if (player.hasMetadata("skillsCritical")) {
                                    createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), true));
                                } else {
                                    createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), false));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Location getLocation(Entity entity) {
        Location location = entity.getLocation();
        if (Options.getBooleanOption(Setting.DAMAGE_HOLOGRAMS_RANDOM_ENABLED)) {
            //Calculate random holograms
            double xMin = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_RANDOM_X_MIN);
            double xMax = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_RANDOM_X_MAX);
            double x = xMin + (xMax - xMin) * r.nextDouble();
            double yMin = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_RANDOM_Y_MIN);
            double yMax = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_RANDOM_Y_MAX);
            double y = yMin + (yMax - yMin) * r.nextDouble();
            double zMin = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_RANDOM_Z_MIN);
            double zMax = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_RANDOM_Z_MAX);
            double z = zMin + (zMax - zMin) * r.nextDouble();
            location.add(x, (entity.getHeight() - entity.getHeight() * 0.1) + y, z);
        }
        else {
            double x = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_OFFSET_X);
            double y = (entity.getHeight() - entity.getHeight() * 0.1) + Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_OFFSET_Y);
            double z = Options.getDoubleOption(Setting.DAMAGE_HOLOGRAMS_OFFSET_Z);
            location.add(x, y, z);
        }
        return location;
    }

    private String getText(double damage, boolean critical) {
        StringBuilder text = new StringBuilder(ChatColor.GRAY + "");
        String damageText;
        if (Options.getBooleanOption(Setting.DAMAGE_HOLOGRAMS_SCALING)) {
            double damageScaling = damage * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING);
            if (damageScaling < Options.hologramsDisplayLessThan) {
                damageText = nf.format(damageScaling);
            }
            else {
                damageText = "" + (int) (damageScaling);
            }
        }
        else {
            if (damage < Options.hologramsDisplayLessThan) {
                damageText = nf.format(damage);
            }
            else {
                damageText = "" + (int) damage;
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
            if (j == 0) {
                text.append(ChatColor.GRAY).append(damageText.charAt(i));
            } else if (j == 1) {
                text.append(ChatColor.WHITE).append(damageText.charAt(i));
            } else if (j == 2) {
                text.append(ChatColor.YELLOW).append(damageText.charAt(i));
            } else if (j == 3) {
                text.append(ChatColor.GOLD).append(damageText.charAt(i));
            } else if (j == 4) {
                text.append(ChatColor.RED).append(damageText.charAt(i));
            } else if (j == 5) {
                text.append(ChatColor.DARK_RED).append(damageText.charAt(i));
            } else if (j == 6) {
                text.append(ChatColor.DARK_PURPLE).append(damageText.charAt(i));
            } else if (j == 7) {
                text.append(ChatColor.LIGHT_PURPLE).append(damageText.charAt(i));
            } else if (j == 8) {
                text.append(ChatColor.BLUE).append(damageText.charAt(i));
            } else {
                text.append(ChatColor.DARK_BLUE).append(damageText.charAt(i));
            }
        }
        return text.toString();
    }

    private void createHologram(Location location, String text) {
        Hologram hologram = HologramsAPI.createHologram(plugin, location);
        hologram.appendTextLine(text);
        new BukkitRunnable() {
            @Override
            public void run() {
                hologram.delete();
            }
        }.runTaskLater(plugin, 30L);
    }
}
