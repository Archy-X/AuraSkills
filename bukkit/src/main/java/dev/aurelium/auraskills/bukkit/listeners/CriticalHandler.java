package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.trait.CritChanceTrait;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CriticalHandler {

    private final Random rand = new Random();
    private final AuraSkills plugin;

    public CriticalHandler(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public DamageModifier getCrit(Player player, User user) {
        if (!Abilities.CRIT_CHANCE.isEnabled()) {
            return DamageModifier.none();
        }

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
