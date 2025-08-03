package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.loot.LootRequirements;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BukkitLootRequirements extends LootRequirements {

    private final List<RequirementNode> nodes;

    public BukkitLootRequirements(List<RequirementNode> nodes) {
        this.nodes = nodes;
    }

    public boolean checkByUuid(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;

        return check(player);
    }

    public boolean check(Player player) {
        for (RequirementNode node : nodes) {
            if (!node.check(player))
                return false;
        }

        return true;
    }

}
