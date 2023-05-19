package dev.auramc.auraskills.common.scheduler;

public interface Task {

    TaskStatus getStatus();

    void cancel();

}
