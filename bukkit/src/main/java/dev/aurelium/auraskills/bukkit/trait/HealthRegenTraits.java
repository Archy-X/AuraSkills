package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.trait.CustomRegenEvent;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class HealthRegenTraits extends TraitImpl {

    public HealthRegenTraits(AuraSkills plugin) {
        super(plugin, Traits.HUNGER_REGEN, Traits.SATURATION_REGEN);
        startRegenTasks();
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        return "+" + NumberUtil.format2(value);
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

    public void startRegenTasks() {
        startHungerRegen();
        startSaturationRegen();
    }

    private void startHungerRegen() {
        Trait trait = Traits.HUNGER_REGEN;
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!trait.isEnabled() || !trait.optionBoolean("use_custom_delay")) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handleCustomRegen(player, trait, p -> p.getFoodLevel() >= 14 && p.getFoodLevel() < 20, CustomRegenEvent.Reason.HUNGER);
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, Traits.HUNGER_REGEN.optionInt("delay", 60) * 50L, TimeUnit.MILLISECONDS);
    }

    private void startSaturationRegen() {
        Trait trait = Traits.SATURATION_REGEN;
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!trait.isEnabled() || !trait.optionBoolean("use_custom_delay")) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handleCustomRegen(player, trait, p -> p.getSaturation() >= 0 && p.getFoodLevel() >= 20, CustomRegenEvent.Reason.SATURATION);
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, Traits.SATURATION_REGEN.optionInt("delay", 20) * 50L, TimeUnit.MILLISECONDS);
    }

    private void handleCustomRegen(Player player, Trait trait, Function<Player, Boolean> regenCondition, CustomRegenEvent.Reason reason) {
        User user = plugin.getUser(player);

        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) return;

        if (player.isDead()) return;

        AttributeInstance attribute = player.getAttribute(AttributeCompat.MAX_HEALTH);
        if (attribute == null) return;

        if (!(player.getHealth() < attribute.getValue())) return;

        if (regenCondition.apply(player)) {
            double amountGained = Math.min(trait.optionDouble("base") + user.getBonusTraitLevel(trait)
                    , attribute.getValue() - player.getHealth());

            final double gainThreshold = 0.001;
            if (amountGained < gainThreshold) {
                return;
            }

            CustomRegenEvent event = new CustomRegenEvent(player, user.toApi(), amountGained, reason);
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
