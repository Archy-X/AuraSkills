package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import org.bukkit.entity.Player;

public interface ManaAbility {

    AureliumSkills getPlugin();

    MAbility getManaAbility();

    void activate(Player player);

    default void update(Player player) { }

    default void stop(Player player) {}

    default void onStop(Player player) {
        getPlugin().getManaAbilityManager().setPlayerCooldown(player, getManaAbility());
        stop(player);
    }
}
