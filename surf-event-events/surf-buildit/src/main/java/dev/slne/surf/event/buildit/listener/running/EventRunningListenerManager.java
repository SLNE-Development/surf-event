package dev.slne.surf.event.buildit.listener.running;

import dev.slne.surf.event.base.manager.EventManager;
import java.util.List;
import org.bukkit.event.Listener;

public class EventRunningListenerManager {

  public static final EventRunningListenerManager INSTANCE = new EventRunningListenerManager();

  private EventRunningListenerManager() {
  }

  private final List<Listener> listeners = List.of();

  public void registerListeners(EventManager manager) {
    listeners.forEach(manager::registerListener);
  }

  public void unregisterListeners(EventManager manager) {
    listeners.forEach(manager::unregisterListener);
  }
}
