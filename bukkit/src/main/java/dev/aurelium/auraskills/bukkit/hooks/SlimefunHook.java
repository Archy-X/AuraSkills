package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.ConfigurationNode;

public class SlimefunHook extends Hook implements Listener {

    private final AuraSkills plugin;

    public SlimefunHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlacerPlaceEvent(BlockPlacerPlaceEvent event) {
        if (!event.isCancelled()) {
            this.plugin.getRegionManager().addPlacedBlock(event.getBlock());
        }
    }

    public boolean hasBlockInfo(Location location) {
        return BlockStorage.hasBlockInfo(location);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return SlimefunHook.class;
    }
}
