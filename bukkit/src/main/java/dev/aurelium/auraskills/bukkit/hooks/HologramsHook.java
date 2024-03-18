package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.util.text.TextUtil;
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
import org.spongepowered.configurate.ConfigurationNode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public abstract class HologramsHook extends Hook implements Listener {

    private final Random random = new Random();
    private NumberFormat numberFormat;
    private ChatColor defaultColor = ChatColor.GRAY;
    private List<ChatColor> criticalColors = new ArrayList<>();

    public HologramsHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);
        loadConfig();
    }

    public void loadConfig() {
        defaultColor = ChatColor.valueOf(plugin.configString(Option.DAMAGE_HOLOGRAMS_COLORS_DEFAULT).toUpperCase(Locale.ROOT));
        criticalColors = plugin.configStringList(Option.DAMAGE_HOLOGRAMS_COLORS_CRITICAL_DIGITS).stream()
                .map(s -> ChatColor.valueOf(s.toUpperCase(Locale.ROOT)))
                .toList();
        numberFormat = new DecimalFormat("#." + TextUtil.repeat('#', plugin.configInt(Option.DAMAGE_HOLOGRAMS_DECIMAL_MAX)));
    }

    public abstract void createHologram(Location location, String text);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (!(plugin.getHookManager().isRegistered(HologramsHook.class))) return;
        if (!plugin.configBoolean(Option.DAMAGE_HOLOGRAMS_ENABLED)) return;

        if (plugin.getWorldManager().isDisabledWorld(event.getEntity().getWorld().getName())) return;

        Player player;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player) {
            player = (Player) projectile.getShooter();
            if (player.equals(event.getEntity())) { // Don't display self damage
                return;
            }
            if (event.getFinalDamage() <= 0) return; // Don't display 0 damage
        } else {
            return;
        }

        boolean critical = player.hasMetadata("skillsCritical");

        createHologram(getLocation(event.getEntity()), getText(event.getFinalDamage(), critical));
    }

    private Location getLocation(Entity entity) {
        Location location = entity.getLocation();
        if (plugin.configBoolean(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_ENABLED)) {
            //Calculate random holograms
            double xMin = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MIN);
            double xMax = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_X_MAX);
            double x = xMin + (xMax - xMin) * random.nextDouble();
            double yMin = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MIN);
            double yMax = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Y_MAX);
            double y = yMin + (yMax - yMin) * random.nextDouble();
            double zMin = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MIN);
            double zMax = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_RANDOM_Z_MAX);
            double z = zMin + (zMax - zMin) * random.nextDouble();
            location.add(x, (entity.getHeight() - entity.getHeight() * 0.1) + y, z);
        } else {
            double x = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_X);
            double y = (entity.getHeight() - entity.getHeight() * 0.1) + plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_Y);
            double z = plugin.configDouble(Option.DAMAGE_HOLOGRAMS_OFFSET_Z);
            location.add(x, y, z);
        }
        return location;
    }

    private String getText(double damage, boolean critical) {
        damage = Math.max(0.0, damage); // Prevent negative values
        StringBuilder text = new StringBuilder(defaultColor + "");
        String damageText;
        if (plugin.configBoolean(Option.DAMAGE_HOLOGRAMS_SCALING)) {
            double damageScaling = damage * Traits.HP.optionDouble("action_bar_scaling");
            if (damageScaling < plugin.configDouble(Option.DAMAGE_HOLOGRAMS_DECIMAL_LESS_THAN)) {
                damageText = numberFormat.format(damageScaling);
            } else {
                damageText = "" + Math.round(damageScaling);
            }
        } else {
            if (damage < plugin.configDouble(Option.DAMAGE_HOLOGRAMS_DECIMAL_LESS_THAN)) {
                damageText = numberFormat.format(damage);
            } else {
                damageText = "" + Math.round(damage);
            }
        }
        if (critical) {
            text.append(getCriticalText(damageText));
        } else {
            text.append(damageText);
        }
        return text.toString();
    }

    private String getCriticalText(String damageText) {
        StringBuilder text = new StringBuilder(defaultColor + "");
        for (int i = 0; i < damageText.length(); i++) {
            // Calculate the reverse index of the digit
            int j = Math.abs(i - (damageText.length() - 1));

            for (int k = 0; k < criticalColors.size(); k++) {
                if (k == j) {
                    text.append(criticalColors.get(k)).append(damageText.charAt(i));
                    break;
                } else if (k == criticalColors.size() - 1) {
                    text.append(criticalColors.get(k)).append(damageText.charAt(i));
                }
            }
        }
        return text.toString();
    }

}
