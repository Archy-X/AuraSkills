package io.github.archy_x.aureliumskills.skills.abilities.mana_abilities;

import org.bukkit.entity.Player;

public interface ManaAbility {

    void activate(Player player);
    void update(Player player);
    void stop(Player player);
}
