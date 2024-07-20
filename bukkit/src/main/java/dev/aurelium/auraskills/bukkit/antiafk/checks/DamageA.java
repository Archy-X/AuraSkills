package dev.aurelium.auraskills.bukkit.antiafk.checks;

import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.bukkit.antiafk.AntiAfkManager;
import dev.aurelium.auraskills.bukkit.antiafk.Check;
import dev.aurelium.auraskills.bukkit.antiafk.CheckType;
import dev.aurelium.auraskills.bukkit.antiafk.handler.PositionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class DamageA extends Check {

    private final PositionHandler handler;

    public DamageA(CheckType type, AntiAfkManager manager) {
        super(type, manager);
        this.handler = new PositionHandler(optionDouble("max_distance"), optionInt("min_count"));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onXpGain(XpGainEvent event) {
        if (isDisabled() || !(event.getSource() instanceof DamageXpSource)) return;

        Player player = event.getPlayer();
        if (handler.failsCheck(getCheckData(player), player)) {
            event.setCancelled(true);
            logFail(player);
        }
    }

}
