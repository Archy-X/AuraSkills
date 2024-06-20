package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.common.hooks.Hook;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.utils.drops.Drop;
import io.th0rgal.oraxen.utils.drops.Loot;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashSet;
import java.util.Set;

public class OraxenHook extends Hook implements Listener {

    private final AuraSkills plugin;
    @Nullable
    private BlockLeveler blockLeveler;

    public OraxenHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
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
        blockLeveler.handleBreak(player, block, event, trait -> getUniqueOraxenDrops(event.getDrop()));
    }

    private Set<ItemStack> getUniqueOraxenDrops(Drop drop) {
        Set<ItemStack> unique = new HashSet<>();
        for (Loot loot : drop.getLoots()) {
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
