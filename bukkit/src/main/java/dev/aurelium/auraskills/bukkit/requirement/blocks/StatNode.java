package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class StatNode extends RequirementNode {

    private final Stat stat;
    private final int value;

    public StatNode(AuraSkills plugin, Stat stat, int value, String message) {
        super(plugin, message);
        this.stat = stat;
        this.value = value;
    }

    @Override
    public boolean check(Player player) {
        return plugin.getUser(player).getStatLevel(stat) >= value;
    }

}
