package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;

public class AnvilCostReductionTrait extends TraitImpl {

    AnvilCostReductionTrait(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        User user = null;
        // Finds the viewer with the highest wisdom level
        for (HumanEntity entity : event.getViewers()) {
            if (entity instanceof Player player) {
                // Check for disabled world
                if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
                    return;
                }
                User checkedUser = plugin.getUser(player);
                if (user == null) {
                    user = checkedUser;
                } else if (user.getStatLevel(Stats.WISDOM) < checkedUser.getStatLevel(Stats.WISDOM)) {
                    user = checkedUser;
                }
            }
        }
        if (user != null) {
            AnvilInventory anvil = event.getInventory();
            double wisdom = user.getEffectiveTraitLevel(Traits.ANVIL_COST_REDUCTION);
            int cost = (int) Math.round(anvil.getRepairCost() * (1 - (-1.0 * Math.pow(1.025, -1.0 * wisdom) + 1)));
            if (cost > 0) {
                anvil.setRepairCost(cost);
            } else {
                anvil.setRepairCost(1);
            }
        }

    }
}
