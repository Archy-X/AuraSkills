package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.user.User;
import me.clip.placeholderapi.PlaceholderAPI;
import org.spongepowered.configurate.ConfigurationNode;

public class PlaceholderApiHook extends PlaceholderHook {

    public PlaceholderApiHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        new PlaceholderApiProvider(plugin, "auraskills").register();
        new PlaceholderApiProvider(plugin, "aureliumskills").register();
    }

    @Override
    public String setPlaceholders(User user, String message) {
        return PlaceholderAPI.setPlaceholders(((BukkitUser) user).getPlayer(), message);
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return PlaceholderHook.class;
    }
}
