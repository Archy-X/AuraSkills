package dev.aurelium.auraskills.bukkit.stat;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.trait.BukkitTraitManager;
import dev.aurelium.auraskills.bukkit.trait.TraitImpl;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.user.User;

public class BukkitStatManager extends StatManager {

    public BukkitStatManager(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void reloadPlayer(User user) {
        // Reload traits
        for (Trait trait : plugin.getTraitManager().getEnabledTraits()) {
            TraitImpl traitImpl = ((BukkitTraitManager) plugin.getTraitManager()).getTraitImpl(trait);
            if (traitImpl == null) continue;

            traitImpl.reload(user, trait);
        }
    }

    @Override
    public <T> void reload(User user, T type) {

    }

    @Override
    public void reloadStat(User user, Stat stat) {
        // Reload traits
        for (Trait trait : stat.getTraits()) {
            TraitImpl traitImpl = ((BukkitTraitManager) plugin.getTraitManager()).getTraitImpl(trait);
            if (traitImpl == null) continue;

            traitImpl.reload(user, trait);
        }
    }
}
