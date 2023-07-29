package dev.aurelium.auraskills.bukkit.skills.farming;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.item.BukkitItemHolder;
import dev.aurelium.auraskills.bukkit.util.BukkitLocationHolder;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class FarmingAbilities extends AbilityImpl {

    public FarmingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.BOUNTIFUL_HARVEST, Abilities.FARMER, Abilities.TRIPLE_HARVEST, Abilities.GENETICIST, Abilities.SCYTHE_MASTER);
    }

    public void bountifulHarvest(Player player, User user, Block block) {
        var ability = Abilities.BOUNTIFUL_HARVEST;

        if (isDisabled(ability)) return;

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        if (rand.nextDouble() < getValue(ability, user) / 100) {
            for (ItemStack item : block.getDrops()) {
                checkMelonSilkTouch(player, block, item);

                var itemHolder = new BukkitItemHolder(item);
                var locationHolder = new BukkitLocationHolder(block.getLocation().add(0.5, 0.5, 0.5));
                LootDropEvent event = new LootDropEvent(plugin.getApi(), user.toApi(), itemHolder, locationHolder, LootDropEvent.Cause.BOUNTIFUL_HARVEST);
                plugin.getEventManager().callEvent(event);
                if (!event.isCancelled()) {
                    block.getWorld().dropItem(event.getLocation().get(Location.class), event.getItem().get(ItemStack.class));
                }
            }
        }
    }

    public void tripleHarvest(Player player, User user, Block block) {
        var ability = Abilities.TRIPLE_HARVEST;

        if (isDisabled(ability)) return;

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        if (rand.nextDouble() < getValue(ability, user) / 100) {
            for (ItemStack item : block.getDrops()) {
                checkMelonSilkTouch(player, block, item);
                ItemStack droppedItem = item.clone();
                droppedItem.setAmount(2);

                var itemHolder = new BukkitItemHolder(droppedItem);
                var locHolder = new BukkitLocationHolder(block.getLocation().add(0.5, 0.5, 0.5));
                LootDropEvent event = new LootDropEvent(plugin.getApi(), user.toApi(), itemHolder, locHolder, LootDropEvent.Cause.TRIPLE_HARVEST);
                plugin.getEventManager().callEvent(event);
                if (!event.isCancelled()) {
                    block.getWorld().dropItem(event.getLocation().get(Location.class), event.getItem().get(ItemStack.class));
                }
            }
        }
    }

    private void checkMelonSilkTouch(Player player, Block block, ItemStack item) {
        if (block.getType() == Material.MELON) {
            if (player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                item.setType(Material.MELON);
                item.setAmount(1);
            }
        }
    }

    @EventHandler
    public void geneticist(PlayerItemConsumeEvent event) {
        var ability = Abilities.GENETICIST;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();
        if (failsChecks(player, ability)) return;

        Material mat = event.getItem().getType();
        if (isPlantBased(mat)) { // Only allow plant based foods
            User user = plugin.getUser(player);

            float amount = (float) getValue(ability, user) / 10;
            player.setSaturation(player.getSaturation() + amount);
        }
    }

    private boolean isPlantBased(Material mat) {
        return mat.equals(Material.BREAD) || mat.equals(Material.APPLE) || mat.equals(Material.GOLDEN_APPLE) || mat.equals(Material.POTATO)
                || mat.equals(Material.BAKED_POTATO) || mat.equals(Material.CARROT) || mat.equals(Material.GOLDEN_CARROT) || mat.equals(Material.MELON)
                || mat.equals(Material.PUMPKIN_PIE) || mat.equals(Material.BEETROOT) || mat.equals(Material.BEETROOT_SOUP) || mat.equals(Material.MUSHROOM_STEW)
                || mat.equals(Material.POISONOUS_POTATO);
    }

    public void scytheMaster(EntityDamageByEntityEvent event, Player player, User user) {
        var ability = Abilities.SCYTHE_MASTER;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) > 0) {
            event.setDamage(event.getDamage() * (1 + (getValue(ability, user) / 100)));
        }
    }

}
