package dev.aurelium.auraskills.bukkit.skills.excavation;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcavationAbilities extends AbilityImpl {

    public ExcavationAbilities(AuraSkills plugin) {
        super(plugin, Abilities.METAL_DETECTOR, Abilities.EXCAVATOR, Abilities.SPADE_MASTER, Abilities.BIGGER_SCOOP, Abilities.LUCKY_SPADES);
    }

    public DamageModifier spadeMaster(Player player, User user) {
        var ability = Abilities.SPADE_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    public void biggerScoop(Player player, User user, Block block) {
        var ability = Abilities.BIGGER_SCOOP;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (player.getGameMode() != GameMode.SURVIVAL) return;

        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            Material mat =  block.getType();
            for (ItemStack item : block.getDrops(tool)) {
                ItemStack drop;
                if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) {
                    // Drop the material of the block if silk touch
                    drop = new ItemStack(mat, 2);
                } else {
                    // Drop the normal block drops if not silk touch
                    drop = item.clone();
                    drop.setAmount(2);
                }
                Location loc = block.getLocation().add(0.5, 0.5, 0.5);

                boolean toInventory = ItemUtils.hasTelekinesis(player.getInventory().getItemInMainHand());
                LootDropEvent event = new LootDropEvent(player, user.toApi(), drop, loc, LootDropEvent.Cause.BIGGER_SCOOP, toInventory);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    ItemUtils.giveBlockLoot(player, event);
                }
            }
        }
    }

}
