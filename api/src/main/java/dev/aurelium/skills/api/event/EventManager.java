package dev.aurelium.skills.api.event;

import java.util.function.Consumer;

public interface EventManager {

    <T extends AureliumSkillsEvent> RegisteredEvent<T> registerHandler(Class<T> eventClass, Consumer<? super T> handler);

    <T extends AureliumSkillsEvent> RegisteredEvent<T> registerHandler(Object plugin, Class<T> eventClass, Consumer<? super T> handler);

}
