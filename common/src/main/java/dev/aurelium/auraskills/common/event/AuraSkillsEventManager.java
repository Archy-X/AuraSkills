package dev.aurelium.auraskills.common.event;

import dev.aurelium.auraskills.api.event.*;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

import java.lang.reflect.Method;
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

    public void registerEvents(Object plugin, AuraSkillsListener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(AuraSkillsEventHandler.class)) {
                return;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                return;
            }

            Class<?> eventType = parameterTypes[0];
            if (!AuraSkillsEvent.class.isAssignableFrom(eventType)) {
                return;
            }
            Class<? extends AuraSkillsEvent> casted = eventType.asSubclass(AuraSkillsEvent.class);

            subscribe(plugin, casted, event -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
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
