package dev.slne.surf.event.buildit.registry;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import dev.slne.surf.event.base.util.Lazy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class SettingRegistry {
  // @formatter:off
  public static final Location SPAWN_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 99.5, 130, -31.5);
  public static final Integer COUNTDOWN_TIME_MINUTES = 90;
  public static final Lazy<World> PASTE_WORLD = Lazy.of(() -> Bukkit.getWorld("finished_plots"));
  public static final Lazy<com.sk89q.worldedit.world.World> PASTE_WORLD_WE = PASTE_WORLD.map(BukkitAdapter::adapt);
  // @formatter:on
}
