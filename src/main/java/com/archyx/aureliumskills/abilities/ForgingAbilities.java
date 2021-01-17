package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.XpGainEvent;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.EnchantmentValue;
import com.archyx.aureliumskills.util.GrindstoneEnchant;
import com.archyx.aureliumskills.util.VersionUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ForgingAbilities extends AbilityProvider implements Listener {

    private final Random random = new Random();

    public ForgingAbilities(AureliumSkills plugin) {
        super(plugin, Skill.FORGING);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void disenchanter(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (blockDisabled(Ability.DISENCHANTER)) return;
        if (!VersionUtils.isAboveVersion(14)) return; // This ability requires at least 1.14
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (blockAbility(player)) return;
            Inventory inventory = event.getClickedInventory();
            if (inventory == null) return;
            if (event.getClickedInventory().getType() == InventoryType.GRINDSTONE) {
                if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                    PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                    if (playerSkill == null) return;
                    if (playerSkill.getAbilityLevel(Ability.DISENCHANTER) == 0) return;
                    Location location = inventory.getLocation();
                    if (location == null) return;
                    ItemStack first = inventory.getItem(0);
                    ItemStack second = inventory.getItem(1);
                    Set<EnchantmentValue> enchants = new HashSet<>();
                    // Add enchants to disenchant
                    if (first != null) {
                        for (Map.Entry<Enchantment, Integer> entry : first.getEnchantments().entrySet()) {
                            if (entry.getKey() != Enchantment.BINDING_CURSE && entry.getKey() != Enchantment.VANISHING_CURSE) {
                                enchants.add(new EnchantmentValue(entry.getKey(), entry.getValue()));
                            }
                        }
                    }
                    if (second != null) {
                        for (Map.Entry<Enchantment, Integer> entry : second.getEnchantments().entrySet()) {
                            if (entry.getKey() != Enchantment.BINDING_CURSE && entry.getKey() != Enchantment.VANISHING_CURSE) {
                                enchants.add(new EnchantmentValue(entry.getKey(), entry.getValue()));
                            }
                        }
                    }
                    if (enchants.size() == 0) return;
                    // Calculate the sum
                    int sum = 0;
                    for (EnchantmentValue value : enchants) {
                        sum += GrindstoneEnchant.valueOf(value.getEnchantment().getKey().getKey().toUpperCase(Locale.ENGLISH)).getLevel(value.getLevel());
                    }
                    int average = (sum + (int) Math.ceil(((double) sum) / 2)) / 2; // Get the average experience that would drop
                    int added = (int) Math.round(average * (getValue(Ability.DISENCHANTER, playerSkill) / 100));
                    World world = location.getWorld();
                    if (world != null) {
                        world.spawn(location, ExperienceOrb.class).setExperience(added);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("deprecation")
    public void repairing(PrepareAnvilEvent event) {
        if (blockDisabled(Ability.REPAIRING)) return;
        Player player = getHighestPlayer(event.getViewers());
        if (player == null) return;
        if (blockAbility(player)) return;
        AnvilInventory inventory = event.getInventory();
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill == null) return;
        if (playerSkill.getAbilityLevel(Ability.REPAIRING) == 0) return;
        ItemStack first = inventory.getItem(0);
        ItemStack second = inventory.getItem(1);
        ItemStack result = event.getResult();
        if (first == null || second == null || result == null) return;
        // Check if the seconds slot is a raw material for repair
        XMaterial rawMaterial = getRawMaterial(first.getType());
        if (rawMaterial == null) return;
        if (second.getType() == rawMaterial.parseMaterial()) {
            if (XMaterial.isNewVersion()) {
                ItemMeta meta = first.getItemMeta();
                if (meta == null) return;
                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    short max = first.getType().getMaxDurability();
                    // Calculate durability to add, vanilla by default adds 20% of the max durability
                    short added = (short) (second.getAmount() * (Math.round(0.25 * max) + Math.round(max * 0.25 * (getValue(Ability.REPAIRING, playerSkill) / 100))));
                    damageable.setDamage(Math.max(damageable.getDamage() - added, 0));
                    result.setItemMeta((ItemMeta) damageable);
                }
            } else {
                // For old versions
                short max = result.getType().getMaxDurability();
                short added = (short) (second.getAmount() * (Math.round(0.25 * max) + Math.round(max * 0.25 * (getValue(Ability.REPAIRING, playerSkill) / 100))));
                result.setDurability((short) Math.max(first.getDurability() - added, 0));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void anvilMaster(InventoryOpenEvent event) {
        if (event.isCancelled()) return;
        if (blockDisabled(Ability.ANVIL_MASTER)) return;
        if (!XMaterial.isNewVersion()) return;
        Inventory inventory = event.getInventory();
        if (inventory.getType() == InventoryType.ANVIL && inventory instanceof AnvilInventory) {
            AnvilInventory anvil = (AnvilInventory) inventory;
            if (event.getPlayer() instanceof Player) {
                Player player = (Player) event.getPlayer();
                if (blockAbility(player)) return;
                PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                if (playerSkill == null) return;
                if (playerSkill.getAbilityLevel(Ability.ANVIL_MASTER) > 0) {
                    int maxCost = (int) Math.round(getValue(Ability.ANVIL_MASTER, playerSkill));
                    anvil.setMaximumRepairCost(maxCost);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("deprecation")
    public void skillMender(XpGainEvent event) {
        if (event.isCancelled()) return;
        if (blockDisabled(Ability.SKILL_MENDER)) return;
        Player player = event.getPlayer();
        if (blockAbility(player)) return;
        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
        if (playerSkill == null) return;
        if (playerSkill.getAbilityLevel(Ability.SKILL_MENDER) == 0) return;
        if (random.nextDouble() < getValue(Ability.SKILL_MENDER, playerSkill) / 100) {
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
            if (mendingItems.size() == 0) return;
            ItemStack mendedItem = mendingItems.get(random.nextInt(mendingItems.size())); // Pick a random item
            int durabilityToRepair = (int) Math.round(event.getAmount() / 2); // One durability per 2 skill xp gained
            // Apply durability repair
            if (XMaterial.isNewVersion()) {
                ItemMeta meta = mendedItem.getItemMeta();
                if (meta == null) return;
                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    damageable.setDamage(Math.max(damageable.getDamage() - durabilityToRepair, 0));
                    mendedItem.setItemMeta((ItemMeta) damageable);
                }
            } else {
                mendedItem.setDurability((short) Math.max(mendedItem.getDurability() - durabilityToRepair, 0));
            }
        }
    }

    @Nullable
    private XMaterial getRawMaterial(Material material) {
        String name = material.name();
        if (name.startsWith("DIAMOND_")) {
            if (!name.equals("DIAMOND_ORE") && !name.equals("DIAMOND_BLOCK") && !name.equals("DIAMOND_HORSE_ARMOR")) {
                return XMaterial.DIAMOND;
            }
        } else if (name.startsWith("GOLD_") || name.startsWith("GOLDEN_")) {
            if (!name.contains("APPLE") && !name.contains("CARROT") && !name.contains("HORSE_ARMOR")
                && !name.contains("GOLD") && !name.contains("INGOT") && !name.contains("NUGGET")
                && !name.contains("ORE") && !name.contains("BARDING")) {
                return XMaterial.GOLD_INGOT;
            }
        } else if (name.startsWith("IRON_")) {
            if (!name.contains("BARS") && !name.contains("DOOR") && !name.contains("BLOCK")
                && !name.contains("INGOT") && !name.contains("NUGGET") && !name.contains("ORE")
                && !name.contains("TRAPDOOR") && !name.contains("HORSE_ARMOR")) {
                return XMaterial.IRON_INGOT;
            }
        } else if (name.startsWith("LEATHER_")) {
            if (!name.contains("HORSE_ARMOR")) {
                return XMaterial.LEATHER;
            }
        } else if (name.startsWith("NETHERITE_")) {
            if (!name.contains("SCRAP") && !name.contains("INGOT") && !name.contains("BLOCK")) {
                return XMaterial.NETHERITE_INGOT;
            }
        } else if (name.equals("TURTLE_HELMET")) {
            return XMaterial.SCUTE;
        } else if (material == Material.ELYTRA) {
            return XMaterial.PHANTOM_MEMBRANE;
        } else if (name.startsWith("CHAINMAIL")) {
            return XMaterial.IRON_INGOT;
        }
        return null;
    }

    @Nullable
    private Player getHighestPlayer(List<HumanEntity> viewers) {
        int highestLevel = 0;
        Player highestPlayer = null;
        for (HumanEntity entity : viewers) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                if (playerSkill != null) {
                    int level = playerSkill.getSkillLevel(Skill.FORGING);
                    if (level > highestLevel) {
                        highestLevel = level;
                        highestPlayer = player;
                    }
                }
            }
        }
        return highestPlayer;
    }

    @SuppressWarnings("deprecation")
    private boolean hasDamage(ItemStack item) {
        if (XMaterial.isNewVersion()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (meta instanceof Damageable) {
                    return ((Damageable) meta).hasDamage();
                }
            }
        } else {
            return item.getDurability() > 0;
        }
        return false;
    }


}
