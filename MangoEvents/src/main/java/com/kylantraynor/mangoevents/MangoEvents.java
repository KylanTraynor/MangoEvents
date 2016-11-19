package com.kylantraynor.mangoevents;

import org.bukkit.plugin.java.JavaPlugin;

import com.kylantraynor.mangoevents.vortexfall.VortexFall;

public class MangoEvents extends JavaPlugin{
	
	/**
	 * When the plugin is about to be enabled.
	 */
	public void onEnable(){
		saveDefaultConfig();
		
		VortexFall.enable(this);
	}
	
	/**
	 * When the plugin is about to be disabled.
	 */
	public void onDisable(){
		VortexFall.disable();
	}

}
