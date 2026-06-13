package dev.aurelium.auraskills.api.event.mana;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManaAbilityBlockDropItemEvent extends BlockDropItemEvent {

    public ManaAbilityBlockDropItemEvent(@NotNull Block theBlock, @NotNull BlockState theBlockState, @NotNull Player player, @NotNull List<Item> items) {
        super(theBlock, theBlockState, player, items);
    }

}
