package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantmentNode extends RequirementNode {

    private final Enchantment enchantment;
    private final int levelMin;
    private final int levelMax;

    public EnchantmentNode(AuraSkills plugin, String enchantment, int levelMin, int levelMax, String message) {
        super(plugin, message);

        NamespacedKey enchantmentKey = NamespacedKey.minecraft(enchantment);
        this.enchantment = enchantmentKey != null ? Registry.ENCHANTMENT.get(enchantmentKey) : null;

        this.levelMin = levelMin;
        this.levelMax = levelMax;
    }

    @Override
    public boolean check(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        // In the case the given enchantment wasn't resolvable.
        if (enchantment == null) {
            return true;
        }

        if (!heldItem.hasItemMeta() || !heldItem.getItemMeta().hasEnchants() || !heldItem.getItemMeta().hasEnchant(enchantment)) {
            return false;
        } else if (levelMin >= 0) {
            int enchantmentLevel = heldItem.getEnchantmentLevel(enchantment);
            if (levelMin == levelMax && enchantmentLevel != levelMin) {
                return false;
            }
            if (enchantmentLevel < levelMin) {
                return false;
            }
            if (levelMax >= 0 && enchantmentLevel > levelMax) {
                return false;
            }
        }

        return true;
    }

}
