package dev.auramc.auraskills.common.event;

import dev.auramc.auraskills.api.event.AuraSkillsEvent;
import dev.auramc.auraskills.api.event.EventManager;
import dev.auramc.auraskills.api.event.RegisteredEvent;
import dev.auramc.auraskills.common.AuraSkillsPlugin;

import java.util.function.Consumer;

public class AuraSkillsEventManager implements EventManager {

    private final AuraSkillsEventBus eventBus;
    private final EventDispatcher eventDispatcher;

    public AuraSkillsEventManager(AuraSkillsPlugin plugin) {
        this.eventBus = new AuraSkillsEventBus(plugin);
        this.eventDispatcher = new EventDispatcher(this);
    }

    public void callEvent(AuraSkillsEvent event) {
        eventDispatcher.callEvent(event);
    }

    @Override
    public <T extends AuraSkillsEvent> RegisteredEvent<T> registerHandler(Class<T> eventClass, Consumer<? super T> handler) {
        return subscribe(null, eventClass, handler);
    }

    @Override
    public <T extends AuraSkillsEvent> RegisteredEvent<T> registerHandler(Object plugin, Class<T> eventClass, Consumer<? super T> handler) {
        return subscribe(plugin, eventClass, handler);
    }

    public AuraSkillsEventBus getEventBus() {
        return eventBus;
    }

    private <T extends AuraSkillsEvent> RegisteredEvent<T> subscribe(Object plugin, Class<T> eventClass, Consumer<? super T> handler) {
        if (!AuraSkillsEvent.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException("Class " + eventClass.getName() + " does not implement AureliumSkillsEvent");
        }

        EventSubscription<T> registeredEvent = new EventSubscription<>(eventBus, eventClass, handler, plugin);
        eventBus.register(eventClass, registeredEvent);

        return registeredEvent;
    }

}
