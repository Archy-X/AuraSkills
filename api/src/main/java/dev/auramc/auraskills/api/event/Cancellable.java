package dev.auramc.auraskills.api.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
