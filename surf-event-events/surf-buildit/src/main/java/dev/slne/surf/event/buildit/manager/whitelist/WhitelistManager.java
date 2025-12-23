package dev.slne.surf.event.buildit.manager.whitelist;

import dev.slne.surf.event.buildit.manager.EventStartStopListener;
import org.bukkit.Bukkit;

public class WhitelistManager implements EventStartStopListener {

  @Override
  public void onEventStart() {
    Bukkit.setWhitelist(true);
    Bukkit.getOnlinePlayers().forEach(player -> player.setWhitelisted(true));
  }

  @Override
  public void onEventStop() {
    Bukkit.setWhitelist(false);
    Bukkit.getWhitelistedPlayers().forEach(player -> player.setWhitelisted(false));
  }

  @Override
  public void continueEvent() {

  }

  @Override
  public void onNotRunning() {

  }
}
