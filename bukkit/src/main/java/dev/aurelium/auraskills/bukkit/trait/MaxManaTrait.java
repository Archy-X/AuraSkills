package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class MaxManaTrait extends TraitImpl {

    MaxManaTrait(AuraSkills plugin) {
        super(plugin, Traits.MAX_MANA);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return trait.optionDouble("base", 20);
    }

    @Override
    protected void reload(Player player, Trait trait) {
        if (!trait.optionBoolean("allow_overflow", false)) {
            User user = plugin.getUser(player);
            // Remove overflow mana
            double maxMana = user.getMaxMana();
            if (user.getMana() > maxMana) {
                user.setMana(maxMana);
            }
        }
    }

    @Override
    protected void changeWorld(PlayerChangedWorldEvent event, Trait trait) {
        if (!trait.equals(Traits.MAX_MANA)) return;

        reload(event.getPlayer(), trait);
    }

}
