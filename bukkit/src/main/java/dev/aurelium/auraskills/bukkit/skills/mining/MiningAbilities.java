package dev.aurelium.auraskills.bukkit.skills.mining;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class MiningAbilities extends AbilityImpl {

    public MiningAbilities(AuraSkills plugin) {
        super(plugin, Abilities.LUCKY_MINER, Abilities.MINER, Abilities.PICK_MASTER, Abilities.HARDENED_ARMOR, Abilities.STAMINA);
    }

    public void luckyMiner(Player player, User user, Block block) {
        var ability = Abilities.LUCKY_MINER;

        if (isDisabled(ability)) return;

        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            Material mat = block.getType();
            if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                if (mat.equals(Material.DIAMOND_ORE) || mat.equals(Material.LAPIS_ORE) ||
                        mat.equals(Material.REDSTONE_ORE) || mat.name().equals("GLOWING_REDSTONE_ORE") ||
                        mat.equals(Material.EMERALD_ORE) || mat.equals(Material.COAL_ORE) ||
                        mat.equals(Material.NETHER_QUARTZ_ORE) || mat.equals(Material.NETHER_GOLD_ORE)) {
                    return;
                }
                if (VersionUtils.isAtLeastVersion(17)) {
                    if (mat == Material.IRON_ORE || mat == Material.GOLD_ORE || mat == Material.COPPER_ORE ||
                            mat.toString().contains("DEEPSLATE_")) {
                        return;
                    }
                }
            }
            Collection<ItemStack> drops = block.getDrops(tool);
            for (ItemStack item : drops) {
                Location location = block.getLocation().add(0.5, 0.5, 0.5);
                LootDropEvent event = new LootDropEvent(player, user.toApi(), item.clone(), location, LootDropEvent.Cause.LUCKY_MINER);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    block.getWorld().dropItem(event.getLocation(), event.getItem());
                }
            }
        }
    }

    public void pickMaster(EntityDamageByEntityEvent event, Player player, User user) {
        var ability = Abilities.PICK_MASTER;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        event.setDamage(event.getDamage() * (1 + (getValue(ability, user) / 100)));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void hardenedArmor(PlayerItemDamageEvent event) {
        var ability = Abilities.HARDENED_ARMOR;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();

        // Checks if item damaged is armor
        if (!ItemUtils.isArmor(event.getItem().getType())) return;

        User user = plugin.getUser(player);

        if (failsChecks(player, ability)) return;

        // Applies ability
        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            event.setCancelled(true);
        }
    }

    public void applyStamina(Player player, User user) {
        var ability = Abilities.STAMINA;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        user.addStatModifier(new StatModifier("mining-stamina", Stats.TOUGHNESS, (int) getValue(ability, user)));
    }

    public void removeStamina(User user) {
        user.removeStatModifier("mining-stamina");
    }

}
