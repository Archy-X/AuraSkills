package dev.aurelium.auraskills.bukkit.modifier;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTType;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
import dev.aurelium.auraskills.bukkit.skills.foraging.ForagingAbilities;
import dev.aurelium.auraskills.bukkit.skills.mining.MiningAbilities;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ItemListener implements Listener {

    private final AuraSkills plugin;
    private final Map<UUID, ItemStack> heldItems;
    private final Map<UUID, ItemStack> offHandItems;
    private final StatManager statManager;
    private final ForagingAbilities foragingAbilities;
    private final MiningAbilities miningAbilities;

    public ItemListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.heldItems = new HashMap<>();
        this.offHandItems = new HashMap<>();
        this.statManager = plugin.getStatManager();
        this.foragingAbilities = plugin.getAbilityManager().getAbilityImpl(ForagingAbilities.class);
        this.miningAbilities = plugin.getAbilityManager().getAbilityImpl(MiningAbilities.class);
        scheduleTask();
    }

    @EventHandler
    public void onJoin(UserLoadEvent event) {
        Player player = event.getPlayer();
        User user = BukkitUser.getUser(event.getUser());

        // Remove legacy stored item modifiers
        List<String> toRemove = new ArrayList<>();
        for (StatModifier modifier : user.getStatModifiers().values()) {
            if (modifier.name().startsWith("AureliumSkills.Modifier")) {
                toRemove.add(modifier.name());
            }
        }
        toRemove.forEach(user::removeStatModifier);
        
        ItemStack held = player.getInventory().getItemInMainHand();
        heldItems.put(player.getUniqueId(), held.clone());
      
        if (!held.getType().equals(Material.AIR)) {
            // Convert the held item
            if (plugin.configBoolean(Option.MODIFIER_AUTO_CONVERT_FROM_LEGACY)) {
                held = convertLegacyItem(held);
                if (!held.equals(player.getInventory().getItemInMainHand())) {
                    player.getInventory().setItemInMainHand(held);
                }
            }
            // Apply modifiers and multipliers
            SkillsItem skillsItem = new SkillsItem(held, plugin);
            for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
                user.addStatModifier(modifier, false);
            }
            for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ITEM)) {
                user.addTraitModifier(modifier, false);
            }
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                user.addMultiplier(multiplier);
            }
        }
        if (plugin.configBoolean(Option.MODIFIER_ITEM_ENABLE_OFF_HAND)) {
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            offHandItems.put(player.getUniqueId(), offHandItem.clone());
            if (!offHandItem.getType().equals(Material.AIR)) {
                if (plugin.configBoolean(Option.MODIFIER_AUTO_CONVERT_FROM_LEGACY)) {
                    offHandItem = convertLegacyItem(offHandItem);
                    if (!offHandItem.equals(player.getInventory().getItemInOffHand())) {
                        player.getInventory().setItemInOffHand(offHandItem);
                    }
                }
                applyOffhandModifiers(offHandItem, user);
            }
        }

    }

    private ItemStack convertLegacyItem(ItemStack item) {
        if (plugin.isNbtApiDisabled()) return item;

        SkillsItem skillsItem = new SkillsItem(item, plugin);
        NBT.modify(item, skillsItem::convertFromLegacy);
        item = skillsItem.getItem();

        NBT.modify(item, nbt -> {
            if (nbt.hasTag("AureliumSkills", NBTType.NBTTagCompound)) {
                nbt.removeKey("AureliumSkills");
            }
        });

        return item;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        heldItems.remove(player.getUniqueId());
        offHandItems.remove(player.getUniqueId());
    }

    public void scheduleTask() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Gets stored and held items
                    ItemStack held = player.getInventory().getItemInMainHand();
                    final ItemStack finalHeld = held;
                    ItemStack stored = heldItems.computeIfAbsent(player.getUniqueId(), id -> finalHeld.clone());
                    // If stored item is different from held
                    if (stored.equals(held)) {
                        continue;
                    }
                    Set<Stat> statsToReload = new HashSet<>();
                    Set<Trait> traitsToReload = new HashSet<>();
                    // Remove modifiers from stored item
                    if (!stored.getType().equals(Material.AIR)) {
                        User user = plugin.getUser(player);
                        SkillsItem storedItem = new SkillsItem(stored, plugin);

                        for (StatModifier modifier : storedItem.getStatModifiers(ModifierType.ITEM)) {
                            user.removeStatModifier(modifier.name(), false);
                            statsToReload.add(modifier.stat());
                        }
                        for (TraitModifier modifier : storedItem.getTraitModifiers(ModifierType.ITEM)) {
                            user.removeTraitModifier(modifier.name(), false);
                            traitsToReload.add(modifier.trait());
                        }
                        for (Multiplier multiplier : storedItem.getMultipliers(ModifierType.ITEM)) {
                            user.removeMultiplier(multiplier.name());
                        }
                        // Remove valor
                        if (ItemUtils.isAxe(stored.getType())) {
                            foragingAbilities.removeValor(user);
                        }
                        // Remove stamina
                        if (ItemUtils.isPickaxe(stored.getType())) {
                            miningAbilities.removeStamina(user);
                        }
                    }
                    // Add modifiers from held item
                    if (!held.getType().equals(Material.AIR)) {
                        if (plugin.configBoolean(Option.MODIFIER_AUTO_CONVERT_FROM_LEGACY)) {
                            held = convertLegacyItem(held);
                            if (!held.equals(player.getInventory().getItemInMainHand())) {
                                player.getInventory().setItemInMainHand(held);
                            }
                        }
                        User user = plugin.getUser(player);
                        SkillsItem heldItem = new SkillsItem(held, plugin);
                        if (heldItem.meetsRequirements(ModifierType.ITEM, player)) {
                            for (StatModifier modifier : heldItem.getStatModifiers(ModifierType.ITEM)) {
                                user.addStatModifier(modifier, false);
                                statsToReload.add(modifier.stat());
                            }
                            for (TraitModifier modifier : heldItem.getTraitModifiers(ModifierType.ITEM)) {
                                user.addTraitModifier(modifier, false);
                                traitsToReload.add(modifier.trait());
                            }
                            for (Multiplier multiplier : heldItem.getMultipliers(ModifierType.ITEM)) {
                                user.addMultiplier(multiplier);
                            }
                        }
                        // Apply valor
                        if (ItemUtils.isAxe(held.getType())) {
                            foragingAbilities.applyValor(user);
                        }
                        // Apply stamina
                        if (ItemUtils.isPickaxe(held.getType())) {
                            miningAbilities.applyStamina(player, user);
                        }
                    }
                    for (Stat stat : statsToReload) {
                        statManager.reloadStat(plugin.getUser(player), stat);
                    }
                    for (Trait trait : traitsToReload) {
                        statManager.reload(plugin.getUser(player), trait);
                    }
                    // Set stored item to held item
                    heldItems.put(player.getUniqueId(), held.clone());
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0L, plugin.configInt(Option.MODIFIER_ITEM_CHECK_PERIOD) * 50L, TimeUnit.MILLISECONDS);
        scheduleOffHandTask();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!plugin.configBoolean(Option.MODIFIER_ITEM_ENABLE_OFF_HAND)) {
            return;
        }
        Player player = event.getPlayer();
        User playerData = plugin.getUser(player);

        // Get items switched
        ItemStack itemOffHand = event.getOffHandItem();
        ItemStack itemMainHand = event.getMainHandItem();
        // Update items
        offHandItems.put(player.getUniqueId(), itemOffHand);
        heldItems.put(player.getUniqueId(), itemMainHand);
        // Things to prevent double reloads
        Set<String> offHandModifiers = new HashSet<>();
        Set<Stat> statsToReload = new HashSet<>();
        Set<Trait> traitsToReload = new HashSet<>();
        Set<String> offHandMultipliers = new HashSet<>();
        // Check offhand item
        if (itemOffHand != null && itemOffHand.getType() != Material.AIR) {
            SkillsItem skillsItem = new SkillsItem(itemOffHand, plugin);
            boolean meetsRequirements = skillsItem.meetsRequirements(ModifierType.ITEM, player); // Get whether player meets requirements
            // For each modifier on the item
            for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
                // Removes the old modifier from main hand
                StatModifier offHandModifier = new StatModifier(modifier.name() + ".Offhand", modifier.stat(), modifier.value());
                playerData.removeStatModifier(modifier.name(), false);
                // Add new one if meets requirements
                if (meetsRequirements) {
                    playerData.addStatModifier(offHandModifier, false);
                }
                // Reload check stuff
                offHandModifiers.add(offHandModifier.name());
                statsToReload.add(modifier.stat());
            }
            for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ITEM)) {
                TraitModifier offHandModifier = new TraitModifier(modifier.name() + ".Offhand", modifier.trait(), modifier.value());
                playerData.removeTraitModifier(modifier.name(), false);
                // Add new one if meets requirements
                if (meetsRequirements) {
                    playerData.addTraitModifier(offHandModifier, false);
                }
                // Reload check stuff
                offHandModifiers.add(offHandModifier.name());
                traitsToReload.add(modifier.trait());
            }
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                Multiplier offHandMultiplier = new Multiplier(multiplier.name() + ".Offhand", multiplier.skill(), multiplier.value());
                playerData.removeMultiplier(multiplier.name());
                if (meetsRequirements) {
                    playerData.addMultiplier(offHandMultiplier);
                }
                offHandMultipliers.add(offHandMultiplier.name());
            }
        }
        // Check main hand item
        if (itemMainHand != null && itemMainHand.getType() != Material.AIR) {
            SkillsItem skillsItem = new SkillsItem(itemMainHand, plugin);
            boolean meetsRequirements = skillsItem.meetsRequirements(ModifierType.ITEM, player); // Get whether player meets requirements
            // For each modifier on the item
            for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
                // Removes the offhand modifier if wasn't already added
                if (!offHandModifiers.contains(modifier.name() + ".Offhand")) {
                    playerData.removeStatModifier(modifier.name() + ".Offhand", false);
                }
                // Add if meets requirements
                if (meetsRequirements) {
                    playerData.addStatModifier(modifier, false);
                }
                // Reload check stuff
                statsToReload.add(modifier.stat());
            }
            // For each modifier on the item
            for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ITEM)) {
                // Removes the offhand modifier if wasn't already added
                if (!offHandModifiers.contains(modifier.name() + ".Offhand")) {
                    playerData.removeTraitModifier(modifier.name() + ".Offhand", false);
                }
                // Add if meets requirements
                if (meetsRequirements) {
                    playerData.addTraitModifier(modifier, false);
                }
                // Reload check stuff
                traitsToReload.add(modifier.trait());
            }
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
                if (!offHandMultipliers.contains(multiplier.name() + ".Offhand")) {
                    playerData.removeMultiplier(multiplier.name() + ".Offhand");
                }
                if (meetsRequirements) {
                    playerData.addMultiplier(multiplier);
                }
            }
        }
        // Reload stats
        for (Stat stat : statsToReload) {
            statManager.reloadStat(plugin.getUser(player), stat);
        }
        for (Trait trait : traitsToReload) {
            statManager.reload(plugin.getUser(player), trait);
        }
    }

    public void scheduleOffHandTask() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!plugin.configBoolean(Option.MODIFIER_ITEM_ENABLE_OFF_HAND)) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Gets stored and held items
                    ItemStack held = player.getInventory().getItemInOffHand();
                    ItemStack stored = offHandItems.computeIfAbsent(player.getUniqueId(), id -> held.clone());
                    // If stored item is different than held
                    if (!stored.equals(held)) {
                        //Remove modifiers from stored item
                        if (!stored.getType().equals(Material.AIR)) {
                            User playerData = plugin.getUser(player);
                            SkillsItem storedItem = new SkillsItem(stored, plugin);
                            for (StatModifier modifier : storedItem.getStatModifiers(ModifierType.ITEM)) {
                                playerData.removeStatModifier(modifier.name() + ".Offhand");
                            }
                            for (TraitModifier modifier : storedItem.getTraitModifiers(ModifierType.ITEM)) {
                                playerData.removeTraitModifier(modifier.name() + ".Offhand");
                            }
                            for (Multiplier multiplier : storedItem.getMultipliers(ModifierType.ITEM)) {
                                playerData.removeMultiplier(multiplier.name() + ".Offhand");
                            }
                        }
                        // Add modifiers from held item
                        if (!held.getType().equals(Material.AIR)) {
                            SkillsItem heldItem = new SkillsItem(held, plugin);
                            if (heldItem.meetsRequirements(ModifierType.ITEM, player)) {
                                applyOffhandModifiers(held, plugin.getUser(player));
                            }
                        }
                        // Set stored item to held item
                        offHandItems.put(player.getUniqueId(), held.clone());
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0L, plugin.configInt(Option.MODIFIER_ITEM_CHECK_PERIOD) * 50L, TimeUnit.MILLISECONDS);
    }

    private void applyOffhandModifiers(ItemStack held, User user) {
        SkillsItem skillsItem = new SkillsItem(held, plugin);
        for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ITEM)) {
            StatModifier offHandModifier = new StatModifier(modifier.name() + ".Offhand", modifier.stat(), modifier.value());
            user.addStatModifier(offHandModifier);
        }
        for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ITEM)) {
            TraitModifier offHandModifier = new TraitModifier(modifier.name() + ".Offhand", modifier.trait(), modifier.value());
            user.addTraitModifier(offHandModifier);
        }
        for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ITEM)) {
            Multiplier offHandMultiplier = new Multiplier(multiplier.name() + ".Offhand", multiplier.skill(), multiplier.value());
            user.addMultiplier(offHandMultiplier);
        }
    }

}
