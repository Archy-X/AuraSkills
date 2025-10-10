package dev.aurelium.auraskills.api.event.mana;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TerraformBlockBreakEvent extends ManaAbilityBlockBreakEvent {

    public TerraformBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
    }

}
