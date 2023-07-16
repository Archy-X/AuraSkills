package dev.aurelium.auraskills.bukkit.leveler;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.source.SourceType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

public abstract class AbstractLeveler implements Listener {

    protected final AuraSkills plugin;
    private final SourceType sourceType;

    public AbstractLeveler(AuraSkills plugin, SourceType sourceType) {
        this.plugin = plugin;
        this.sourceType = sourceType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    protected boolean failsChecks(Cancellable event, Player player, Location location, Skill skill) {
        return isDisabled(skill) || isCancelled(event, skill) || blockLocation(player, location, skill) || blockPlayer(player, skill);
    }

    protected boolean failsChecks(Player player, Location location, Skill skill) {
        return isDisabled(skill) || blockLocation(player, location, skill) || blockPlayer(player, skill);
    }

    protected boolean isDisabled(Skill skill) {
        return !skill.isEnabled();
    }

    protected boolean isCancelled(Cancellable event, Skill skill) {
        return skill.optionBoolean("check_cancelled", true) && event.isCancelled();
    }

    protected boolean blockLocation(Player player, Location location, Skill skill) {
        // Checks if in blocked world
        if (plugin.getWorldManager().isInBlockedWorld(location)) {
            return true;
        }
        // Checks if in blocked region
        if (plugin.getHookManager().isRegistered(WorldGuardHook.class)) {
            WorldGuardHook worldGuard = plugin.getHookManager().getHook(WorldGuardHook.class);
            if (worldGuard.isInBlockedRegion(location)) {
                return true;
            }
            // Check if blocked by flags
            else if (worldGuard.blockedByFlag(location, player, WorldGuardHook.FlagKey.XP_GAIN)) {
                return true;
            } else return worldGuard.blockedBySkillFlag(location, player, skill);
        }
        return false;
    }

    protected boolean blockPlayer(Player player, Skill skill) {
        if (!player.hasPermission("auraskills.skill" + skill.name())) {
            return true;
        }
        // Check creative mode disable
        if (plugin.configBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
            return player.getGameMode().equals(GameMode.CREATIVE);
        }
        return false;
    }

}
