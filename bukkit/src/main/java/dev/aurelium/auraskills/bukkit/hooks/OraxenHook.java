package dev.aurelium.auraskills.bukkit.hooks;

import com.google.common.collect.ImmutableMap;
import dev.aurelium.auraskills.api.source.type.BlockXpSource.BlockXpSourceState;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.source.type.BlockSource;
import dev.aurelium.auraskills.common.source.type.BlockSource.BlockSourceState;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.utils.drops.Drop;
import io.th0rgal.oraxen.utils.drops.Loot;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;

public class OraxenHook extends Hook implements Listener {

    private final AuraSkills plugin;
    @Nullable
    private BlockLeveler blockLeveler;

    public OraxenHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
        registerExternalItemProvider();
        registerBlockParsingExtension();
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return OraxenHook.class;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNoteBlockPlace(OraxenNoteBlockPlaceEvent event) {
        Block block = event.getBlock();
        plugin.getRegionManager().handleBlockPlace(block);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNoteBlockBreak(OraxenNoteBlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Lazy initialize BlockLeveler
        if (blockLeveler == null) {
            blockLeveler = plugin.getLevelManager().getLeveler(BlockLeveler.class);
        }
        blockLeveler.handleBreak(player, block, event, trait -> getUniqueOraxenDrops(player, event.getDrop()));
    }

    public void registerExternalItemProvider() {
        plugin.getItemRegistry().registerExternalItemProvider("oraxen", key -> {
            ItemBuilder builder = OraxenItems.getItemById(key);
            return builder.build();
        });
    }

    public void registerBlockParsingExtension() {
        plugin.getSourceTypeRegistry().registerParsingExtension(SourceTypes.BLOCK, source -> {
            BlockSource block = (BlockSource) source;

            String[] blocks = block.getBlocks();
            if (blocks == null) return block;

            @Nullable BlockXpSourceState[] states = block.getStates();
            List<BlockXpSourceState> stateBuilder = new ArrayList<>();
            // Modify states
            final String ORAXEN_PREFIX = "oraxen:";

            for (int i = 0; i < blocks.length; i++) {
                String blockId = blocks[i];
                if (!blockId.startsWith(ORAXEN_PREFIX)) {
                    continue;
                }

                String oraxenBlockId = blockId.substring(ORAXEN_PREFIX.length());
                BlockData blockData = OraxenBlocks.getOraxenBlockData(oraxenBlockId);
                if (blockData == null) continue;

                String blockDataString = blockData.getAsString(true);

                Map<String, Object> blockDataMap = BlockLeveler.parseFromBlockData(blockDataString);

                stateBuilder.add(new BlockSourceState(ImmutableMap.copyOf(blockDataMap)));

                // Replace oraxen: prefixed block name with actual material name
                blocks[i] = blockData.getMaterial().getKey().getKey().toLowerCase(Locale.ROOT);
            }
            // Convert stateBuilder List to states array and assign
            if (!stateBuilder.isEmpty()) {
                states = stateBuilder.toArray(new BlockXpSourceState[0]);
            }

            // May be modified: blocks, states
            return new BlockSource(plugin, block.getValues(), blocks, block.getTriggers(), block.checkReplace(),
                    states, block.getAfterStates(), block.getStateMultiplier(), block.getSupportBlockType(),
                    block.isTrunk(), block.isLeaf());
        });
    }

    private Set<ItemStack> getUniqueOraxenDrops(Player player, Drop drop) {
        Set<ItemStack> unique = new HashSet<>();
        for (Loot loot : drop.getLootToDrop(player)) {
            ItemStack item = loot.getItemStack();

            boolean alreadyAdded = false;
            for (ItemStack existing : unique) {
                if (existing.isSimilar(item)) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                unique.add(item);
            }
        }
        return unique;
    }

}
