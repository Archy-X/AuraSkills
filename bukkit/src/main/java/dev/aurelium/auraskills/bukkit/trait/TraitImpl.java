package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public abstract class TraitImpl implements BukkitTraitHandler, Listener {

    protected final AuraSkills plugin;
    private final Trait[] traits;

    TraitImpl(AuraSkills plugin, Trait... traits) {
        this.plugin = plugin;
        this.traits = traits;
    }

    @Override
    public Trait[] getTraits() {
        return traits;
    }

    @Override
    public void onReload(Player player, SkillsUser user, Trait trait) {
        reload(player, trait);
    }

    protected void reload(Player player, Trait trait) {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        for (Trait trait : traits) {
            changeWorld(event, trait);
        }
    }

    protected void changeWorld(PlayerChangedWorldEvent event, Trait trait) {

    }

}
