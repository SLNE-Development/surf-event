package dev.slne.surf.event.buildit.registry;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.PlotArea;

public final class PlotRegistry {

  private static final PlotAPI plotAPI = new PlotAPI();

  public static PlotArea getPlotArea() {
    return plotAPI.getPlotSquared().getPlotAreaManager().getPlotArea("plotworld", null);
  }

  public static PlotArea getFinishPlotArea() {
    return plotAPI.getPlotSquared().getPlotAreaManager().getPlotArea("finished_plots", null);
  }
}
