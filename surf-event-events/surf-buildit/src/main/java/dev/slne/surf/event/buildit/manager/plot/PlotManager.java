package dev.slne.surf.event.buildit.manager.plot;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.generator.ClassicPlotManagerComponent;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import com.plotsquared.core.plot.PlotModificationManager;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import com.plotsquared.core.plot.flag.implementations.DoneFlag;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.slne.surf.event.buildit.BuildItEvent;
import dev.slne.surf.event.buildit.manager.EventStartStopListener;
import dev.slne.surf.event.buildit.manager.plot.CategoryFlag.Category;
import dev.slne.surf.event.buildit.permission.Permission;
import dev.slne.surf.event.buildit.random.SecureRandomHolder;
import dev.slne.surf.event.buildit.registry.PlotRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlotManager implements EventStartStopListener {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("PlotManager");
  public static final int PLOTS_PER_CATEGORY = 5;

  @Override
  public void onEventStart() {
    GlobalFlagContainer.getInstance().addFlag(CategoryFlag.CATEGORY_NOT_SET);
    PlotSquared.platform().placeholderRegistry()
        .createPlaceholder("currentplot_category",
            (__, plot) -> plot.getFlag(CategoryFlag.class).getDisplayName());

    Bukkit.getScheduler().runTaskAsynchronously(BuildItEvent.getInstance(), this::createPlots);
  }

  private void createPlots() {
    final PlotAPI plotAPI = new PlotAPI();
    final PlotArea plotArea = PlotRegistry.getPlotArea();
    final List<Player> players = getShuffledPlayers();
    final Queue<CategoryFlag> categories = getCategories();

    LOGGER.info("Creating plots for players: {}", players);
    LOGGER.info("Categories: {}", categories);

    final Object2ObjectMap<CategoryFlag, Queue<Plot>> plotsByCategory = generatePlotsByCategory(
        plotArea);

    for (final Player player : players) {
      if (Permission.BUILD_IT_EVENT_BYPASS.test(player)) {
        LOGGER.info("Player {} has bypass permission, skipping plot creation", player.getName());
        continue;
      }

      final PlotPlayer<?> plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());

      if (plotPlayer == null) {
        LOGGER.error("Failed to get PlotPlayer for player {}", player.getName());
        continue;
      }

      final CategoryFlag category = categories.poll();

      if (category == null) {
        LOGGER.error("Failed to get category for player {}", player.getName());
        continue;
      }

      final Queue<Plot> categoryPlots = plotsByCategory.get(category);

      if (categoryPlots == null) {
        LOGGER.error("Failed to get plots for category {}", category);
        continue;
      }

      if (categoryPlots.isEmpty()) {
        LOGGER.error("No plots left for category {}", category);
        continue;
      }

      final Plot plot = categoryPlots.poll();
      final PlotModificationManager modificationManager = plot.getPlotModificationManager();

      plot.claim(plotPlayer, false, null, true, true);

      plot.setFlag(category);
      modificationManager.setComponent(ClassicPlotManagerComponent.WALL.toString(), category.getValue().getBlockType(), null, null);
      modificationManager.setComponent(ClassicPlotManagerComponent.FLOOR.toString(), BlockTypes.LIGHT_GRAY_CONCRETE, null, null);

      plot.getCenter( (location) -> {
        Location loc = new Location(player.getWorld(), location.getX(),110, location.getZ());
        player.teleportAsync(loc);
      });

      LOGGER.info("Player {} claimed plot {} with category {}", player.getName(), plot.getId(),
          category);
    }
  }

  private Object2ObjectMap<CategoryFlag, Queue<Plot>> generatePlotsByCategory(PlotArea plotArea) {
    final Object2ObjectMap<CategoryFlag, Queue<Plot>> plotsByCategory = new Object2ObjectOpenHashMap<>();
    int categoryIndex = 0;

    for (final CategoryFlag category : CategoryFlag.getAllSetCategories()) {

      if (category.equals(CategoryFlag.CATEGORY_NOT_SET)) {
        continue;
      }

      final Queue<Plot> plots = new ArrayDeque<>();
      for (int i = 1; i <= PLOTS_PER_CATEGORY; i++) {
        final PlotId plotId = PlotId.fromString(categoryIndex + ";" + i);
        final Plot plot = plotArea.getPlot(plotId);

        if (plot != null) {
          plots.add(plot);
        } else {
          LOGGER.error("Failed to get plot {} for category {}", plotId, category);
        }
      }
      plotsByCategory.put(category, plots);
      categoryIndex += 1;
    }

    return plotsByCategory;
  }

  @Override
  public void onEventStop() {
    final PlotArea plotArea = PlotRegistry.getPlotArea();
    final Collection<Plot> plots = plotArea.getPlots().stream().filter(Plot::hasOwner).toList();

    setDoneFlag(plots);
  }

  private void setDoneFlag(Collection<Plot> plots) {
    final long flagValue = System.currentTimeMillis() / 1000;
    for (final Plot plot : plots) {
      final DoneFlag plotFlag = plot.getFlagContainer().getFlag(DoneFlag.class)
          .createFlagInstance(Long.toString(flagValue));

      plot.setFlag(plotFlag);
    }
  }

  @Override
  public void continueEvent() {

  }

  @Override
  public void onNotRunning() {

  }

  private List<Player> getShuffledPlayers() {
    final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    final SecureRandom random = SecureRandomHolder.getSecureRandom();

    LOGGER.info("Shuffling players for plot creation");
    LOGGER.info("Players: {}", players);

    Collections.shuffle(players, random);

    return players;
  }

  @Contract(" -> new")
  private @NotNull Queue<CategoryFlag> getCategories() {
    final SecureRandom random = SecureRandomHolder.getSecureRandom();

    final List<Category>  categoriesValue = Arrays.stream(Category.values()).filter(category -> !category.equals(Category.NOT_SET)).toList();
    final List<CategoryFlag> categories = new ArrayList<>(
        categoriesValue.size() * PLOTS_PER_CATEGORY);

    for (final Category category : categoriesValue) {
      for (short i = 0; i < PLOTS_PER_CATEGORY; i++) {
        categories.add(category.getFlag());
      }
    }

    Collections.shuffle(categories, random);

    return new ArrayDeque<>(categories);
  }
}
