package dev.aurelium.auraskills.bukkit.skills.mining;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
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
            // Don't give drops if Silk Touch is used
            if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0 && dropsMineralDirectly(block)) {
                return;
            }
            Collection<ItemStack> drops = block.getDrops(tool);
            for (ItemStack item : drops) {
                Location location = block.getLocation().add(0.5, 0.5, 0.5);

                boolean toInventory = ItemUtils.hasTelekinesis(player.getInventory().getItemInMainHand());
                LootDropEvent event = new LootDropEvent(player, user.toApi(), item.clone(), location, LootDropEvent.Cause.LUCKY_MINER, toInventory);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    ItemUtils.giveBlockLoot(player, event);
                }
            }
        }
    }

    public boolean dropsMineralDirectly(Block block) {
        Material mat = block.getType();
        switch (mat) {
            case DIAMOND_ORE, REDSTONE_ORE, EMERALD_ORE, COAL_ORE, LAPIS_ORE, NETHER_QUARTZ_ORE, NETHER_GOLD_ORE:
                return true;
        }
        if (VersionUtils.isAtLeastVersion(17)) {
            switch (mat) {
                case IRON_ORE, DEEPSLATE_IRON_ORE, GOLD_ORE, DEEPSLATE_GOLD_ORE, COPPER_ORE, DEEPSLATE_COPPER_ORE,
                        DEEPSLATE_DIAMOND_ORE, DEEPSLATE_REDSTONE_ORE, DEEPSLATE_EMERALD_ORE,
                        DEEPSLATE_COAL_ORE, DEEPSLATE_LAPIS_ORE:
                    return true;
            }
        }
        return false;
    }

    public DamageModifier pickMaster(Player player, User user) {
        var ability = Abilities.PICK_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
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
