package dev.aurelium.auraskills.bukkit.hooks;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FancyHologramsHook extends HologramsHook {

    private final AuraSkills plugin;

    public FancyHologramsHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
    }

    @Override
    public void createHologram(Location location, String text, Player player) {
        TextHologramData data = new TextHologramData("auraskills_" + UUID.randomUUID(), location);
        data.setText(List.of(text));
        data.setBillboard(Billboard.CENTER);
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        Hologram hologram = manager.create(data);
        manager.addHologram(hologram);
        hologram.forceShowHologram(player);
        // Schedule removal
        deleteHologram(manager, hologram);
    }

    public void deleteHologram(HologramManager manager, Hologram hologram) {
        plugin.getScheduler().scheduleAtLocation(hologram.getData().getLocation(), () -> {
            manager.removeHologram(hologram);
        }, 30L * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return HologramsHook.class;
    }
}
