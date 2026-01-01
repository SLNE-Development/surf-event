package dev.slne.surf.event.buildit.placeholder;

import dev.slne.surf.event.buildit.pdc.PersistentDataContainerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuildIttPlaceholderExpansion extends PlaceholderExpansion {

  @Override
  public @NotNull String getIdentifier() {
    return "buildit";
  }

  @Override
  public @NotNull String getAuthor() {
    return "SLNE";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
    if (player == null) {
      return null;
    }

    String[] paramsArray = params.split("_");

    if (paramsArray.length == 0) {
      return null;
    }

    if (params.equalsIgnoreCase("countdown")) {
      PersistentDataContainerManager instance = PersistentDataContainerManager.INSTANCE;


      if (!instance.isEventRunning()) {
        return "00:00:00";
      }

      return "%02d:%02d:%02d".formatted(
          instance.getCountdown() / 3600,
          (instance.getCountdown() / 60) % 60,
          instance.getCountdown() % 60
      );
    }

    return null;
  }
}
