package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import dev.aurelium.slate.inv.ClickableItem;
import dev.aurelium.slate.inv.SmartInventory;
import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UnclaimedItemsMenu implements InventoryProvider {

    private final AuraSkills plugin;
    private final User user;

    public UnclaimedItemsMenu(AuraSkills plugin, User user) {
        this.plugin = plugin;
        this.user = user;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        for (int slot = 0; slot < 54; slot++) {
            int row = slot / 9;
            int column = slot % 9;
            if (user.getUnclaimedItems().size() <= slot) { // Empty slot
                contents.set(row, column, ClickableItem.empty(new ItemStack(Material.AIR)));
            } else { // Slot with item
                KeyIntPair keyIntPair = user.getUnclaimedItems().get(slot);

                String itemKey = keyIntPair.getKey();
                int amount = keyIntPair.getValue();
                ItemStack item = plugin.getItemRegistry().getItem(NamespacedId.fromDefault(itemKey));
                if (item == null) {
                    plugin.getLogger().warning("Could not find a registered item with key " + itemKey + " when claiming unclaimed item rewards");
                    continue;
                }
                item.setAmount(amount);
                contents.set(row, column, ClickableItem.from(getDisplayItem(item), data -> {
                    // Give item on click
                    ItemStack leftoverItem = ItemUtils.addItemToInventory(player, item);
                    if (leftoverItem == null) { // All items were added
                        user.getUnclaimedItems().remove(keyIntPair);
                        if (!user.getUnclaimedItems().isEmpty()) {
                            init(player, contents);
                        } else {
                            player.closeInventory();
                        }
                    } else if (leftoverItem.getAmount() != item.getAmount()) { // Some items could not fit
                        keyIntPair.setValue(leftoverItem.getAmount());
                        init(player, contents);
                    } else { // All items could not fit
                        player.sendMessage(ChatColor.YELLOW + plugin.getMsg(MenuMessage.INVENTORY_FULL, user.getLocale()));
                        player.closeInventory();
                    }
                }));
            }
        }
    }

    public static SmartInventory getInventory(AuraSkills plugin, User user) {
        return SmartInventory.builder()
                .manager(plugin.getInventoryManager())
                .provider(new UnclaimedItemsMenu(plugin, user))
                .size(6, 9)
                .title(plugin.getMsg(MenuMessage.UNCLAIMED_ITEMS_TITLE, user.getLocale()))
                .build();
    }

    private ItemStack getDisplayItem(ItemStack baseItem) {
        ItemStack displayItem = baseItem.clone();
        ItemMeta meta = displayItem.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            } else {
                lore.add(" ");
            }
            lore.add(ChatColor.YELLOW + plugin.getMsg(MenuMessage.CLICK_TO_CLAIM, user.getLocale()));
            meta.setLore(lore);
        }
        displayItem.setItemMeta(meta);
        return displayItem;
    }

}
