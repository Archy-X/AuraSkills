package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.antiafk.AntiAfkManager;
import dev.aurelium.auraskills.common.antiafk.Check;
import dev.aurelium.auraskills.common.antiafk.CheckType;
import dev.aurelium.auraskills.common.antiafk.LogLocation;
import dev.aurelium.auraskills.common.ref.PlayerRef;
import dev.aurelium.auraskills.common.region.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

import static dev.aurelium.auraskills.bukkit.ref.BukkitPlayerRef.unwrap;

public class BukkitAntiAfkManager extends AntiAfkManager {

    private final AuraSkills plugin;

    public BukkitAntiAfkManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void registerCheckEvents(Check check) {
        if (check instanceof Listener listener) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void unregisterCheckEvents(Check check) {
        if (check instanceof Listener listener) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    public LogLocation getLogLocation(PlayerRef ref) {
        Player player = unwrap(ref);

        Location loc = player.getLocation();
        BlockPosition coords = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        @Nullable World world = loc.getWorld();
        String worldName = world != null ? world.getName() : "";

        return new LogLocation(coords, worldName);
    }

    @Override
    protected Constructor<?> getCheckConstructor(Class<? extends Check> checkClass) throws NoSuchMethodException {
        return checkClass.getDeclaredConstructor(BukkitCheckType.class, BukkitAntiAfkManager.class);
    }

    @Override
    public CheckType[] getCheckTypes() {
        return BukkitCheckType.values();
    }

}
