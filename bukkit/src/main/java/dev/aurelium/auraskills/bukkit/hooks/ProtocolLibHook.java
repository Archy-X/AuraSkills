package dev.aurelium.auraskills.bukkit.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.Hook;
import org.bukkit.entity.Player;

public class ProtocolLibHook extends Hook {

    public ProtocolLibHook(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public void sendActionBar(Player player, String message) {
        // TODO Implement ProtocolLib support
    }

}
