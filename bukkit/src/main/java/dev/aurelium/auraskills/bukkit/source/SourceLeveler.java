package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardHook;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.source.SourceType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;

public abstract class SourceLeveler implements Listener {

    protected final AuraSkills plugin;
    private final SourceType sourceType;

    public SourceLeveler(AuraSkills plugin, SourceType sourceType) {
        this.plugin = plugin;
        this.sourceType = sourceType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    protected boolean disabled() {
        return !plugin.getSkillManager().isSourceEnabled(sourceType);
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
            return worldGuard.isBlocked(location, player, skill);
        }
        return false;
    }

    protected boolean blockPlayer(Player player, Skill skill) {
        if (!player.hasPermission("auraskills.skill." + skill.name().toLowerCase(Locale.ROOT))) {
            return true;
        }
        // Check creative mode disable
        if (plugin.configBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
            return player.getGameMode().equals(GameMode.CREATIVE);
        }
        return false;
    }

    protected boolean failsClickChecks(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return true;

        ClickType click = event.getClick();
        // Only allow right and left clicks if inventory full
        if (click != ClickType.LEFT && click != ClickType.RIGHT && ItemUtils.isInventoryFull(player)) return true;
        if (event.getResult() != Event.Result.ALLOW) return true; // Make sure the click was successful
        if (player.getItemOnCursor().getType() != Material.AIR) return true; // Make sure cursor is empty
        InventoryAction action = event.getAction();
        // Only give if item was picked up
        return action != InventoryAction.PICKUP_ALL && action != InventoryAction.MOVE_TO_OTHER_INVENTORY
                && action != InventoryAction.PICKUP_HALF && action != InventoryAction.DROP_ALL_SLOT
                && action != InventoryAction.DROP_ONE_SLOT && action != InventoryAction.HOTBAR_SWAP;
    }

}
