package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardFlags.FlagKey;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Random;

public class DoubleDropTrait extends TraitImpl {

    private final Random r = new Random();

    DoubleDropTrait(AuraSkills plugin) {
        super(plugin, Traits.DOUBLE_DROP);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        return NumberUtil.format1(value) + "%";
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!Traits.DOUBLE_DROP.isEnabled()) return;
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        //Checks if in blocked or disabled world
        if (plugin.getWorldManager().isInBlockedWorld(block.getLocation())) {
            return;
        }
        //Checks if in blocked region
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            WorldGuardHook worldGuard = plugin.getHookManager().getHook(WorldGuardHook.class);
            if (worldGuard.isBlocked(block.getLocation(), player, FlagKey.XP_GAIN)) {
                return;
            }
        }
        if (!event.isDropItems()) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (plugin.getRegionManager().isPlacedBlock(block)) return;
        User user = plugin.getUser(player);
        Material mat = block.getType();
        if (mat.equals(Material.STONE) || mat.equals(Material.COBBLESTONE) || mat.equals(Material.SAND) || mat.equals(Material.GRAVEL)
                || mat.equals(Material.DIRT) || mat.equals(Material.GRASS_BLOCK) || mat.equals(Material.ANDESITE)
                || mat.equals(Material.DIORITE) || mat.equals(Material.GRANITE)) {
            //Calculate chance
            double percentChance = user.getEffectiveTraitLevel(Traits.DOUBLE_DROP);
            // Apply max_percent option
            double maxPercent = Traits.DOUBLE_DROP.optionDouble("max_percent");
            if (percentChance > maxPercent) {
                percentChance = maxPercent;
            }
            double chance = percentChance / 100.0;
            if (r.nextDouble() < chance) {
                ItemStack tool = player.getInventory().getItemInMainHand();
                Location location = block.getLocation().add(0.5, 0.5, 0.5);
                for (ItemStack item : block.getDrops(tool)) {
                    // If silk touch
                    ItemStack itemToDrop;
                    if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                        if (mat.equals(Material.STONE)) {
                            itemToDrop = new ItemStack(Material.STONE);
                        } else if (mat.equals(Material.GRASS_BLOCK)) {
                            itemToDrop = new ItemStack(Material.GRASS_BLOCK);
                        } else {
                            itemToDrop = item.clone();
                        }
                    } else {
                        itemToDrop = item.clone();
                    }
                    boolean toInventory = plugin.getLootTableManager().toInventory(player.getInventory().getItemInMainHand());
                    LootDropEvent lootDropEvent = new LootDropEvent(player, user.toApi(), itemToDrop, location, LootDropEvent.Cause.LUCK_DOUBLE_DROP, toInventory);
                    Bukkit.getPluginManager().callEvent(lootDropEvent);

                    if (lootDropEvent.isCancelled()) {
                        continue;
                    }

                    ItemUtils.giveBlockLoot(player, lootDropEvent);
                }
            }
        }
    }
}
