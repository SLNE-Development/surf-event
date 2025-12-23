package dev.slne.surf.event.base.instance;

import dev.slne.surf.event.base.manager.EventManager;
import org.bukkit.Bukkit;

/**
 * The interface Event instance.
 */
public class EventInstance {

	private EventManager eventManager;

	/**
	 * On load.
	 */
	public void onLoad() {
		eventManager = new EventManager(Bukkit.getPluginManager());
	}

	/**
	 * On enable.
	 */
	public void onEnable() {
	}

	/**
	 * On disable.
	 */
	public void onDisable() {
		eventManager.unregisterAllListeners();
	}

	/**
	 * Gets event manager.
	 *
	 * @return the event manager
	 */
	public EventManager getEventManager() {
		return eventManager;
	}
}
