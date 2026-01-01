package dev.slne.surf.event.buildit.manager;

public interface EventStartStopListener {
  void onEventStart();

  void onEventStop();

  void continueEvent();

  void onNotRunning();
}
