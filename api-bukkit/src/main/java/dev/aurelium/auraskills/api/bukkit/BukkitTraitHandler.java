package dev.aurelium.auraskills.api.bukkit;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitHandler;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;

public interface BukkitTraitHandler extends TraitHandler {

    double getBaseLevel(Player player, Trait trait);

    void onReload(Player player, SkillsUser user, Trait trait);

}
