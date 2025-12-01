package dev.slne.surf.event.buildit.manager.timer;

import dev.slne.surf.event.buildit.manager.EventStartStopListener;

public class TimerManager implements EventStartStopListener {

  private BuiltItCountdown countdown = new BuiltItCountdown();

  @Override
  public void onEventStart() {
    countdown.start();
  }

  @Override
  public void onEventStop() {
    countdown.cancelCountDown();
  }

  @Override
  public void continueEvent() {
    countdown.continueCountdown();
  }

  @Override
  public void onNotRunning() {

  }

  public BuiltItCountdown getCountdown() {
    return countdown;
  }
}
