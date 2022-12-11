package com.archyx.aureliumskills.loot.handler;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.support.WorldGuardFlags;
import com.archyx.lootmanager.loot.Loot;
import com.archyx.lootmanager.loot.LootPool;
import com.archyx.lootmanager.loot.LootTable;
import com.archyx.lootmanager.loot.type.CommandLoot;
import com.archyx.lootmanager.loot.type.ItemLoot;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;

public abstract class BlockLootHandler extends LootHandler implements Listener {

    private final Random random = new Random();

    public BlockLootHandler(AureliumSkills plugin, Skill skill, Ability ability) {
        super(plugin, skill, ability);
    }

    public abstract Source getSource(Block block);

    public abstract double getChance(LootPool pool, PlayerData playerData);

    public abstract LootDropCause getCause(LootPool pool);

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        if (!OptionL.isEnabled(skill)) return;
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (getSource(block) == null) return;

        // Check block replace
        if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
            return;
        }

        Player player = event.getPlayer();
        if (blockAbility(player)) return;

        if (player.getGameMode() != GameMode.SURVIVAL) { // Only drop loot in survival mode
            return;
        }

        if (plugin.isWorldGuardEnabled()) {
            if (plugin.getWorldGuardSupport().blockedByFlag(block.getLocation(), player, WorldGuardFlags.FlagKey.CUSTOM_LOOT)) {
                return;
            }
        }

        if (plugin.isSlimefunEnabled()) {
            if (BlockStorage.hasBlockInfo(block.getLocation())) {
                return;
            }
        }

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return;

        Source originalSource = getSource(block);

        LootTable table = plugin.getLootTableManager().getLootTable(skill);
        if (table == null) return;
        for (LootPool pool : table.getPools()) {
            // Calculate chance for pool
            double chance = getChance(pool, playerData);
            LootDropCause cause = getCause(pool);

            // Select pool and give loot
            if (selectBlockLoot(pool, player, chance, originalSource, event, cause)) {
                break;
            }
        }
    }

    private boolean selectBlockLoot(LootPool pool, Player player, double chance, Source originalSource, BlockBreakEvent event, LootDropCause cause) {
        if (random.nextDouble() < chance) { // Pool is selected
            Loot selectedLoot = selectLoot(pool, originalSource);
            // Give loot
            if (selectedLoot != null) {
                if (selectedLoot instanceof ItemLoot) {
                    ItemLoot itemLoot = (ItemLoot) selectedLoot;
                    giveBlockItemLoot(player, itemLoot, event, originalSource, cause);
                } else if (selectedLoot instanceof CommandLoot) {
                    CommandLoot commandLoot = (CommandLoot) selectedLoot;
                    giveCommandLoot(player, commandLoot, originalSource);
                }
                // Override vanilla loot if enabled
                if (pool.overridesVanillaLoot()) {
                    event.setDropItems(false);
                }
                return true;
            }
        }
        return false;
    }

}
