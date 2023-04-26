package dev.aurelium.skills.common.event;

import dev.aurelium.skills.api.event.AureliumSkillsEvent;
import dev.aurelium.skills.api.event.EventManager;
import dev.aurelium.skills.api.event.RegisteredEvent;
import dev.aurelium.skills.common.AureliumSkillsPlugin;

import java.util.function.Consumer;

public class AureliumSkillsEventManager implements EventManager {

    private final AureliumSkillsEventBus eventBus;
    private final EventDispatcher eventDispatcher;

    public AureliumSkillsEventManager(AureliumSkillsPlugin plugin) {
        this.eventBus = new AureliumSkillsEventBus(plugin);
        this.eventDispatcher = new EventDispatcher(this);
    }

    public void callEvent(AureliumSkillsEvent event) {
        eventDispatcher.callEvent(event);
    }

    @Override
    public <T extends AureliumSkillsEvent> RegisteredEvent<T> registerHandler(Class<T> eventClass, Consumer<? super T> handler) {
        return subscribe(null, eventClass, handler);
    }

    @Override
    public <T extends AureliumSkillsEvent> RegisteredEvent<T> registerHandler(Object plugin, Class<T> eventClass, Consumer<? super T> handler) {
        return subscribe(plugin, eventClass, handler);
    }

    public AureliumSkillsEventBus getEventBus() {
        return eventBus;
    }

    private <T extends AureliumSkillsEvent> RegisteredEvent<T> subscribe(Object plugin, Class<T> eventClass, Consumer<? super T> handler) {
        if (!AureliumSkillsEvent.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException("Class " + eventClass.getName() + " does not implement AureliumSkillsEvent");
        }

        EventSubscription<T> registeredEvent = new EventSubscription<>(eventBus, eventClass, handler, plugin);
        eventBus.register(eventClass, registeredEvent);

        return registeredEvent;
    }

}
