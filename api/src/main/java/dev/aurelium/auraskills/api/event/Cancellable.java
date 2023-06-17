package dev.aurelium.auraskills.api.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
