package dev.slne.surf.event.buildit.instance;

import dev.slne.surf.event.base.instance.EventInstance;
import dev.slne.surf.event.buildit.manager.BuildItEventManager;
import dev.slne.surf.event.buildit.permission.Permission;

public class BuildItEventInstance extends EventInstance {

  @Override
  public void onLoad() {
    super.onLoad();
  }

  @Override
  public void onEnable() {
    super.onEnable();

    BuildItEventManager.INSTANCE.tryContinueEvent();
    Permission.registerPermissions();
  }

  @Override
  public void onDisable() {
    super.onDisable();
  }
}
