package dev.slne.surf.event.buildit.listener.notrunning.join;

import dev.slne.surf.event.buildit.registry.SettingRegistry;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NotRunningJoinListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    event.getPlayer().teleportAsync(SettingRegistry.SPAWN_LOCATION);
    event.getPlayer().getWorld().getBlockAt(SettingRegistry.SPAWN_LOCATION.clone().add(0, -1, 0)).setType(Material.BEDROCK);
  }
}
