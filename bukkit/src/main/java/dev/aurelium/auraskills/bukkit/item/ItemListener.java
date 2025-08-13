package dev.aurelium.auraskills.bukkit.item;

import com.google.common.collect.Sets;
import dev.aurelium.auraskills.api.stat.ReloadableIdentifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ItemListener implements Listener {

    private final AuraSkills plugin;
    private final ItemStateManager stateManager;

    public ItemListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.stateManager = new ItemStateManager(plugin);
        scheduleTask();
    }

    public void scheduleTask() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Gets stored and held items
                    ItemStack held = player.getInventory().getItemInMainHand();

                    stateManager.changeItemInSlot(plugin.getUser(player), player, held, EquipmentSlot.HAND);
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0L, plugin.configInt(Option.MODIFIER_ITEM_CHECK_PERIOD) * 50L, TimeUnit.MILLISECONDS);
        scheduleOffHandTask();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (!plugin.configBoolean(Option.MODIFIER_ITEM_ENABLE_OFF_HAND)) {
            return;
        }

        Player player = event.getPlayer();
        User user = plugin.getUser(player);
        Set<ReloadableIdentifier> toReload = Sets.newConcurrentHashSet();

        // Get items switched
        ItemStack itemOffHand = event.getOffHandItem();
        ItemStack itemMainHand = event.getMainHandItem();

        // Unload all existing hand effects for a short time to prevent overriding effects
        toReload.addAll(stateManager.changeItemInSlot(user, player, new ItemStack(Material.AIR), EquipmentSlot.OFF_HAND, false, false, false));
        toReload.addAll(stateManager.changeItemInSlot(user, player, new ItemStack(Material.AIR), EquipmentSlot.HAND, false, false, false));

        if (itemOffHand == null) {
            itemOffHand = new ItemStack(Material.AIR);
        }

        if (itemMainHand == null) {
            itemMainHand = new ItemStack(Material.AIR);
        }

        // Now that all effects are unloaded, we can reload the items which will apply the new effects without overriding each other

        toReload.addAll(stateManager.changeItemInSlot(user, player, itemOffHand, EquipmentSlot.OFF_HAND, false, false, false));
        toReload.addAll(stateManager.changeItemInSlot(user, player, itemMainHand, EquipmentSlot.HAND, false, false, false));

        stateManager.reloadIdentifiers(user, toReload);
    }

    public void scheduleOffHandTask() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!plugin.configBoolean(Option.MODIFIER_ITEM_ENABLE_OFF_HAND)) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Gets stored and held items
                    ItemStack held = player.getInventory().getItemInOffHand();

                    stateManager.changeItemInSlot(plugin.getUser(player), player, held, EquipmentSlot.OFF_HAND);
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0L, plugin.configInt(Option.MODIFIER_ITEM_CHECK_PERIOD) * 50L, TimeUnit.MILLISECONDS);
    }

}
