package com.androdome.iadventure.plugin;

import java.io.File;

import com.androdome.iadventure.MainFrame;

public abstract class JavaPlugin {
	PluginManager pluginManager;
	/**
	 * Returns the {@link PluginManager}
	 */
	public final PluginManager getManager()
	{
		return pluginManager;
	}
	/**
	 * returns the {@link PluginManager}'s {@link MainFrame} instance
	 */
	public final MainFrame getSelectedTab()
	{
		return pluginManager.getSelectedTab();
	}

	/**
	 * Called when the plug-in is first initialized by the {@link PluginManager}
	 */
	public abstract void init();
	public abstract void destroy();
	public File getPluginDirectory() {
		return pluginManager.getPluginDirectory();
	}
	
	

}
