package dev.slne.surf.event.buildit.command;

import dev.slne.surf.event.buildit.manager.BuildItEventManager;
import dev.slne.surf.event.buildit.pdc.PersistentDataContainerManager;
import dev.slne.surf.event.buildit.permission.Permission;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandExecutor;

public final class EventStartStopCommand extends CommandAPICommand {

  public EventStartStopCommand(String commandName) {
    super(commandName);

    withPermission(Permission.COMMAND_EVENT_START_STOP.getPermission());

    withSubcommands(startCommand(), stopCommand());
  }

  private CommandAPICommand startCommand() {
    return new CommandAPICommand("start")
        .withRequirement(sender -> !PersistentDataContainerManager.INSTANCE.isEventRunning())
        .executes((CommandExecutor) (sender, args) -> BuildItEventManager.INSTANCE.startEvent());
  }

  private CommandAPICommand stopCommand() {
    return new CommandAPICommand("stop")
        .withRequirement(sender -> PersistentDataContainerManager.INSTANCE.isEventRunning())
        .executes((CommandExecutor) (sender, args) -> BuildItEventManager.INSTANCE.stopEvent());
  }
}
