package dev.slne.surf.event.buildit.listener.notrunning;

import dev.slne.surf.event.base.manager.EventManager;
import dev.slne.surf.event.buildit.listener.notrunning.join.NotRunningJoinListener;
import java.util.List;
import org.bukkit.event.Listener;

public class EventNotRunningListenerManager {

  public static final EventNotRunningListenerManager INSTANCE = new EventNotRunningListenerManager();

  private EventNotRunningListenerManager() {
  }

  private final List<Listener> listeners = List.of(new NotRunningJoinListener());

  public void registerListeners(EventManager manager) {
    listeners.forEach(manager::registerListener);
  }

  public void unregisterListeners(EventManager manager) {
    listeners.forEach(manager::unregisterListener);
  }
}
