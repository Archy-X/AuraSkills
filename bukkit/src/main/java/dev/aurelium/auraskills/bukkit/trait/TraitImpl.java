package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;

public abstract class TraitImpl implements Listener {

    protected final AuraSkills plugin;
    private final Set<Trait> traits;

    TraitImpl(AuraSkills plugin, Trait... traits) {
        this.plugin = plugin;
        this.traits = Set.of(traits);
    }

    public Set<Trait> getTraits() {
        return traits;
    }

    public abstract double getBaseLevel(Player player, Trait trait);

    public String getMenuDisplay(double value, Trait trait) {
        return NumberUtil.format1(value);
    }

    public final void reload(User user, Trait trait) {
        reload(((BukkitUser) user).getPlayer(), trait);
    }

    protected void reload(Player player, Trait trait) {

    }

}
