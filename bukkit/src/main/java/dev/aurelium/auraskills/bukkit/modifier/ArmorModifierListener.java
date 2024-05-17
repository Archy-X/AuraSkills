package dev.aurelium.auraskills.bukkit.modifier;

import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.SkillsItem;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.armor.ArmorEquipEvent;
import dev.aurelium.auraskills.bukkit.util.armor.ArmorType;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.api.skill.Multiplier;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ArmorModifierListener implements Listener {

    private final AuraSkills plugin;
    private final StatManager statManager;
    private final Map<UUID, Map<ArmorType, ItemStack>> storedArmor;

    public ArmorModifierListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.statManager = plugin.getStatManager();
        this.storedArmor = new HashMap<>();
        if (plugin.configBoolean(Option.MODIFIER_ARMOR_TIMER_ENABLED)) {
            startTimer();
        }
    }

    @EventHandler
    public void onJoin(UserLoadEvent event) {
        Player player = event.getPlayer();
        User user = BukkitUser.getUser(event.getUser());
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null) {
                continue;
            }
            if (plugin.configBoolean(Option.MODIFIER_ARMOR_TIMER_ENABLED)) {
                storedArmor.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(ArmorType.matchType(armor), armor.clone());
            }
            if (armor.getType().equals(Material.AIR)) {
                continue;
            }
            SkillsItem skillsItem = new SkillsItem(armor, plugin);
            if (skillsItem.meetsRequirements(ModifierType.ARMOR, player)) {
                for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ARMOR)) {
                    user.addStatModifier(modifier, false);
                }
                for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ARMOR)) {
                    user.addTraitModifier(modifier, false);
                }
                for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                    user.addMultiplier(multiplier);
                }
            }
        }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        if (event.isCancelled()) return;
        if (plugin.configBoolean(Option.MODIFIER_ARMOR_TIMER_ENABLED)) return; // Don't use if timer is enabled
        Player player = event.getPlayer();
        User user = plugin.getUser(player);
        // Equip
        if (event.getNewArmorPiece() != null && event.getNewArmorPiece().getType() != Material.AIR) {
            ItemStack item = event.getNewArmorPiece();
            SkillsItem skillsItem = new SkillsItem(item, plugin);
            if (skillsItem.meetsRequirements(ModifierType.ARMOR, player)) {
                for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ARMOR)) {
                    user.addStatModifier(modifier);
                }
                for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ARMOR)) {
                    user.addTraitModifier(modifier);
                }
                for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                    user.addMultiplier(multiplier);
                }
            }
        }
        // Un-equip
        if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
            ItemStack item = event.getOldArmorPiece();
            SkillsItem skillsItem = new SkillsItem(item, plugin);
            for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ARMOR)) {
                user.removeStatModifier(modifier.name());
            }
            for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ARMOR)) {
                user.removeTraitModifier(modifier.name());
            }
            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                user.removeMultiplier(multiplier.name());
            }
        }
    }

    // Timer based detection
    private void startTimer() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    // Get the stored armor player has or create if it doesn't exist
                    Map<ArmorType, ItemStack> playerStoredArmor = storedArmor.computeIfAbsent(uuid, k -> new HashMap<>());
                    for (ArmorType armorType : ArmorType.values()) { // Go through each armor slot
                        ItemStack stored = playerStoredArmor.get(armorType); // Get the stored item in the slot
                        ItemStack wearing = player.getInventory().getItem(armorType.getEquipmentSlot()); // Get the armor player is currently wearing

                        boolean remove = true;
                        if (stored == null) {
                            remove = false;
                        } else if (stored.equals(wearing)) { // Don't check if stored and wearing are the same item
                            continue;
                        }

                        Set<Stat> statsToReload = new HashSet<>();
                        Set<Trait> traitsToReload = new HashSet<>();
                        // Remove modifiers and multipliers that are on stored item from player
                        if (remove && stored.getType() != Material.AIR) {
                            User user = plugin.getUser(player);
                            SkillsItem skillsItem = new SkillsItem(stored, plugin);

                            for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ARMOR)) {
                                user.removeStatModifier(modifier.name(), false);
                                statsToReload.add(modifier.stat());
                            }
                            for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ARMOR)) {
                                user.addTraitModifier(modifier, false);
                                traitsToReload.add(modifier.trait());
                            }
                            for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                                user.removeMultiplier(multiplier.name());
                            }
                        }
                        // Add modifiers and multipliers that are on worn item to the player
                        if (wearing != null && wearing.getType() != Material.AIR) {
                            User user = plugin.getUser(player);
                            SkillsItem skillsItem = new SkillsItem(wearing, plugin);

                            if (skillsItem.meetsRequirements(ModifierType.ARMOR, player)) {
                                for (StatModifier modifier : skillsItem.getStatModifiers(ModifierType.ARMOR)) {
                                    user.addStatModifier(modifier, false);
                                    statsToReload.add(modifier.stat());
                                }
                                for (TraitModifier modifier : skillsItem.getTraitModifiers(ModifierType.ARMOR)) {
                                    user.addTraitModifier(modifier, false);
                                    traitsToReload.add(modifier.trait());
                                }
                                for (Multiplier multiplier : skillsItem.getMultipliers(ModifierType.ARMOR)) {
                                    user.addMultiplier(multiplier);
                                }
                            }
                        }
                        for (Stat stat : statsToReload) {
                            statManager.reloadStat(plugin.getUser(player), stat);
                        }
                        for (Trait trait : traitsToReload) {
                            statManager.reload(plugin.getUser(player), trait);
                        }
                        // Set stored item to worn item
                        if (wearing != null) {
                            playerStoredArmor.put(armorType, wearing.clone());
                        } else {
                            playerStoredArmor.put(armorType, new ItemStack(Material.AIR));
                        }
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0L, plugin.configInt(Option.MODIFIER_ARMOR_TIMER_CHECK_PERIOD) * 50L, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        storedArmor.remove(event.getPlayer().getUniqueId());
    }

}
