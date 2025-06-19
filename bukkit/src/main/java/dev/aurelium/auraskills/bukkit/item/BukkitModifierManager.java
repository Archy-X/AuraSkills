package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.stat.ReloadableIdentifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.modifier.ModifierManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class BukkitModifierManager implements ModifierManager {

    private final AuraSkills plugin;
    private final ItemStateManager manager;

    public BukkitModifierManager(AuraSkills plugin) {
        this.plugin = plugin;
        this.manager = new ItemStateManager(plugin);
    }

    public void applyModifiers(Player player, boolean reload) {
        User user = plugin.getUser(player);

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        var toReloadMain = manager.changeItemInSlot(user, player, mainHand, EquipmentSlot.HAND, false, true, false);
        Set<ReloadableIdentifier> toReload = new HashSet<>(toReloadMain);

        ItemStack itemOffHand = player.getInventory().getItemInOffHand();
        toReload.addAll(manager.changeItemInSlot(user, player, itemOffHand, EquipmentSlot.OFF_HAND, false, true, false));

        EntityEquipment equipment = player.getEquipment();
        if (equipment != null) {
            for (var slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack armor = equipment.getItem(slot);
                if (armor == null) continue;

                if (armor.getType() == Material.AIR) {
                    continue;
                }

                toReload.addAll(manager.changeItemInSlot(user, player, armor, slot, false, true, false));
            }
        }

        if (reload) {
            manager.reloadIdentifiers(user, toReload);
        }

    }

    @Override
    public void applyModifiers(User user, boolean reload) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player != null) {
            applyModifiers(player, reload);
        }
    }

}
