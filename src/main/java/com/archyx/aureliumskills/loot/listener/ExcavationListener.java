package com.archyx.aureliumskills.loot.listener;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.ability.AbilityProvider;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.LootPool;
import com.archyx.aureliumskills.loot.LootTable;
import com.archyx.aureliumskills.loot.type.BlockItemLoot;
import com.archyx.aureliumskills.loot.type.CommandLoot;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.excavation.ExcavationSource;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Random;

public class ExcavationListener extends AbilityProvider implements Listener {

    private final Random random = new Random();

    public ExcavationListener(AureliumSkills plugin) {
        super(plugin, Skills.FISHING);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        if (!OptionL.isEnabled(Skills.EXCAVATION)) return;
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (ExcavationSource.getSource(block) == null) return;

        Player player = event.getPlayer();
        if (blockAbility(player)) return;

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return;

        LootTable table = plugin.getLootTableManager().getLootTable(Skills.EXCAVATION);
        if (table == null) return;
        for (LootPool pool : table.getPools()) {
            // Calculate chance for pool
            double chance = pool.getBaseChance();
            if (pool.getName().equals("rare") && plugin.getAbilityManager().isEnabled(Ability.METAL_DETECTOR)) {
                chance += (getValue(Ability.METAL_DETECTOR, playerData) / 100);
            } else if (pool.getName().equals("epic") && plugin.getAbilityManager().isEnabled(Ability.LUCKY_SPADES)) {
                chance += (getValue(Ability.LUCKY_SPADES, playerData) / 100);
            }

            if (random.nextDouble() < chance) { // Pool is selected
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

                // Give loot
                if (selectedLoot != null) {
                    if (selectedLoot instanceof BlockItemLoot) {
                        BlockItemLoot blockItemLoot = (BlockItemLoot) selectedLoot;
                        blockItemLoot.giveLoot(event);
                    } else if (selectedLoot instanceof CommandLoot) {
                        CommandLoot commandLoot = (CommandLoot) selectedLoot;
                        commandLoot.giveLoot(player);
                    }
                    break;
                }
            }
        }
    }

}
