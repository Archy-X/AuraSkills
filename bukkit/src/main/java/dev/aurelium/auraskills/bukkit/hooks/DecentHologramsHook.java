package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.hooks.Hook;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DecentHologramsHook extends HologramsHook {

    private final AuraSkills plugin;

    public DecentHologramsHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
    }

    @Override
    public void createHologram(Location location, String text) {
        Hologram hologram = DHAPI.createHologram("AureliumSkills_" + UUID.randomUUID(), location, false, Collections.singletonList(text));
        deleteHologram(hologram);
    }

    public void deleteHologram(Hologram dh) {
        plugin.getScheduler().scheduleSync(dh::delete, 30L * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return HologramsHook.class;
    }
}
