package dev.aurelium.auraskills.bukkit.loot.handler;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent.Cause;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.SlimefunHook;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootPool;
import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.bukkit.loot.context.SourceContext;
import dev.aurelium.auraskills.bukkit.loot.provider.SkillLootProvider;
import dev.aurelium.auraskills.bukkit.loot.type.CommandLoot;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import dev.aurelium.auraskills.bukkit.skills.excavation.ExcavationLootProvider;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BlockLootHandler extends LootHandler implements Listener {

    private final Random random = new Random();
    private final Map<Skill, SkillLootProvider> lootProviders = new HashMap<>();

    public BlockLootHandler(AuraSkills plugin) {
        super(plugin);
        registerLootProviders();
    }

    private void registerLootProviders() {
        lootProviders.put(Skills.EXCAVATION, new ExcavationLootProvider(plugin, this));
        lootProviders.put(Skills.MINING, new SkillLootProvider(plugin, this) {
            @Override
            public Cause getCause(LootPool pool) {
                return Cause.MINING_OTHER_LOOT;
            }
        });
        lootProviders.put(Skills.FORAGING, new SkillLootProvider(plugin, this) {
            @Override
            public Cause getCause(LootPool pool) {
                return Cause.FORAGING_OTHER_LOOT;
            }
        });
        lootProviders.put(Skills.FARMING, new SkillLootProvider(plugin, this) {
            @Override
            public Cause getCause(LootPool pool) {
                return Cause.FARMING_OTHER_LOOT;
            }
        });
    }

    public SkillSource<BlockXpSource> getSource(Block block) {
        return plugin.getLevelManager().getLeveler(BlockLeveler.class).getSource(block, BlockXpSource.BlockTriggers.BREAK);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();

        var originalSource = getSource(block);

        if (originalSource == null) return;

        BlockXpSource source = originalSource.source();
        Skill skill = originalSource.skill();

        // Check block replace
        if (plugin.configBoolean(Option.CHECK_BLOCK_REPLACE_ENABLED) && plugin.getRegionManager().isPlacedBlock(block)) {
            if (source.checkReplace()) {
                return;
            }
        }

        Player player = event.getPlayer();

        if (failsChecks(player, block.getLocation())) return;

        if (plugin.getHookManager().isRegistered(SlimefunHook.class)) {
            if (plugin.getHookManager().getHook(SlimefunHook.class).hasBlockInfo(block.getLocation())) {
                return;
            }
        }

        User user = plugin.getUser(player);

        // Get the loot provider for getting chance and cause
        SkillLootProvider provider = lootProviders.get(skill);

        LootTable table = plugin.getLootTableManager().getLootTable(skill);
        if (table == null) return;
        for (LootPool pool : table.getPools()) {
            // Ignore non-applicable sources
            if (provider != null && !provider.isApplicable(pool, source)) {
                continue;
            }
            if (isPoolUnobtainable(pool, source)) {
                continue;
            }
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
            if (selectBlockLoot(table, pool, player, chance, source, event, skill, cause)) {
                break;
            }
        }
    }

    private boolean selectBlockLoot(LootTable table, LootPool pool, Player player, double chance, XpSource originalSource, BlockBreakEvent event, Skill skill, LootDropEvent.Cause cause) {
        double rolled = random.nextDouble();
        if (rolled < chance) { // Pool is selected
            Loot selectedLoot = selectLoot(pool, new SourceContext(originalSource));
            // Give loot
            if (selectedLoot != null) {
                if (selectedLoot instanceof ItemLoot itemLoot) {
                    giveBlockItemLoot(player, itemLoot, event, skill, cause, table);
                } else if (selectedLoot instanceof CommandLoot commandLoot) {
                    giveCommandLoot(player, commandLoot, null, skill);
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
