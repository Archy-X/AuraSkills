package dev.aurelium.auraskills.bukkit.item;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserEquipment {

    @NotNull
    private ItemStack mainHand;
    @NotNull
    private ItemStack offHand;
    @NotNull
    private ItemStack head;
    @NotNull
    private ItemStack chest;
    @NotNull
    private ItemStack legs;
    @NotNull
    private ItemStack feet;

    private UserEquipment(
            @NotNull ItemStack mainHand,
            @NotNull ItemStack offHand,
            @NotNull ItemStack head,
            @NotNull ItemStack chest,
            @NotNull ItemStack legs,
            @NotNull ItemStack feet
    ) {
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
    }

    public static UserEquipment empty() {
        return new UserEquipment(
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR)
        );
    }

    @NotNull
    public ItemStack getSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HAND -> mainHand;
            case OFF_HAND -> offHand;
            case HEAD -> head;
            case CHEST -> chest;
            case LEGS -> legs;
            case FEET -> feet;
            default -> new ItemStack(Material.AIR);
        };
    }

    public void setSlot(EquipmentSlot slot, @NotNull ItemStack item) {
        validate(item);
        switch (slot) {
            case HAND -> mainHand = item;
            case OFF_HAND -> offHand = item;
            case HEAD -> head = item;
            case CHEST -> chest = item;
            case LEGS -> legs = item;
            case FEET -> feet = item;
        }
    }

    private void validate(ItemStack item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot set a UserEquipment ItemStack to null!");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UserEquipment) obj;
        return Objects.equals(this.mainHand, that.mainHand) &&
                Objects.equals(this.offHand, that.offHand) &&
                Objects.equals(this.head, that.head) &&
                Objects.equals(this.chest, that.chest) &&
                Objects.equals(this.legs, that.legs) &&
                Objects.equals(this.feet, that.feet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainHand, offHand, head, chest, legs, feet);
    }

    @Override
    public String toString() {
        return "UserEquipment[" +
                "mainHand=" + mainHand + ", " +
                "offHand=" + offHand + ", " +
                "head=" + head + ", " +
                "chest=" + chest + ", " +
                "legs=" + legs + ", " +
                "feet=" + feet + ']';
    }

}
