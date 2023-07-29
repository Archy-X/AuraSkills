package dev.aurelium.auraskills.bukkit.skills.fishing;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.item.BukkitItemHolder;
import dev.aurelium.auraskills.bukkit.util.BukkitLocationHolder;
import dev.aurelium.auraskills.common.user.User;
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

        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            ItemStack drop = item.getItemStack();
            if (drop.getMaxStackSize() <= 0) return;

            drop.setAmount(drop.getAmount() * 2);

            var itemHolder = new BukkitItemHolder(drop);
            var locHolder = new BukkitLocationHolder(item.getLocation());
            LootDropEvent dropEvent = new LootDropEvent(plugin.getApi(), user.toApi(), itemHolder, locHolder, LootDropEvent.Cause.LUCKY_CATCH);
            plugin.getEventManager().callEvent(dropEvent);

            if (!event.isCancelled()) {
                item.setItemStack(dropEvent.getItem().get(ItemStack.class));
            }
        }
    }

    @EventHandler
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
