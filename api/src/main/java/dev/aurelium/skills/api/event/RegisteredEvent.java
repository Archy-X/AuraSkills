package dev.aurelium.skills.api.event;

import java.util.function.Consumer;

public interface RegisteredEvent<T extends AureliumSkillsEvent> extends AutoCloseable {

    Class<T> getEventClass();

    boolean isActive();

    @Override
    void close();

    Consumer<? super T> getHandler();

}
