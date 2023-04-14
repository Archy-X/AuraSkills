package dev.aurelium.skills.api.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
