package com.archyx.aureliumskills.util;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.Setting;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class HologramSupport implements Listener {

    private Plugin plugin;

    public HologramSupport(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled()) {
            if (AureliumSkills.holographicDisplaysEnabled) {
                if (Options.getBooleanOption(Setting.DAMAGE_HOLOGRAMS)) {
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

    private Location getLocation(Entity entity) {
        Location location = entity.getLocation();
        location.add(0, entity.getHeight() - entity.getHeight() * 0.1, 0);
        return location;
    }

    private String getText(double damage, boolean critical) {
        NumberFormat nf = new DecimalFormat("#,###");
        StringBuilder text = new StringBuilder(ChatColor.GRAY + "");
        String damageText = "" + (int) (damage * 5);
        if (critical) {
            for (int i = 0; i < damageText.length(); i++) {
                int j = Math.abs(i - (damageText.length() - 1));
                if (j == 0) {
                    text.append(ChatColor.GRAY).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 1) {
                    text.append(ChatColor.WHITE).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 2) {
                    text.append(ChatColor.YELLOW).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 3) {
                    text.append(ChatColor.GOLD).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 4) {
                    text.append(ChatColor.RED).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 5) {
                    text.append(ChatColor.DARK_RED).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 6) {
                    text.append(ChatColor.DARK_PURPLE).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 7) {
                    text.append(ChatColor.LIGHT_PURPLE).append(String.valueOf(damageText.charAt(i)));
                } else if (j == 8) {
                    text.append(ChatColor.BLUE).append(String.valueOf(damageText.charAt(i)));
                } else {
                    text.append(ChatColor.DARK_BLUE).append(String.valueOf(damageText.charAt(i)));
                }
            }
        }
        else {
            text.append(damageText);
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
