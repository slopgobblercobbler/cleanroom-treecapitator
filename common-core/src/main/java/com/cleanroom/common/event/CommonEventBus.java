package com.cleanroom.common.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class CommonEventBus {
    private static final CommonEventBus INSTANCE = new CommonEventBus();
    private final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    private CommonEventBus() {}

    public static CommonEventBus getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> void register(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void post(T event) {
        List<Consumer<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Consumer<?> rawListener : eventListeners) {
                ((Consumer<T>) rawListener).accept(event);
            }
        }
    }

    public void clear() {
        listeners.clear();
    }
}
