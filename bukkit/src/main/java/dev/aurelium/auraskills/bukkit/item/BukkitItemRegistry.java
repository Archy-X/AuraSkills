package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.item.ItemRegistry;
import dev.aurelium.auraskills.common.message.type.LevelerMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BukkitItemRegistry implements ItemRegistry {

    private final AuraSkills plugin;
    private final Map<NamespacedId, ItemStack> items = new HashMap<>();
    private final BukkitSourceMenuItems sourceMenuItems;

    public BukkitItemRegistry(AuraSkills plugin) {
        this.plugin = plugin;
        this.sourceMenuItems = new BukkitSourceMenuItems(plugin);
    }

    public void register(NamespacedId key, ItemStack item) {
        items.put(key, item.clone());
    }

    public void unregister(NamespacedId key) {
        items.remove(key);
    }

    @Nullable
    public ItemStack getItem(NamespacedId key) {
        ItemStack item = items.get(key);
        if (item != null) {
            return item.clone();
        } else {
            return null;
        }
    }

    @Override
    public boolean containsItem(NamespacedId key) {
        return items.containsKey(key);
    }

    @Override
    public void giveItem(User user, NamespacedId key, int amount) {
        Player player = ((BukkitUser) user).getPlayer();
        ItemStack item = getItem(key);

        if (item == null) {
            return;
        }

        ItemStack leftoverItem = ItemUtils.addItemToInventory(player, item); // Attempt item give
        // Handle items that could not fit in the inventory
        if (leftoverItem != null) {
            // Add unclaimed item key and amount to player data
            user.getUnclaimedItems().add(new KeyIntPair(key.toString(), leftoverItem.getAmount()));
            // Notify player
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    player.sendMessage(plugin.getPrefix(user.getLocale()) + plugin.getMsg(LevelerMessage.UNCLAIMED_ITEM, user.getLocale())), 1);
        }
    }

    @Override
    public int getItemAmount(NamespacedId key) {
        ItemStack item = getItem(key);
        if (item == null) {
            return 0;
        }
        return item.getAmount();
    }

    @Override
    public @Nullable String getEffectiveItemName(NamespacedId key) {
        ItemStack item = getItem(key);
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            return meta.hasDisplayName() ? meta.getDisplayName() : meta.getLocalizedName();
        }
        return null;
    }

    public BukkitSourceMenuItems getSourceMenuItems() {
        return sourceMenuItems;
    }
}
