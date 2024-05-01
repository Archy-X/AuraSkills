package dev.aurelium.auraskills.bukkit.skills.fishing;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent.Cause;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.trait.GatheringLuckTraits;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FishingAbilities extends AbilityImpl {

    public FishingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.LUCKY_CATCH, Abilities.FISHER, Abilities.TREASURE_HUNTER, Abilities.GRAPPLER, Abilities.EPIC_CATCH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void luckyCatch(PlayerFishEvent event) {
        var ability = Abilities.LUCKY_CATCH;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();

        if (failsChecks(player, ability)) return;

        if (event.isCancelled()) return;

        if (!(event.getCaught() instanceof Item item)) return;

        if (event.getExpToDrop() <= 0) return;

        User user = plugin.getUser(player);

        int extraDrops = plugin.getTraitManager().getTraitImpl(GatheringLuckTraits.class).rollExtraDrops(user, Traits.FISHING_LUCK);
        if (extraDrops == 0) return;

        ItemStack drop = item.getItemStack();

        drop.setAmount(Math.min(drop.getAmount() + extraDrops, drop.getMaxStackSize()));

        LootDropEvent dropEvent = new LootDropEvent(player, user.toApi(), drop, item.getLocation(), Cause.FISHING_LUCK, false);
        Bukkit.getPluginManager().callEvent(dropEvent);

        if (!dropEvent.isCancelled()) {
            item.setItemStack(dropEvent.getItem());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void grappler(PlayerFishEvent event) {
        var ability = Abilities.GRAPPLER;

        if (isDisabled(ability)) return;

        if (event.getCaught() == null) return;

        // Caught entity should be a mob, not an item
        if (event.getCaught() instanceof Item) return;

        Player player = event.getPlayer();
        if (failsChecks(player, ability)) return;
        User user = plugin.getUser(player);

        Vector vector = player.getLocation().toVector().subtract(event.getCaught().getLocation().toVector());
        Vector result = vector.multiply(0.004 + (getValue(ability, user) / 25000));

        if (isUnsafeVelocity(result)) { // Prevent excessive velocity warnings
            return;
        }
        event.getCaught().setVelocity(result);
    }

    private boolean isUnsafeVelocity(Vector vector) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        return x > 4 || x < -4 || y > 4 || y < -4 || z > 4 || z < -4;
    }

}
