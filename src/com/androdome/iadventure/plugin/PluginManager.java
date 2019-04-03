package com.androdome.iadventure.plugin;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.androdome.iadventure.MainFrame;

/**
 * PluginManager for the entire browser. Loads, unloads, destroys, and registers events for plug-ins.
 */
public class PluginManager {

	private MainFrame browerFrame;
	private File pluginDir = new File("./conf/plugins/");
	private ArrayList<RegisteredEvent> registeredEvents = new ArrayList<RegisteredEvent>();
	private HashMap<String, JavaPlugin> plugins = new HashMap<String, JavaPlugin>();
	private HashMap<JavaPlugin, String> names = new HashMap<JavaPlugin, String>();
	//ClassLoader mainLoader = ClassLoader.getSystemClassLoader();
	public static final float API_VERSION = 0.5F;
	public static enum Status{
		OK, CMD_IN_USE, CMD_ALNUM_WARN, CMD_HANDLE_INVALID
	}
	
	
	
	public boolean isAlphaNumeric(String s){
	    String pattern= "^[a-zA-Z0-9]*$";
	    return s.matches(pattern);
	}
	
	/**
	 * Unloads and destroys specified plug-in
	 */
	public boolean unload(JavaPlugin plugin)
	{
		
		for(int i = this.registeredEvents.size()-1; i >= 0; i--)
		{
			if(registeredEvents.get(i).getPlugin() == plugin)
			{
				registeredEvents.remove(i);
			}
		}
		plugin.destroy();
		names.remove(plugin);
		return plugins.values().remove(plugin);
	}
	
	/**
	 * Reloads all plug-ins when called. Will unload all handlers previously registered.
	 */
	public void reload()
	{
		this.destroyPlugins();
		this.loadPlugins();
	}
	
	/**
	 * Registers an event that the plug-in will listen to and returns a {@link RegisteredEvent} object. 
	 * Priorities are the order in which an event will be called upon occurring.<br><br>
	 * Highest recommended is Event.HIGHEST, with Event.REALTIME reserved for 
	 * mission critical plug-ins such as permission managers.
	 */
	public RegisteredEvent registerEvent(Event.Type eventType, EventListener listener, JavaPlugin plugin, Event.Priority priority)
	{
		for(int i = 0; i < registeredEvents.size(); i++)
		{
			if(registeredEvents.get(i).getPriority().compareTo(priority) > 0)
			{
				RegisteredEvent event = new RegisteredEvent(eventType, listener, plugin, priority);
				registeredEvents.add(i, event);
				return event;
			}
		}
		RegisteredEvent event = new RegisteredEvent(eventType, listener, plugin, priority);
		registeredEvents.add(event);
		return event;
	}
	
	/**
	 * Unregisters a {@link RegisteredEvent} from the event list and returns true if event was registered.
	 */
	public boolean unregisterEvent(RegisteredEvent event)
	{
		return registeredEvents.remove(event);
	}
	
	/*public boolean processEvent(BlockChangeEvent event)
	{
		for(int i = 0; i < this.registeredEvents.size(); i++)
		{
			if(this.registeredEvents.get(i).getType() == event.TYPE)
			{
				try{
					((BlockEventListener)registeredEvents.get(i).getListener()).onBlockChange(event);
				}
				catch(Throwable ex)
				{
					MinecraftServer.log.severe("Failed to pass event " + event.TYPE + " to "+this.names.get(registeredEvents.get(i).getPlugin())+", Unloading");
					ex.printStackTrace();
					unload(registeredEvents.get(i).getPlugin());
				}
			}
		}
		return !event.isCancelled();
	}*/
	
	
	public boolean processEvent(Event event)
	{
		return true;
	}
	
	public PluginManager(MainFrame server) {
		this.browerFrame = server;
	}

	/**
	 * Returns the instance of the current tab 
	 */
	public MainFrame getSelectedTab() {
		return browerFrame;
	}
	
	/**
	 * Returns the desired plug-in by name if it exists, or null if it doesn't
	 */
	public JavaPlugin getPlugin(String name)
	{
		return this.plugins.get(name);
	}
	
	public void destroyPlugins()
	{
		Collection<JavaPlugin> pluginList = plugins.values();
		JavaPlugin[] pluginArr = new JavaPlugin[pluginList.size()];
		pluginList.toArray(pluginArr);
		for(int i = 0; i < pluginArr.length; i++)
		{
			pluginArr[i].destroy();
		}
		registeredEvents.clear();
		plugins.clear();
		names.clear();
	}
	public void loadPlugins()
	{
		System.out.println("Loading plugins...");
		if(!plugins.isEmpty())
			throw new RuntimeException("Already initialized plugin manager!");
		if(!pluginDir.isDirectory())
		{
			pluginDir.mkdirs();
		}
		else
		{
			File[] jars = pluginDir.listFiles();
			ArrayList<URL> urls = new ArrayList<URL>();
			ArrayList<String> mainClass = new ArrayList<String>();
			ArrayList<String> name = new ArrayList<String>();
			if(jars != null)
			for(int i = 0; i < jars.length; i++)
			{
				String pluginName = jars[i].getName();
				try
				{
					if(jars[i].getName().endsWith(".jar"))
					{
						URLClassLoader classLoader = new URLClassLoader(new URL[]{jars[i].toURI().toURL()});
						BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("plugin.info")));
						pluginName = reader.readLine();
						String mainClassStr = reader.readLine();
						reader.close();
						urls.add(jars[i].toURI().toURL());
						mainClass.add(mainClassStr);
						name.add(pluginName);
					}
				}
				catch (Exception e)
				{
					System.out.println("Failed to load plugin " + pluginName+" (is it out of date?):");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Error e)
				{
					System.out.println("Plugin " + pluginName+" crashed while attempting to load. (is it out of date?):");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			URL[] uarr = new URL[urls.size()];
			urls.toArray(uarr);
			URLClassLoader classLoader = new URLClassLoader(uarr);
			for(int i = 0; i < urls.size(); i++)
			{
				try
				{
				Class<?> pluginClass;
				pluginClass = classLoader.loadClass(mainClass.get(i));
				JavaPlugin plugin = (JavaPlugin) pluginClass.newInstance();
				plugin.pluginManager = this;
				this.plugins.put(name.get(i), plugin);
				this.names.put(plugin, name.get(i));
				
				}
				catch (Exception e)
				{
					System.out.println("Failed to load plugin " + name.get(i)+" (is it out of date?):");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Error e)
				{
					System.out.println("Plugin " + name.get(i)+" crashed while attempting to load. (is it out of date?):");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ArrayList<JavaPlugin> plugins = new ArrayList<JavaPlugin>(this.plugins.values());
			for(int i = 0; i < plugins.size(); i++)
			{
				try{
					plugins.get(i).init();
				}
				catch (Exception e)
				{
					System.out.println("Failed to load plugin " + name.get(i)+" (is it out of date?):");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Error e)
				{
					System.out.println("Plugin " + name.get(i)+" crashed while attempting to load. (is it out of date?):");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Done!");
	}

	public ArrayList<String> getPlugins() {
		return new ArrayList<String>(this.names.values());
	}

	public File getPluginDirectory() {
		return pluginDir;
	}
	
	

}
