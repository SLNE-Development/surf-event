package dev.slne.surf.event.buildit.permission;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public enum Permission {
  BUILD_IT_EVENT_BYPASS("buildit.event.bypass.plot.selection"),
  COMMAND_EVENT_START_STOP("buildit.event.command.startstop"),
  ;

  private final String permission;

  Permission(String permission) {
    this.permission = permission;
  }

  public String getPermission() {
    return permission;
  }

  public static void registerPermissions() {
    final PluginManager pluginManager = Bukkit.getPluginManager();

    for (final Permission permission : values()) {
      try {
        pluginManager.addPermission(
            new org.bukkit.permissions.Permission(permission.getPermission()));
      } catch (final IllegalArgumentException e) {
        // Permission already exists
      }
    }
  }

  public boolean test(@NotNull Permissible permissible) {
    return permissible.hasPermission(permission);
  }
}
