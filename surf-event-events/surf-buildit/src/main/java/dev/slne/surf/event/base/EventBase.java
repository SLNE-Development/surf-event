package dev.slne.surf.event.base;

import dev.slne.surf.event.base.instance.EventInstance;
import dev.slne.surf.event.base.manager.EventManager;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

/**
 * The type Event base.
 *
 * @param <I> the type parameter
 */
public class EventBase<I extends EventInstance> extends JavaPlugin {

	private static EventBase<? extends EventInstance> baseInstance;
	private I eventInstance;

	/**
	 * Instantiates a new Event base.
	 *
	 * @param eventInstance the event instance
	 */
	public EventBase(I eventInstance) {
		this.eventInstance = eventInstance;
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	public void onLoad() {
		baseInstance = this;

		eventInstance.onLoad();
		onLoaded(eventInstance.getEventManager());
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	public void onEnable() {
		eventInstance.onEnable();
		onEnabled(eventInstance.getEventManager());
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	public void onDisable() {
		onDisabled(eventInstance.getEventManager());
		eventInstance.onDisable();

		baseInstance = null;
		eventInstance = null;
	}


	/**
	 * On loaded.
	 *
	 * @param eventManager the event manager
	 */
	@OverrideOnly
	public void onLoaded(EventManager eventManager) {

	}

	/**
	 * On enabled.
	 *
	 * @param eventManager the event manager
	 */
	@OverrideOnly
	public void onEnabled(EventManager eventManager) {
	}

	/**
	 * On disabled.
	 *
	 * @param eventManager the event manager
	 */
	@OverrideOnly
	public void onDisabled(EventManager eventManager) {

	}

	/**
	 * Gets base instance.
	 *
	 * @return the base instance
	 */
	@SuppressWarnings("unchecked")
	public static <I extends EventInstance> EventBase<I> getBaseInstance() {
		return (EventBase<I>) baseInstance;
	}

	/**
	 * Gets event instance.
	 *
	 * @return the event instance
	 */
	public EventInstance getEventInstance() {
		return eventInstance;
	}
}
