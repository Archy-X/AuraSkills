package dev.aurelium.auraskills.bukkit.hooks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
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
        Hologram hologram = HologramsAPI.createHologram(plugin, location);
        hologram.appendTextLine(text);
        deleteHologram(hologram);
    }

    public void deleteHologram(Hologram hd) {
        plugin.getScheduler().scheduleSync(hd::delete, 30L * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return HologramsHook.class;
    }
}
