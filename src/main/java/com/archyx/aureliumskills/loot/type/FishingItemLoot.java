package com.archyx.aureliumskills.loot.type;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishingItemLoot extends ItemLoot {

    public FishingItemLoot(AureliumSkills plugin, int weight, ItemStack item, int minAmount, int maxAmount) {
        super(plugin, weight, item, minAmount, maxAmount);
    }

    public void giveLoot(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Item)) return;
        Item itemEntity = (Item) event.getCaught();

        int amount = generateAmount();
        if (amount == 0) return;

        ItemStack loot = item.clone();
        loot.setAmount(amount);
        itemEntity.setItemStack(loot);
    }

}
