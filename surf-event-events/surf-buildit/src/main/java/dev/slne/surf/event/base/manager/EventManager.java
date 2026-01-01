package dev.slne.surf.event.base.manager;

import dev.slne.surf.event.base.EventBase;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

/**
 * The interface Event manager.
 */
public class EventManager {

	private final PluginManager pluginManager;

	/**
	 * Instantiates a new Event manager.
	 *
	 * @param pluginManager the plugin manager
	 */
	public EventManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	/**
	 * Register listener.
	 *
	 * @param listener the listener
	 */
	public void registerListener(Listener listener) {
		pluginManager.registerEvents(listener, EventBase.getBaseInstance());
	}

	/**
	 * Unregister listener.
	 *
	 * @param listener the listener
	 */
	public void unregisterListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}

	/**
	 * Unregister all listeners.
	 */
	public void unregisterAllListeners() {
		HandlerList.unregisterAll(EventBase.getBaseInstance());
	}
}
