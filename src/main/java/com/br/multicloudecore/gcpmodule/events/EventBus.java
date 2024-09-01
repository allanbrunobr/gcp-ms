package com.br.multicloudecore.gcpmodule.events;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
/**
 * EventBus is a simple event bus that allows objects to publish events and subscribe to them.doc
 */
@Component
public class EventBus {
  private final Map<Class<?>, CopyOnWriteArrayList<Subscription<?>>> subscribers = new ConcurrentHashMap<>();

  public <T> Subscription<T> subscribe(Class<T> eventType, Predicate<T> handler) {
    Subscription<T> subscription = new Subscription<>(eventType, handler);
    subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(subscription);
    return subscription;
  }

  public void unsubscribe(Subscription<?> subscription) {
    subscribers.computeIfPresent(subscription.eventType, (key, list) -> {
      list.remove(subscription);
      return list.isEmpty() ? null : list;
    });
  }

  public <T> void publish(T event) {
    Class<?> eventType = event.getClass();
    subscribers.getOrDefault(eventType, new CopyOnWriteArrayList<>()).removeIf(subscription -> {
      @SuppressWarnings("unchecked")
      Subscription<T> typedSubscription = (Subscription<T>) subscription;
      return !typedSubscription.handle(event);
    });
  }

  public class Subscription<T> {
    private final Class<T> eventType;
    private final Predicate<T> handler;

    Subscription(Class<T> eventType, Predicate<T> handler) {
      this.eventType = eventType;
      this.handler = handler;
    }

    boolean handle(T event) {
      return handler.test(event);
    }

    public void unsubscribe() {
      EventBus.this.unsubscribe(this);
    }
  }
}