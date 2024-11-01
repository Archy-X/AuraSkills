package dev.aurelium.auraskills.bukkit.ui;

import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ui.ActionBarManager;
import dev.aurelium.auraskills.common.ui.UiProvider;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitActionBarManager extends ActionBarManager {

    public BukkitActionBarManager(AuraSkillsPlugin plugin, UiProvider uiProvider) {
        super(plugin, uiProvider);
    }

    @Override
    @NotNull
    public String getHp(User user) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return "";

        return String.valueOf(Math.round(player.getHealth() * Traits.HP.optionDouble("action_bar_scaling", 1)));
    }

    @Override
    @NotNull
    public String getMaxHp(User user) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return "";

        AttributeInstance attribute = player.getAttribute(AttributeCompat.MAX_HEALTH);
        if (attribute != null) {
            return String.valueOf(Math.round(attribute.getValue() * Traits.HP.optionDouble("action_bar_scaling", 1)));
        }
        return "";
    }

    @Override
    @NotNull
    public String getWorldName(User user) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player != null) {
            return player.getWorld().getName();
        }
        return "";
    }
}
