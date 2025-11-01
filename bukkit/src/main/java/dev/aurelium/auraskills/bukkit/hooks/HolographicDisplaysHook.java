package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.concurrent.TimeUnit;

public class HolographicDisplaysHook extends HologramsHook {

    private final AuraSkills plugin;

    public HolographicDisplaysHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
    }

    @Override
    public void createHologram(Location location, String text) {
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);
        Hologram hologram = api.createHologram(location);
        hologram.getLines().appendText(text);
        deleteHologram(hologram);
    }

    public void deleteHologram(Hologram hd) {
        plugin.getScheduler().scheduleAtLocation(hd.getPosition().toLocation(), hd::delete, 30L * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return HologramsHook.class;
    }

}
