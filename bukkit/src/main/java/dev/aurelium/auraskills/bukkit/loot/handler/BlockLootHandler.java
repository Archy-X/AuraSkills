package dev.aurelium.auraskills.bukkit.loot.handler;

import com.archyx.lootmanager.loot.Loot;
import com.archyx.lootmanager.loot.LootPool;
import com.archyx.lootmanager.loot.LootTable;
import com.archyx.lootmanager.loot.type.CommandLoot;
import com.archyx.lootmanager.loot.type.ItemLoot;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.bukkit.loot.provider.SkillLootProvider;
import dev.aurelium.auraskills.bukkit.skills.excavation.ExcavationLootProvider;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class BlockLootHandler extends LootHandler implements Listener {

    private final Random random = new Random();
    private final Map<Skill, SkillLootProvider> lootProviders = new HashMap<>();

    public BlockLootHandler(AuraSkills plugin) {
        super(plugin);
        registerLootProviders();
    }

    private void registerLootProviders() {
        lootProviders.put(Skills.EXCAVATION, new ExcavationLootProvider(plugin, this));
    }

    public Pair<BlockXpSource, Skill> getSource(Block block) {
        return plugin.getLevelManager().getLeveler(BlockLeveler.class).getSource(block, BlockXpSource.BlockTriggers.BREAK);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (getSource(block) == null) return;

        // Check block replace
        if (plugin.configBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) { // Only drop loot in survival mode
            return;
        }

        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            if (plugin.getHookManager().getHook(WorldGuardHook.class).blockedByFlag(block.getLocation(), player, WorldGuardHook.FlagKey.CUSTOM_LOOT)) {
                return;
            }
        }

        // TODO Reimplement SlimeFun hook

        User user = plugin.getUser(player);

        var originalSource = getSource(block);

        BlockXpSource source = originalSource.first();
        Skill skill = originalSource.second();

        // Get the loot provider for getting chance and cause
        SkillLootProvider provider = lootProviders.get(skill);

        LootTable table = plugin.getLootTableManager().getLootTable(skill);
        if (table == null) return;
        for (LootPool pool : table.getPools()) {
            // Calculate chance for pool
            double chance;
            if (provider != null) {
                chance = provider.getChance(pool, user);
            } else {
                chance = getCommonChance(pool, user);
            }

            LootDropEvent.Cause cause;
            if (provider != null) {
                cause = provider.getCause(pool);
            } else {
                cause = LootDropEvent.Cause.UNKNOWN;
            }
            // Select pool and give loot
            if (selectBlockLoot(pool, player, chance, source, event, skill, cause)) {
                break;
            }
        }
    }

    private boolean selectBlockLoot(LootPool pool, Player player, double chance, XpSource originalSource, BlockBreakEvent event, Skill skill, LootDropEvent.Cause cause) {
        if (random.nextDouble() < chance) { // Pool is selected
            Loot selectedLoot = selectLoot(pool, originalSource);
            // Give loot
            if (selectedLoot != null) {
                if (selectedLoot instanceof ItemLoot itemLoot) {
                    giveBlockItemLoot(player, itemLoot, event, originalSource, skill, cause);
                } else if (selectedLoot instanceof CommandLoot commandLoot) {
                    giveCommandLoot(player, commandLoot, originalSource, skill);
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
