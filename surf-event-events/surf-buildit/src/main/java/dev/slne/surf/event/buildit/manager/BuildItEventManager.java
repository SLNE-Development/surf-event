package dev.slne.surf.event.buildit.manager;

import dev.slne.surf.event.buildit.BuildItEvent;
import dev.slne.surf.event.buildit.listener.ListenerManager;
import dev.slne.surf.event.buildit.manager.player.PlayerManager;
import dev.slne.surf.event.buildit.manager.plot.PlotManager;
import dev.slne.surf.event.buildit.manager.timer.TimerManager;
import dev.slne.surf.event.buildit.manager.whitelist.WhitelistManager;
import dev.slne.surf.event.buildit.pdc.PersistentDataContainerManager;
import dev.jorel.commandapi.CommandAPI;
import java.util.List;
import org.bukkit.Bukkit;

public final class BuildItEventManager {

  public static final BuildItEventManager INSTANCE = new BuildItEventManager();

  private final List<EventStartStopListener> listeners = List.of(
      new ListenerManager(),
      new PlotManager(),
      new WhitelistManager(),
      new PlayerManager(),
      new TimerManager()
  );

  public void startEvent() {
    PersistentDataContainerManager.INSTANCE.setEventRunning(true);
    listeners.forEach(EventStartStopListener::onEventStart);

    updateRequirements();
  }

  public void stopEvent() {
    Bukkit.getScheduler().runTask(BuildItEvent.getInstance(), () -> {
      PersistentDataContainerManager.INSTANCE.setEventRunning(false);

      listeners.forEach(EventStartStopListener::onEventStop);
      listeners.forEach(EventStartStopListener::onNotRunning);

      updateRequirements();
    });
  }

  public void tryContinueEvent() {
    if (!PersistentDataContainerManager.INSTANCE.isEventRunning()) {
      listeners.forEach(EventStartStopListener::onNotRunning);
    } else {
      listeners.forEach(EventStartStopListener::continueEvent);
    }
  }

  private void updateRequirements() {
    Bukkit.getOnlinePlayers().forEach(CommandAPI::updateRequirements);
  }

}
