package dev.slne.surf.event.buildit.pdc;

import dev.slne.surf.event.buildit.BuildItEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataContainerManager {

  public static final PersistentDataContainerManager INSTANCE = new PersistentDataContainerManager();
  private static final NamespacedKey EVENT_RUNNING = key("event_running");
  private static final NamespacedKey COUNTDOWN = key("countdown");

  private static NamespacedKey key(String key) {
    return new NamespacedKey(BuildItEvent.getInstance(), key);
  }

  public boolean isEventRunning() {
    return Boolean.TRUE.equals(getGlobalPDC().get(EVENT_RUNNING, PersistentDataType.BOOLEAN));
  }

  public void setEventRunning(boolean running) {
    getGlobalPDC().set(EVENT_RUNNING, PersistentDataType.BOOLEAN, running);
  }

  public long decrementCountdown() {
    long countdown = getCountdown();
    if (countdown-- > 0) {
      setCountdown(countdown);
    }

    return countdown;
  }

  public long getCountdown() {
    return getGlobalPDC().getOrDefault(COUNTDOWN, PersistentDataType.LONG, 0L);
  }

  public void setCountdown(long countdown) {
    getGlobalPDC().set(COUNTDOWN, PersistentDataType.LONG, countdown);
  }

  private PersistentDataContainer getGlobalPDC() {
    return Bukkit.getWorlds().getFirst().getPersistentDataContainer();
  }
}
