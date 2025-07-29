package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ItemNode extends RequirementNode {

    private final String item;

    public ItemNode(AuraSkills plugin, String item, String message) {
        super(plugin, message);
        this.item = item.toUpperCase(Locale.ROOT);
    }

    @Override
    public boolean check(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Material heldMaterial = heldItem != null ? heldItem.getType() : null;
        Material material = Material.getMaterial(item);

        if (material != null && heldMaterial != material) {
            return false;
        }

        return true;
    }

}
