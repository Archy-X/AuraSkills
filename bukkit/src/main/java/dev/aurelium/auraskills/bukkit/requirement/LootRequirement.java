package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.bukkit.entity.Player;

import java.util.List;

public class LootRequirement {

    private final NamespacedId id;
    private final List<RequirementNode> nodes;

    public LootRequirement(NamespacedId id, List<RequirementNode> nodes) {
        this.id = id;
        this.nodes = nodes;
    }

    public NamespacedId getId() {
        return id;
    }

    public boolean check(Player player) {
        for (RequirementNode node : nodes) {
            if (!node.check(player))
                return false;
        }

        return true;
    }

}
