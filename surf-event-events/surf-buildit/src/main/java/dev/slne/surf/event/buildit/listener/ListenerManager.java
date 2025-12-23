package dev.slne.surf.event.buildit.listener;

import dev.slne.surf.event.buildit.BuildItEvent;
import dev.slne.surf.event.buildit.listener.notrunning.EventNotRunningListenerManager;
import dev.slne.surf.event.buildit.listener.running.EventRunningListenerManager;
import dev.slne.surf.event.buildit.manager.EventStartStopListener;

public class ListenerManager implements EventStartStopListener {

  @Override
  public void onEventStart() {
    EventNotRunningListenerManager.INSTANCE.unregisterListeners(BuildItEvent.getInstance().getEventInstance().getEventManager());
    EventRunningListenerManager.INSTANCE.registerListeners(BuildItEvent.getInstance().getEventInstance().getEventManager());
  }

  @Override
  public void onEventStop() {
    EventRunningListenerManager.INSTANCE.unregisterListeners(BuildItEvent.getInstance().getEventInstance().getEventManager());
  }

  @Override
  public void continueEvent() {
    EventRunningListenerManager.INSTANCE.registerListeners(BuildItEvent.getInstance().getEventInstance().getEventManager());
  }

  @Override
  public void onNotRunning() {
    EventNotRunningListenerManager.INSTANCE.registerListeners(BuildItEvent.getInstance().getEventInstance().getEventManager());
  }
}
