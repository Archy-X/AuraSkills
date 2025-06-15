package dev.aurelium.auraskills.bukkit.antiafk.checks;

import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.bukkit.antiafk.BukkitAntiAfkManager;
import dev.aurelium.auraskills.bukkit.antiafk.BukkitCheck;
import dev.aurelium.auraskills.bukkit.antiafk.BukkitCheckType;
import dev.aurelium.auraskills.bukkit.antiafk.HandlerFunctions;
import dev.aurelium.auraskills.common.antiafk.PositionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import static dev.aurelium.auraskills.bukkit.ref.BukkitPlayerRef.wrap;

public class DamageA extends BukkitCheck {

    private final PositionHandler handler;

    public DamageA(BukkitCheckType type, BukkitAntiAfkManager manager) {
        super(type, manager);
        this.handler = new PositionHandler(optionDouble("max_distance"), optionInt("min_count"), HandlerFunctions::distanceSquared);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onXpGain(XpGainEvent event) {
        if (isDisabled() || !(event.getSource() instanceof DamageXpSource)) return;

        Player player = event.getPlayer();
        if (handler.failsCheck(getCheckData(player), wrap(player))) {
            event.setCancelled(true);
            logFail(player);
        }
    }

}
