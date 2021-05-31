package com.archyx.aureliumskills.loot.type;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockItemLoot extends ItemLoot {

    public BlockItemLoot(AureliumSkills plugin, int weight, String message, ItemStack item, int minAmount, int maxAmount) {
        super(plugin, weight, message, item, minAmount, maxAmount);
    }

    public void giveLoot(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack drop = item.clone();
        drop.setAmount(generateAmount());
        block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), drop);
        attemptSendMessage(event.getPlayer());
    }

}
