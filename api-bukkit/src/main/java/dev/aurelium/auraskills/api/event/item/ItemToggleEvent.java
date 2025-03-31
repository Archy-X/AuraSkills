package dev.aurelium.auraskills.api.event.item;

import dev.aurelium.auraskills.api.item.ModifierType;
import dev.aurelium.auraskills.api.stat.ReloadableIdentifier;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ItemToggleEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser user;
    private final ItemStack item;
    private final ModifierType type;
    private final @Nullable EquipmentSlot slot;
    private final Set<ReloadableIdentifier> toReload;

    public ItemToggleEvent(Player player, SkillsUser user, ItemStack item, ModifierType type, @Nullable EquipmentSlot slot, Set<ReloadableIdentifier> toReload) {
        this.player = player;
        this.user = user;
        this.item = item;
        this.type = type;
        this.slot = slot;
        this.toReload = toReload;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public SkillsUser getUser() {
        return user;
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    @NotNull
    public ModifierType getType() {
        return type;
    }

    @Nullable
    public EquipmentSlot getSlot() {
        return slot;
    }

    @NotNull
    public Set<ReloadableIdentifier> getToReload() {
        return toReload;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
