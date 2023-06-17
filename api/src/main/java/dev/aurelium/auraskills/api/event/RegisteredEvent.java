package dev.aurelium.auraskills.api.event;

import java.util.function.Consumer;

public interface RegisteredEvent<T extends AuraSkillsEvent> extends AutoCloseable {

    Class<T> getEventClass();

    boolean isActive();

    @Override
    void close();

    Consumer<? super T> getHandler();

}
