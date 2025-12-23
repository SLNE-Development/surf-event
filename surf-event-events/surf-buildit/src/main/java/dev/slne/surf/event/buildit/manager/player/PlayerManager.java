package dev.slne.surf.event.buildit.manager.player;

import dev.slne.surf.event.buildit.manager.EventStartStopListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class PlayerManager implements EventStartStopListener {

  @Override
  public void onEventStart() {

  }

  @Override
  public void onEventStop() {
    Bukkit.getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));
  }

  @Override
  public void continueEvent() {

  }

  @Override
  public void onNotRunning() {

  }
}
