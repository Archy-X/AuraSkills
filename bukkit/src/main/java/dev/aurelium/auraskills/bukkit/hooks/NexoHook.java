package dev.aurelium.auraskills.bukkit.hooks;

import com.google.common.collect.ImmutableMap;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockBreakEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockPlaceEvent;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.nexo.utils.drops.Loot;
import dev.aurelium.auraskills.api.source.type.BlockXpSource.BlockXpSourceState;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.source.type.BlockSource;
import dev.aurelium.auraskills.common.source.type.BlockSource.BlockSourceState;
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

public class NexoHook extends Hook implements Listener {

    private final AuraSkills plugin;
    @Nullable
    private BlockLeveler blockLeveler;

    public NexoHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
        registerExternalItemProvider();
        registerBlockParsingExtension();
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return NexoHook.class;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNoteBlockPlace(NexoNoteBlockPlaceEvent event) {
        Block block = event.getBlock();
        plugin.getRegionManager().handleBlockPlace(block);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNoteBlockBreak(NexoNoteBlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Lazy initialize BlockLeveler
        if (blockLeveler == null) {
            blockLeveler = plugin.getLevelManager().getLeveler(BlockLeveler.class);
        }
        blockLeveler.handleBreak(player, block, event, trait -> getUniqueNexoDrops(player, event.getDrop()));
    }

    public void registerExternalItemProvider() {
        plugin.getItemRegistry().registerExternalItemProvider("nexo", key -> {
            ItemBuilder builder = NexoItems.itemFromId(key);
            return builder != null ? builder.build() : null;
        });
    }

    @SuppressWarnings("deprecation")
    public void registerBlockParsingExtension() {
        plugin.getSourceTypeRegistry().registerParsingExtension(SourceTypes.BLOCK, source -> {
            BlockSource block = (BlockSource) source;

            String[] blocks = block.getBlocks();
            if (blocks == null) return block;

            @Nullable BlockXpSourceState[] states = block.getStates();
            List<BlockXpSourceState> stateBuilder = new ArrayList<>();
            // Modify states
            final String nexoPrefix = "nexo:";

            for (int i = 0; i < blocks.length; i++) {
                String blockId = blocks[i];
                if (!blockId.startsWith(nexoPrefix)) {
                    continue;
                }

                String nextBlockId = blockId.substring(nexoPrefix.length());
                BlockData blockData = NexoBlocks.blockData(nextBlockId);
                if (blockData == null) continue;

                String blockDataString = blockData.getAsString(true);

                Map<String, Object> blockDataMap = BlockLeveler.parseFromBlockData(blockDataString);

                stateBuilder.add(new BlockSourceState(ImmutableMap.copyOf(blockDataMap)));

                // Replace nexo: prefixed block name with actual material name
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

    private Set<ItemStack> getUniqueNexoDrops(Player player, Drop drop) {
        Set<ItemStack> unique = new HashSet<>();
        for (Loot loot : drop.lootToDrop(player)) {
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
