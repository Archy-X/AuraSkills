package dev.aurelium.auraskills.bukkit.antiafk.checks;

import dev.aurelium.auraskills.api.event.skill.EntityXpGainEvent;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.bukkit.antiafk.AntiAfkManager;
import dev.aurelium.auraskills.bukkit.antiafk.Check;
import dev.aurelium.auraskills.bukkit.antiafk.CheckType;
import dev.aurelium.auraskills.bukkit.antiafk.handler.IdentityHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class EntityC extends Check {

    private final IdentityHandler handler;

    public EntityC(CheckType type, AntiAfkManager manager) {
        super(type, manager);
        this.handler = new IdentityHandler(optionInt("min_count"), "previous_entity");
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityXpGain(EntityXpGainEvent event) {
        if (isDisabled() || !(event.getSource() instanceof EntityXpSource) || !(event.getAttacked() instanceof Player)) return;

        Player player = event.getPlayer();
        if (handler.failsCheck(getCheckData(player), event.getAttacked().getUniqueId())) {
            event.setCancelled(true);
            logFail(player);
        }
    }
}
