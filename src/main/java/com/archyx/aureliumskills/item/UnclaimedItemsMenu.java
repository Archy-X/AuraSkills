package com.archyx.aureliumskills.item;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.misc.KeyIntPair;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UnclaimedItemsMenu implements InventoryProvider {

    private final AureliumSkills plugin;
    private final PlayerData playerData;

    public UnclaimedItemsMenu(AureliumSkills plugin, PlayerData playerData) {
        this.plugin = plugin;
        this.playerData = playerData;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        int row = 0;
        int column = 0;
        for (KeyIntPair keyIntPair : playerData.getUnclaimedItems()) {
            String itemKey = keyIntPair.getKey();
            int amount = keyIntPair.getValue();
            ItemStack item = plugin.getItemRegistry().getItem(itemKey);
            if (item == null) {
                plugin.getLogger().warning("Could not find a registered item with key " + itemKey + " when claiming unclaimed item rewards");
                continue;
            }
            item.setAmount(amount);
            contents.set(row, column, ClickableItem.of(getDisplayItem(item), event -> {
                // Give item on click
                if (ItemUtils.canAddItemToInventory(player, item)) {
                    ItemUtils.addItemToInventory(player, item);
                    playerData.getUnclaimedItems().remove(keyIntPair);
                    if (playerData.getUnclaimedItems().size() > 0) {
                        init(player, contents); // Refresh inventory
                    } else {
                        player.closeInventory();
                    }
                } else {
                    player.sendMessage(Lang.getMessage(MenuMessage.INVENTORY_FULL, playerData.getLocale()));
                    player.closeInventory();
                }
            }));
            // Increment slot position
            if (column < 8) {
                column++;
            } else {
                row++;
                column = 0;
            }
        }
    }

    public static SmartInventory getInventory(AureliumSkills plugin, PlayerData playerData) {
        return SmartInventory.builder()
                .manager(plugin.getInventoryManager())
                .provider(new UnclaimedItemsMenu(plugin, playerData))
                .size(6, 9)
                .title(Lang.getMessage(MenuMessage.UNCLAIMED_ITEMS_TITLE, playerData.getLocale()))
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
            lore.add(Lang.getMessage(MenuMessage.CLICK_TO_CLAIM, playerData.getLocale()));
            meta.setLore(lore);
        }
        displayItem.setItemMeta(meta);
        return displayItem;
    }

}
