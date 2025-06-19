package dev.aurelium.auraskills.bukkit.stat;

import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;
import dev.aurelium.auraskills.api.stat.ReloadableIdentifier;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.trait.BukkitTraitManager;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.stat.StatManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;

public class BukkitStatManager extends StatManager {

    public BukkitStatManager(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public <T extends ReloadableIdentifier> void reload(User user, T type) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return;

        if (type instanceof Stat stat) {
            reloadStat(user, stat);
        } else if (type instanceof Trait trait) {
            reloadTrait(user, player, trait);
        }
    }

    @Override
    public void reloadAllTraits(User user) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return;

        for (BukkitTraitHandler impl : ((BukkitTraitManager) plugin.getTraitManager()).getAllTraitImpls()) {
            for (Trait trait : impl.getTraits()) {
                impl.onReload(player, user.toApi(), trait);
            }
        }
    }

    private void reloadStat(User user, Stat stat) {
        if (!stat.isEnabled()) return;
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return;
        // Reload traits
        for (Trait trait : stat.getTraits()) {
            reloadTrait(user, player, trait);
        }
    }

    private void reloadTrait(User user, Player player, Trait trait) {
        BukkitTraitHandler traitImpl = ((BukkitTraitManager) plugin.getTraitManager()).getTraitImpl(trait);
        if (traitImpl == null) return;

        traitImpl.onReload(player, user.toApi(), trait);
    }

}
