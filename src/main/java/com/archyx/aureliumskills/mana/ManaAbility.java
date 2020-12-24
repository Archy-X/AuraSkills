package com.archyx.aureliumskills.mana;

import org.bukkit.entity.Player;

public interface ManaAbility {

    void activate(Player player);
    void update(Player player);
    void stop(Player player);
}
