package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.view.AnvilView;

import java.util.Locale;

public class AnvilDiscountTrait extends TraitImpl {

    AnvilDiscountTrait(AuraSkills plugin) {
        super(plugin, Traits.ANVIL_DISCOUNT);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        return NumberUtil.format1(getDiscount(value) * 100) + "%";
    }

    @EventHandler
    @SuppressWarnings("removal")
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
            double wisdom = user.getEffectiveTraitLevel(Traits.ANVIL_DISCOUNT);
            if (VersionUtils.isAtLeastVersion(21, 1)) {
                AnvilView view = event.getView();
                int repairCost = view.getRepairCost();
                int cost = (int) Math.round(repairCost * (1 - getDiscount(wisdom)));
                if (cost > 0) {
                    view.setRepairCost(cost);
                } else {
                    view.setRepairCost(1);
                }
            } else {
                AnvilInventory anvil = event.getInventory();
                int cost = (int) Math.round(anvil.getRepairCost() * (1 - getDiscount(wisdom)));
                if (cost > 0) {
                    anvil.setRepairCost(cost);
                } else {
                    anvil.setRepairCost(1);
                }
            }
        }
    }

    // Gets the anvil discount from 0 (0% off) to 1 (100% off)
    private double getDiscount(double wisdom) {
        return -1.0 * Math.pow(1.025, -1.0 * wisdom) + 1;
    }

}
