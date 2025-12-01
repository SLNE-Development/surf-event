package dev.slne.surf.event.buildit;

import dev.slne.surf.event.base.EventBase;
import dev.slne.surf.event.base.manager.EventManager;
import dev.slne.surf.event.buildit.command.EventStartStopCommand;
import dev.slne.surf.event.buildit.instance.BuildItEventInstance;
import dev.slne.surf.event.buildit.placeholder.BuildIttPlaceholderExpansion;

public class BuildItEvent extends EventBase<BuildItEventInstance> {

  /**
   * Instantiates a new Event base.
   */
  public BuildItEvent() {
    super(new BuildItEventInstance());
  }


  @Override
  public void onLoaded(EventManager eventManager) {
    super.onLoaded(eventManager);

    new EventStartStopCommand("event").register();
}

  @Override
  public void onEnable() {
    super.onEnable();

    new BuildIttPlaceholderExpansion().register();
  }

  public static BuildItEvent getInstance() {
    return getPlugin(BuildItEvent.class);
  }
}
