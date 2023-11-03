package dev.aurelium.auraskills.api.event.user;

import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SkillsUser user;

    public UserLoadEvent(Player player, SkillsUser user) {
        this.player = player;
        this.user = user;
    }

    public Player getPlayer() {
        return player;
    }

    public SkillsUser getUser() {
        return user;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
