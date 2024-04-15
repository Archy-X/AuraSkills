package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.trait.CritChanceTrait;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.TimeUnit;

public class CriticalHandler implements Listener {

    private final AuraSkills plugin;

    public CriticalHandler(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        DamageMeta meta = event.getDamageMeta();
        Player attacker = meta.getAttackerAsPlayer();

        if (attacker != null &&
                plugin.configBoolean(Option.valueOf("CRITICAL_ENABLED_" + event.getDamageMeta().getDamageType().name()))) {
            User user = plugin.getUser(attacker);
            meta.addAttackModifier(getCrit(attacker, user));
        }
    }

    private DamageModifier getCrit(Player player, User user) {
        if (!isCrit(user)) {
            return DamageModifier.none();
        }
        // Set metadata for holograms to detect
        player.setMetadata("skillsCritical", new FixedMetadataValue(plugin, true));
        plugin.getScheduler().scheduleSync(() -> player.removeMetadata("skillsCritical", plugin), 50, TimeUnit.MILLISECONDS);

        double value = user.getEffectiveTraitLevel(Traits.CRIT_DAMAGE) / 100;
        return new DamageModifier(value, DamageModifier.Operation.ADD_COMBINED);
    }

    private boolean isCrit(User user) {
        return plugin.getTraitManager().getTraitImpl(CritChanceTrait.class).isCrit(user);
    }

}
