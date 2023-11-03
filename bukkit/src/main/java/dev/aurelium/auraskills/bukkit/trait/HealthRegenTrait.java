package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.event.trait.CustomRegenEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.concurrent.TimeUnit;

public class HealthRegenTrait extends TraitImpl {

    public HealthRegenTrait(AuraSkills plugin) {
        super(plugin, Traits.HUNGER_REGEN, Traits.SATURATION_REGEN);
        startHungerRegen();
        startSaturationRegen();
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            return;
        }
        // Check for disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        User user = plugin.getUser(player);
        if (player.getSaturation() > 0) {
            if (!Traits.SATURATION_REGEN.isEnabled()) return;

            if (Traits.SATURATION_REGEN.optionBoolean("use_custom_delay", false)) {
                event.setCancelled(true);
                return;
            }

            event.setAmount(event.getAmount() + user.getBonusTraitLevel(Traits.SATURATION_REGEN));
        } else if (player.getFoodLevel() >= 14) {
            if (!Traits.HUNGER_REGEN.isEnabled()) return;

            if (Traits.HUNGER_REGEN.optionBoolean("use_custom_delay", false)) {
                event.setCancelled(true);
                return;
            }

            event.setAmount(event.getAmount() + user.getBonusTraitLevel(Traits.HUNGER_REGEN));
        }
    }

    public void startHungerRegen() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!Traits.HUNGER_REGEN.optionBoolean("use_custom_delay")) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    User user = plugin.getUser(player);

                    if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) continue;

                    if (player.isDead()) continue;

                    AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (attribute == null) continue;

                    if (!(player.getHealth() < attribute.getValue())) continue;

                    if (player.getFoodLevel() >= 14 && player.getFoodLevel() < 20) {
                        double amountGained = Math.min(Traits.HUNGER_REGEN.optionDouble("base") + user.getBonusTraitLevel(Traits.HUNGER_REGEN)
                                , attribute.getValue() - player.getHealth());
                        CustomRegenEvent event = new CustomRegenEvent(player, user.toApi(), amountGained, CustomRegenEvent.Reason.HUNGER);
                        Bukkit.getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            player.setHealth(player.getHealth() + amountGained);
                            if (player.getFoodLevel() - 1 >= 0) {
                                player.setFoodLevel(player.getFoodLevel() - 1);
                            }
                        }
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, Traits.HUNGER_REGEN.optionInt("delay") * 50L, TimeUnit.MILLISECONDS);
    }

    public void startSaturationRegen() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!Traits.SATURATION_REGEN.optionBoolean("use_custom_delay")) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    User user = plugin.getUser(player);

                    if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) continue;

                    if (player.isDead()) continue;

                    AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (attribute == null) continue;

                    if (!(player.getHealth() < attribute.getValue())) continue;

                    if (player.getSaturation() >= 0 && player.getFoodLevel() >= 20) {
                        double amountGained = Math.min(Traits.SATURATION_REGEN.optionDouble("base") + user.getBonusTraitLevel(Traits.SATURATION_REGEN)
                                , attribute.getValue() - player.getHealth());
                        CustomRegenEvent event = new CustomRegenEvent(player, user.toApi(), amountGained, CustomRegenEvent.Reason.SATURATION);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            player.setHealth(player.getHealth() + amountGained);
                            if (player.getFoodLevel() - 1 >= 0) {
                                player.setFoodLevel(player.getFoodLevel() - 1);
                            }
                        }
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, Traits.SATURATION_REGEN.optionInt("delay") * 50L, TimeUnit.MILLISECONDS);
    }

}
