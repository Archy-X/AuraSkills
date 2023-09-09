package dev.aurelium.auraskills.bukkit.modifier;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import dev.aurelium.auraskills.api.event.AuraSkillsEventHandler;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.requirement.Requirements;
import dev.aurelium.auraskills.bukkit.skills.foraging.ForagingAbilities;
import dev.aurelium.auraskills.bukkit.skills.mining.MiningAbilities;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.modifier.Multiplier;
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
    private final Modifiers modifiers;
    private final Requirements requirements;
    private final Multipliers multipliers;
    private final ForagingAbilities foragingAbilities;
    private final MiningAbilities miningAbilities;

    public ItemListener(AuraSkills plugin) {
        this.plugin = plugin;
        heldItems = new HashMap<>();
        offHandItems = new HashMap<>();
        this.statManager = plugin.getStatManager();
        this.modifiers = new Modifiers(plugin);
        this.requirements = new Requirements(plugin);
        this.multipliers = new Multipliers(plugin);
        this.foragingAbilities = plugin.getAbilityManager().getAbilityImpl(ForagingAbilities.class);
        this.miningAbilities = plugin.getAbilityManager().getAbilityImpl(MiningAbilities.class);
        scheduleTask();
    }

    @AuraSkillsEventHandler
    public void onJoin(UserLoadEvent event) {
        Player player = BukkitUser.getPlayer(event.getUser());
        User user = BukkitUser.getUser(event.getUser());
        
        ItemStack held = player.getInventory().getItemInMainHand();
        heldItems.put(player.getUniqueId(), held.clone());
      
        if (!held.getType().equals(Material.AIR)) {
            if (plugin.configBoolean(Option.MODIFIER_AUTO_CONVERT_FROM_LEGACY)) {
                held = convertLegacyItem(held);
                if (!held.equals(player.getInventory().getItemInMainHand())) {
                    player.getInventory().setItemInMainHand(held);
                }
            }
            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, held)) {
                user.addStatModifier(modifier, false);
            }
            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, held)) {
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
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, offHandItem)) {
                    StatModifier offHandModifier = new StatModifier(modifier.name() + ".Offhand", modifier.stat(), modifier.value());
                    user.addStatModifier(offHandModifier);
                }
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, offHandItem)) {
                    Multiplier offHandMultiplier = new Multiplier(multiplier.name() + ".Offhand", multiplier.skill(), multiplier.value());
                    user.addMultiplier(offHandMultiplier);
                }
            }
        }

    }

    private ItemStack convertLegacyItem(ItemStack item) {
        item = modifiers.convertFromLegacy(item);
        item = requirements.convertFromLegacy(item);
        item = multipliers.convertFromLegacy(item);

        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasTag("AureliumSkills", NBTType.NBTTagCompound)) {
            nbtItem.removeKey("AureliumSkills");
            item = nbtItem.getItem();
        }

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
                    // If stored item is different than held
                    if (stored.equals(held)) {
                        continue;
                    }
                    Set<Stat> statsToReload = new HashSet<>();
                    // Remove modifiers from stored item
                    if (!stored.getType().equals(Material.AIR)) {
                        User user = plugin.getUser(player);

                        for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, stored)) {
                            user.removeStatModifier(modifier.name(), false);
                            statsToReload.add(modifier.stat());
                        }
                        for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, stored)) {
                            user.removeMultiplier(multiplier.name());
                        }
                        // Remove valor
                        if (ItemUtils.isAxe(stored.getType())) {
                            plugin.getAbilityManager().getAbilityImpl(ForagingAbilities.class).removeValor(user);
                        }
                        // Remove stamina
                        if (ItemUtils.isPickaxe(stored.getType())) {
                            plugin.getAbilityManager().getAbilityImpl(MiningAbilities.class).removeStamina(user);
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
                        if (requirements.meetsRequirements(ModifierType.ITEM, held, player)) {
                            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, held)) {
                                user.addStatModifier(modifier, false);
                                statsToReload.add(modifier.stat());
                            }
                            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, held)) {
                                user.addMultiplier(multiplier);
                            }
                        }
                        // Apply valor
                        if (ItemUtils.isAxe(held.getType())) {
                            foragingAbilities.removeValor(user);
                        }
                        // Apply stamina
                        if (ItemUtils.isPickaxe(held.getType())) {
                            miningAbilities.removeStamina(user);
                        }
                    }
                    for (Stat stat : statsToReload) {
                        statManager.reloadStat(plugin.getUser(player), stat);
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
        Set<String> offHandMultipliers = new HashSet<>();
        // Check off hand item
        if (itemOffHand != null) {
            if (itemOffHand.getType() != Material.AIR) {
                boolean meetsRequirements = requirements.meetsRequirements(ModifierType.ITEM, itemOffHand, player); // Get whether player meets requirements
                // For each modifier on the item
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
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
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, itemOffHand)) {
                    Multiplier offHandMultiplier = new Multiplier(multiplier.name() + ".Offhand", multiplier.skill(), multiplier.value());
                    playerData.removeMultiplier(multiplier.name());
                    if (meetsRequirements) {
                        playerData.addMultiplier(offHandMultiplier);
                    }
                    offHandMultipliers.add(offHandMultiplier.name());
                }
            }
        }
        // Check main hand item
        if (itemMainHand != null) {
            if (itemMainHand.getType() != Material.AIR) {
                boolean meetsRequirements = requirements.meetsRequirements(ModifierType.ITEM, itemMainHand, player); // Get whether player meets requirements
                // For each modifier on the item
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemMainHand)) {
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
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, itemMainHand)) {
                    if (!offHandMultipliers.contains(multiplier.name() + ".Offhand")) {
                        playerData.removeMultiplier(multiplier.name() + ".Offhand");
                    }
                    if (meetsRequirements) {
                        playerData.addMultiplier(multiplier);
                    }
                }
            }
        }
        // Reload stats
        for (Stat stat : statsToReload) {
            statManager.reloadStat(plugin.getUser(player), stat);
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
                            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, stored)) {
                                playerData.removeStatModifier(modifier.name() + ".Offhand");
                            }
                            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, stored)) {
                                playerData.removeMultiplier(multiplier.name() + ".Offhand");
                            }
                        }
                        // Add modifiers from held item
                        if (!held.getType().equals(Material.AIR)) {
                            User playerData = plugin.getUser(player);
                            if (requirements.meetsRequirements(ModifierType.ITEM, held, player)) {
                                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, held)) {
                                    StatModifier offHandModifier = new StatModifier(modifier.name() + ".Offhand", modifier.stat(), modifier.value());
                                    playerData.addStatModifier(offHandModifier);
                                }
                                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, held)) {
                                    Multiplier offHandMultiplier = new Multiplier(multiplier.name() + ".Offhand", multiplier.skill(), multiplier.value());
                                    playerData.addMultiplier(offHandMultiplier);
                                }
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

}
