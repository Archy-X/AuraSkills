package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.LevelerContext;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class SourceLeveler implements Listener {

    protected final AuraSkills plugin;
    private final SourceType sourceType;
    private final LevelerContext context;

    public SourceLeveler(AuraSkills plugin, SourceType sourceType) {
        this.plugin = plugin;
        this.sourceType = sourceType;
        this.context = new LevelerContext(plugin.getApi(), sourceType);
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    protected boolean disabled() {
        return !plugin.getSkillManager().isSourceEnabled(sourceType);
    }

    protected boolean failsChecks(Cancellable event, Player player, Location location, Skill skill) {
        return context.failsChecks(event, player, location, skill);
    }

    protected boolean failsChecks(Player player, Location location, Skill skill) {
        return context.failsChecks(player, location, skill);
    }

    protected boolean isDisabled(Skill skill) {
        return context.isDisabled(skill);
    }

    protected boolean isCancelled(Cancellable event, Skill skill) {
        return context.isCancelled(event, skill);
    }

    protected boolean blockLocation(Player player, Location location, Skill skill) {
        return context.blockLocation(player, location, skill);
    }

    protected boolean blockPlayer(Player player, Skill skill) {
        return context.blockPlayer(player, skill);
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
