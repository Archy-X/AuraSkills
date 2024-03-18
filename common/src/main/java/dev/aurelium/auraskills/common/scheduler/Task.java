package dev.aurelium.auraskills.common.scheduler;

public interface Task {

    TaskStatus getStatus();

    void cancel();

}
