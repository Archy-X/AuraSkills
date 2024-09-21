package dev.aurelium.auraskills.bukkit.skills.enchanting;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EnchantingAbilities extends AbilityImpl {
    
    public EnchantingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.XP_CONVERT, Abilities.ENCHANTER, Abilities.XP_WARRIOR, Abilities.ENCHANTED_STRENGTH, Abilities.LUCKY_TABLE);
        enchantedStrength();
    }

    @EventHandler
    public void xpConvert(XpGainEvent event) {
        var ability = Abilities.XP_CONVERT;
        
        if (isDisabled(ability)) return;
        
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        
        if (failsChecks(player, ability)) return;

        User user = BukkitUser.getUser(event.getUser());

        if (!(event.getAmount() > 0)) return;

        double value = getValue(ability, user);
        double prevData = Math.min(user.getAbilityData(ability).getDouble("xp"), value);
        double eventAmount = event.getAmount();
        double totalXp = prevData + eventAmount;
        if (value > 0) {
            int added = (int) (totalXp / value);
            double remainder = totalXp - added * value;
            player.giveExp(Math.max(added, 0));
            user.getAbilityData(ability).setData("xp", Math.min(remainder, value));
        }
    }

    @EventHandler
    public void xpWarrior(EntityDeathEvent event) {
        var ability = Abilities.XP_WARRIOR;

        if (isDisabled(ability)) return;

        LivingEntity entity = event.getEntity();
        if (entity.getKiller() == null) return;

        Player player = entity.getKiller();

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);
        if (event.getDroppedExp() <= 0) {
            return;
        }

        if (rand.nextDouble() < getValue(ability, user) / 100) {
            event.setDroppedExp(event.getDroppedExp() * 2);
        }
    }

    private void enchantedStrength() {
        var ability = Abilities.ENCHANTED_STRENGTH;
        String MODIFIER_NAME = "AbilityModifier-EnchantedStrength";
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (isDisabled(ability)) return;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    User user = plugin.getUser(player);

                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (!item.getEnchantments().isEmpty()) {
                        if (failsChecks(player, ability)) continue;

                        // Apply modifier
                        double strengthPerType = getValue(ability, user);
                        int enchantCount = 0;
                        for (Enchantment enchantment : item.getEnchantments().keySet()) {
                            if (ability.optionStringList("excluded_enchantments").contains(enchantment.getKey().getKey())) {
                                continue;
                            }
                            enchantCount++;
                        }
                        if (enchantCount > 0) {
                            StatModifier modifier = new StatModifier(MODIFIER_NAME, Stats.STRENGTH, strengthPerType * enchantCount);
                            user.addStatModifier(modifier, false);
                        }
                    } else {
                        user.removeStatModifier(MODIFIER_NAME);
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 50, 10 * 50, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void luckyTable(EnchantItemEvent event) {
        var ability = Abilities.LUCKY_TABLE;

        if (event.isCancelled()) return;

        if (isDisabled(ability)) return;

        Player player = event.getEnchanter();

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        for (Map.Entry<Enchantment, Integer> entry : event.getEnchantsToAdd().entrySet()) {
            if (entry.getKey().getMaxLevel() > entry.getValue()) { // Make sure enchant isn't already maxed
                if (rand.nextDouble() < getValue(ability, user) / 100) {
                    entry.setValue(entry.getValue() + 1); // Increase enchant level by 1
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("removal")
    public void anvilMaster(InventoryOpenEvent event) {
        var ability = Abilities.ANVIL_MASTER;

        if (event.isCancelled()) return;

        if (isDisabled(ability)) return;

        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.ANVIL || !(inventory instanceof AnvilInventory anvil)) {
            return;
        }

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        int maxCost = (int) Math.round(getValue(ability, user));

        if (VersionUtils.isAtLeastVersion(21, 1)) {
            if (event.getView() instanceof AnvilView view) {
                view.setMaximumRepairCost(maxCost);
            }
        } else {
            anvil.setMaximumRepairCost(maxCost);
        }
    }

}
