package dev.aurelium.auraskills.api.event;

import java.util.function.Consumer;

public interface EventManager {

    <T extends AuraSkillsEvent> RegisteredEvent<T> registerHandler(Class<T> eventClass, Consumer<? super T> handler);

    <T extends AuraSkillsEvent> RegisteredEvent<T> registerHandler(Object plugin, Class<T> eventClass, Consumer<? super T> handler);

    void registerEvents(Object plugin, AuraSkillsListener listener);

}
