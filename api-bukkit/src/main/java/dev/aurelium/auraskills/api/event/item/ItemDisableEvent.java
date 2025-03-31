package dev.aurelium.auraskills.api.event.item;

import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.stat.ReloadableIdentifier;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ItemDisableEvent extends ItemToggleEvent {

    public ItemDisableEvent(Player player, SkillsUser user, ItemStack item, ModifierType type, @Nullable EquipmentSlot slot, Set<ReloadableIdentifier> toReload) {
        super(player, user, item, type, slot, toReload);
    }

}
