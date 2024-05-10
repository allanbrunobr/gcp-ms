package com.br.multicloudecore.gcpmodule.events;

import com.br.multicloudecore.gcpmodule.interfaces.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventBus is a simple event bus that allows objects to publish events and subscribe to them.doc
 */
public class EventBus {
  private Map<Class<?>, List<EventListener<?>>> subscribers;

  /**
   * EventBus is a simple event bus that allows objects to publish events and subscribe to them.
   */
  public EventBus() {
    this.subscribers = new HashMap<>();
  }

  public <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
    subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
  }

  /**
   * Publishes an event to all the registered listeners.
   * The event type is determined by the argument passed to the method.
   *
   * @param event the event to be published
   * @param <T>   the type of the event
   */
  public <T> void publish(T event) {
    List<EventListener<?>> listeners = subscribers.get(event.getClass());
    if (listeners != null) {
      for (EventListener<?> listener : listeners) {
        @SuppressWarnings("unchecked")
        EventListener<T> typedListener = (EventListener<T>) listener;
        typedListener.onEvent(event);
      }
    }
  }
}