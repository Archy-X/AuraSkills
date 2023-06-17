package dev.aurelium.auraskills.common.event;

import dev.aurelium.auraskills.api.event.AuraSkillsEvent;
import dev.aurelium.auraskills.api.event.RegisteredEvent;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EventSubscription<T extends AuraSkillsEvent> implements RegisteredEvent<T>, EventSubscriber<T> {

    private final AuraSkillsEventBus eventBus;
    private final Class<T> eventClass;
    private final Consumer<? super T> handler;
    private final @Nullable Object plugin;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public EventSubscription(AuraSkillsEventBus eventBus, Class<T> eventClass, Consumer<? super T> handler, @Nullable Object plugin) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
        this.handler = handler;
        this.plugin = plugin;
    }

    @Override
    public Class<T> getEventClass() {
        return eventClass;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void close() {
        if (!this.active.getAndSet(false)) {
            return;
        }

        this.eventBus.unregister(this);
    }

    @Override
    public Consumer<? super T> getHandler() {
        return handler;
    }

    @Override
    public void invoke(@NonNull T event) {
        try {
            handler.accept(event);
        } catch (Throwable t) {
            this.eventBus.getPlugin().logger().warn("Error passing event " + event.getEventType().getSimpleName() + " to handler " + this.handler.getClass().getName(), t);
        }
    }

    public @Nullable Object getPlugin() {
        return plugin;
    }

}
