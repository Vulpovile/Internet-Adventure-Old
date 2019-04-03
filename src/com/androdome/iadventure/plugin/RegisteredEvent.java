package com.androdome.iadventure.plugin;

public class RegisteredEvent {

	private EventListener listener;
	private JavaPlugin plugin;
	private Event.Priority priority;
	private Event.Type type;

	/**
	 * RegisteredEvent class that was returned by the {@link PluginManager}'s registerEvent method
	 */
	RegisteredEvent(Event.Type type, EventListener listener, JavaPlugin plugin, Event.Priority priority) {
		this.type = type;
		this.listener = listener;
		this.plugin = plugin;
		this.priority = priority;
	}
	
	public Event.Priority getPriority()
	{
		return priority;
	}
	public Event.Type getType()
	{
		return type;
	}
	public JavaPlugin getPlugin()
	{
		return plugin;
	}
	public EventListener getListener()
	{
		return listener;
	}

}
