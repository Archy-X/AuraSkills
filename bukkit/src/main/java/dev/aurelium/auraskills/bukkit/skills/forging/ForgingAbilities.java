package dev.aurelium.auraskills.bukkit.skills.forging;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.source.GrindstoneLeveler;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.bukkit.util.EnchantmentValue;
import dev.aurelium.auraskills.bukkit.util.GrindstoneEnchant;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ForgingAbilities extends AbilityImpl {

    public ForgingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.DISENCHANTER, Abilities.FORGER, Abilities.REPAIRING, Abilities.ANVIL_MASTER, Abilities.SKILL_MENDER);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void disenchanter(InventoryClickEvent event) {
        var ability = Abilities.DISENCHANTER;

        if (event.isCancelled()) return;

        if (isDisabled(ability)) return;

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (failsChecks(player, ability)) return;

        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        ClickType click = event.getClick();
        // Only allow right and left clicks if inventory full
        if (click != ClickType.LEFT && click != ClickType.RIGHT && ItemUtils.isInventoryFull(player)) return;
        if (event.getResult() != Event.Result.ALLOW) return; // Make sure the click was successful

        InventoryAction action = event.getAction();
        // Only give if item was picked up
        if (action != InventoryAction.PICKUP_ALL && action != InventoryAction.MOVE_TO_OTHER_INVENTORY
                && action != InventoryAction.PICKUP_HALF && action != InventoryAction.DROP_ALL_SLOT
                && action != InventoryAction.DROP_ONE_SLOT && action != InventoryAction.HOTBAR_SWAP) {
            return;
        }
        if (player.getItemOnCursor().getType() != Material.AIR) {
            if (action == InventoryAction.DROP_ALL_SLOT || action == InventoryAction.DROP_ONE_SLOT) {
                return;
            }
        }
        if (event.getClickedInventory().getType() != InventoryType.GRINDSTONE) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        User user = plugin.getUser(player);

        Location location = inventory.getLocation();
        if (location == null) return;

        ItemStack first = inventory.getItem(0);
        ItemStack second = inventory.getItem(1);
        if (first != null && second != null) { // If two items, make sure items are the same type
            if (first.getType() != second.getType()) {
                return;
            }
        }

        Set<EnchantmentValue> enchants = new HashSet<>();
        // Add enchants to disenchant
        checkEnchants(first, enchants);
        checkEnchants(second, enchants);
        if (enchants.isEmpty()) return;
        // Calculate the sum
        try {
            int sum = 0;
            for (EnchantmentValue value : enchants) {
                String enchantName = value.getEnchantment().getKey().getKey().toUpperCase(Locale.ROOT);
                if (containsEnchant(enchantName)) {
                    sum += GrindstoneEnchant.valueOf(enchantName).getLevel(value.getLevel());
                }
            }
            int average = (sum + (int) Math.ceil(((double) sum) / 2)) / 2; // Get the average experience that would drop
            int added = (int) Math.round(average * (getValue(ability, user) / 100));
            World world = location.getWorld();
            if (world != null) {
                world.spawn(location, ExperienceOrb.class).setExperience(added);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    private void checkEnchants(ItemStack item, Set<EnchantmentValue> enchants) {
        if (item == null) {
            return;
        }
        for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            if (plugin.getLevelManager().getLeveler(GrindstoneLeveler.class).isDisenchantable(entry.getKey())) {
                enchants.add(new EnchantmentValue(entry.getKey(), entry.getValue()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void repairing(PrepareAnvilEvent event) {
        var ability = Abilities.REPAIRING;

        if (isDisabled(ability)) return;

        Player player = getHighestPlayer(event.getViewers(), ability.getSkill());
        if (player == null) return;

        if (failsChecks(player, ability)) return;

        AnvilInventory inventory = event.getInventory();

        User user = plugin.getUser(player);

        ItemStack first = inventory.getItem(0);
        ItemStack second = inventory.getItem(1);
        ItemStack result = event.getResult();
        if (first == null || second == null || result == null) return;
        // Check if the seconds slot is a raw material for repair
        Material rawMaterial = getRawMaterial(first.getType());
        if (rawMaterial == null) return;
        if (second.getType() != rawMaterial) {
            return;
        }

        ItemMeta meta = first.getItemMeta();
        if (meta == null) return;
        if (meta instanceof Damageable damageable) {
            short max = first.getType().getMaxDurability();
            // Calculate durability to add, vanilla by default adds 20% of the max durability
            long addedLong = second.getAmount() * (Math.round(0.25 * max) + Math.round(max * 0.25 * (getValue(ability, user) / 100)));
            short added;
            if (addedLong > Short.MAX_VALUE) {
                added = (short) damageable.getDamage();
            } else if (addedLong < Short.MIN_VALUE) {
                added = 0;
            } else {
                added = (short) addedLong;
            }
            damageable.setDamage(Math.max(damageable.getDamage() - added, 0));
            result.setItemMeta(damageable);
        }
    }

    @EventHandler
    public void skillMender(XpGainEvent event) {
        var ability = Abilities.SKILL_MENDER;

        if (event.isCancelled()) return;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();

        if (failsChecks(player, ability)) return;

        User user = BukkitUser.getUser(event.getUser());

        if (rand.nextDouble() < getValue(ability, user) / 100) {
            // Get all the items that have mending
            List<ItemStack> mendingItems = new ArrayList<>();
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null) {
                    if (item.containsEnchantment(Enchantment.MENDING) && hasDamage(item)) {
                        mendingItems.add(item);
                    }
                }
            }
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            if (mainHandItem.containsEnchantment(Enchantment.MENDING) && hasDamage(mainHandItem)) {
                mendingItems.add(mainHandItem);
            }
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            if (offHandItem.containsEnchantment(Enchantment.MENDING) && hasDamage(offHandItem)) {
                mendingItems.add(offHandItem);
            }
            if (mendingItems.isEmpty()) return;

            ItemStack mendedItem = mendingItems.get(rand.nextInt(mendingItems.size())); // Pick a random item
            int durabilityToRepair = (int) Math.round(event.getAmount() / 2); // One durability per 2 skill xp gained
            // Apply durability repair
            ItemMeta meta = mendedItem.getItemMeta();
            if (meta == null) return;
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(Math.max(damageable.getDamage() - durabilityToRepair, 0));
                mendedItem.setItemMeta(damageable);
            }
        }
    }

    @Nullable
    private Material getRawMaterial(Material material) {
        String name = material.name();
        if (name.startsWith("DIAMOND_")) {
            if (!name.equals("DIAMOND_ORE") && !name.equals("DIAMOND_BLOCK") && !name.equals("DIAMOND_HORSE_ARMOR")) {
                return Material.DIAMOND;
            }
        } else if (name.startsWith("GOLD_") || name.startsWith("GOLDEN_")) {
            if (!name.contains("APPLE") && !name.contains("CARROT") && !name.contains("HORSE_ARMOR")
                    && !name.contains("GOLD") && !name.contains("INGOT") && !name.contains("NUGGET")
                    && !name.contains("ORE") && !name.contains("BARDING")) {
                return Material.GOLD_INGOT;
            }
        } else if (name.startsWith("IRON_")) {
            if (!name.contains("BARS") && !name.contains("DOOR") && !name.contains("BLOCK")
                    && !name.contains("INGOT") && !name.contains("NUGGET") && !name.contains("ORE")
                    && !name.contains("TRAPDOOR") && !name.contains("HORSE_ARMOR")) {
                return Material.IRON_INGOT;
            }
        } else if (name.startsWith("LEATHER_")) {
            if (!name.contains("HORSE_ARMOR")) {
                return Material.LEATHER;
            }
        } else if (name.startsWith("NETHERITE_")) {
            if (!name.contains("SCRAP") && !name.contains("INGOT") && !name.contains("BLOCK")) {
                return Material.NETHERITE_INGOT;
            }
        } else if (name.equals("TURTLE_HELMET")) {
            return CompatUtil.getTurtleScute();
        } else if (material == Material.ELYTRA) {
            return Material.PHANTOM_MEMBRANE;
        } else if (name.startsWith("CHAINMAIL")) {
            return Material.IRON_INGOT;
        }
        return null;
    }

    @Nullable
    private Player getHighestPlayer(List<HumanEntity> viewers, Skill skill) {
        int highestLevel = 0;
        Player highestPlayer = null;
        for (HumanEntity entity : viewers) {
            if (entity instanceof Player player) {
                User user = plugin.getUser(player);

                int level = user.getSkillLevel(skill);
                if (level > highestLevel) {
                    highestLevel = level;
                    highestPlayer = player;
                }
            }
        }
        return highestPlayer;
    }

    private boolean hasDamage(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta instanceof Damageable) {
                return ((Damageable) meta).hasDamage();
            }
        }
        return false;
    }

    private boolean containsEnchant(String enchantName) {
        for (GrindstoneEnchant grindstoneEnchant : GrindstoneEnchant.values()) {
            if (grindstoneEnchant.toString().equals(enchantName)) {
                return true;
            }
        }
        return false;
    }

}
