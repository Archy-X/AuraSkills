package com.archyx.aureliumskills.loot.listener;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.AbilityProvider;
import com.archyx.aureliumskills.commands.CommandExecutor;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.CustomMessageKey;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.LootPool;
import com.archyx.aureliumskills.loot.type.CommandLoot;
import com.archyx.aureliumskills.loot.type.ItemLoot;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LootHandler extends AbilityProvider {

    private final Random random = new Random();

    public LootHandler(AureliumSkills plugin, Skill skill) {
        super(plugin, skill);
    }

    protected void giveCommandLoot(Player player, CommandLoot loot) {
        // Apply placeholders to command
        String finalCommand = TextUtil.replace(loot.getCommand(), "{player}", player.getName());
        if (plugin.isPlaceholderAPIEnabled()) {
            finalCommand = PlaceholderAPI.setPlaceholders(player, finalCommand);
        }
        // Execute command
        CommandExecutor executor = loot.getExecutor();
        if (executor == CommandExecutor.CONSOLE) {
            Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand);
        } else if (executor == CommandExecutor.PLAYER) {
            Bukkit.dispatchCommand(player, finalCommand);
        }
        attemptSendMessage(player, loot);
    }

    protected void giveBlockItemLoot(Player player, ItemLoot loot, BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack drop = loot.getItem().clone();
        drop.setAmount(generateAmount(loot.getMinAmount(), loot.getMaxAmount()));
        block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), drop);
        attemptSendMessage(player, loot);
    }

    protected void giveFishingItemLoot(Player player, ItemLoot loot, PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Item)) return;
        Item itemEntity = (Item) event.getCaught();

        int amount = generateAmount(loot.getMinAmount(), loot.getMaxAmount());
        if (amount == 0) return;

        ItemStack drop = loot.getItem().clone();
        drop.setAmount(amount);
        itemEntity.setItemStack(drop);
        attemptSendMessage(player, loot);
    }

    protected Loot selectLoot(LootPool pool) {
        List<Loot> lootList = pool.getLoot();
        // Loot selected based on weight
        int totalWeight = 0;
        for (Loot loot : lootList) {
            totalWeight += loot.getWeight();
        }
        int selected = random.nextInt(totalWeight);
        int currentWeight = 0;
        Loot selectedLoot = null;
        for (Loot loot : lootList) {
            if (selected >= currentWeight && selected < currentWeight + loot.getWeight()) {
                selectedLoot = loot;
                break;
            }
            currentWeight += loot.getWeight();
        }
        return selectedLoot;
    }

    private int generateAmount(int minAmount, int maxAmount) {
        return new Random().nextInt(maxAmount - minAmount + 1) + minAmount;
    }

    private void attemptSendMessage(Player player, Loot loot) {
        String message = loot.getMessage();
        if (message != null && !message.equals("")) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData == null) return;

            Locale locale = playerData.getLocale();
            // Try to get message as message key
            CustomMessageKey key = new CustomMessageKey(message);
            String finalMessage = Lang.getMessage(key, locale);
            // Use input as message if fail
            if (finalMessage == null) {
                finalMessage = message;
            }
            // Replace placeholders
            if (plugin.isPlaceholderAPIEnabled()) {
                finalMessage = PlaceholderAPI.setPlaceholders(player, finalMessage);
            }
            player.sendMessage(finalMessage);
        }
    }

}
