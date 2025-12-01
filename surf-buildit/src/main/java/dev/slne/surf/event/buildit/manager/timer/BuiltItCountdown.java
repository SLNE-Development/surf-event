package dev.slne.surf.event.buildit.manager.timer;

import dev.slne.surf.event.buildit.BuildItEvent;
import dev.slne.surf.event.buildit.manager.BuildItEventManager;
import dev.slne.surf.event.buildit.pdc.PersistentDataContainerManager;
import dev.slne.surf.event.buildit.registry.SettingRegistry;
import dev.slne.surf.surfapi.core.api.messages.Colors;
import java.time.Duration;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

public class BuiltItCountdown implements Runnable {

  private BukkitTask task;

  public BuiltItCountdown() {
  }

  @Override
  public void run() {
    final long remainingSeconds = PersistentDataContainerManager.INSTANCE.decrementCountdown();

    if (remainingSeconds <= 10) {
      Bukkit.getOnlinePlayers().forEach(player -> {
        player.sendActionBar(Component.text("Die Bauphase endet in ", Colors.INFO).append(Component.text(remainingSeconds, Colors.VARIABLE_VALUE))
            .append(Component.text(" Sekunden!", Colors.INFO)));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
      });
    }

    if (remainingSeconds <= 0) {
      cancelCountDown();
      BuildItEventManager.INSTANCE.stopEvent();
      Bukkit.getOnlinePlayers().forEach(player -> {
        player.sendActionBar(Component.text("Die Bauphase wurde beendet! Viel Erfolg!", Colors.SUCCESS));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.7f, 1);
      });
    }
  }

  public void start() {
    PersistentDataContainerManager.INSTANCE.setCountdown(Duration.ofMinutes(SettingRegistry.COUNTDOWN_TIME_MINUTES).toSeconds());
    task = Bukkit.getScheduler()
        .runTaskTimerAsynchronously(BuildItEvent.getInstance(), this, 0, 20);
  }

  public void cancelCountDown() {
    task.cancel();
  }

  public void continueCountdown() {
    if (task != null) {
      task.cancel();
    }

    task = Bukkit.getScheduler()
        .runTaskTimerAsynchronously(BuildItEvent.getInstance(), this, 0, 20);
  }
}
