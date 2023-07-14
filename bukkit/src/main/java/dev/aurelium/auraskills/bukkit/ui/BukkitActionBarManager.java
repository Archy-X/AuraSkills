package dev.aurelium.auraskills.bukkit.ui;

import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.ui.ActionBarManager;
import dev.aurelium.auraskills.common.ui.UiProvider;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.attribute.Attribute;
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
        return String.valueOf(Math.round(player.getHealth() * plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
    }

    @Override
    @NotNull
    public String getMaxHp(User user) {
        Player player = ((BukkitUser) user).getPlayer();
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            return String.valueOf(Math.round(attribute.getValue() * plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING)));
        }
        return "";
    }

    @Override
    @NotNull
    public String getWorldName(User user) {
        return ((BukkitUser) user).getPlayer().getWorld().getName();
    }
}
