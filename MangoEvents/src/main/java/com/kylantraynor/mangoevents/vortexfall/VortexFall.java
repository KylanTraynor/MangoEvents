package com.kylantraynor.mangoevents.vortexfall;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import com.kylantraynor.mangoevents.MangoEvents;

public class VortexFall {
	static File boosterFile = null;
	private static YamlConfiguration boosterConfig;
	/**
	 * Enables the VortexFall event.
	 * @param plugin (MangoEvents)
	 */
	public static void enable(MangoEvents plugin){
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new VortexFallListener(), plugin);
		plugin.getCommand("VortexFall").setExecutor(new VortexFallCommand());
		File dataFolder = plugin.getDataFolder();
		if(dataFolder.exists()){
			boosterFile = new File(dataFolder, "VortexFall/boosters.yml");
			if(boosterFile.exists()){
				loadAllBoosters();
			}
		}
	}
	/**
	 * Disables the VortexFall event.
	 */
	public static void disable(){
		saveAllBoosters();
	}
	/**
	 * Loads all boosters from the file.
	 */
	private static void loadAllBoosters() {
		YamlConfiguration cfg = getBoosterConfig();
		VortexFallBooster.all.clear();
		for(World w : Bukkit.getWorlds()){
			int i = 0;
			while(cfg.contains(w.getName() + "." + i)){
				double locationX = cfg.getDouble(w.getName() + "." + i + ".locationX");
				double locationY = cfg.getDouble(w.getName() + "." + i + ".locationY");
				double locationZ = cfg.getDouble(w.getName() + "." + i + ".locationZ");
				double directionX = cfg.getDouble(w.getName() + "." + i + ".directionX");
				double directionY = cfg.getDouble(w.getName() + "." + i + ".directionY");
				double directionZ = cfg.getDouble(w.getName() + "." + i + ".directionZ");
				int radius = cfg.getInt(w.getName() + "." + i + ".radius");
				Material m = Material.getMaterial(cfg.getString(w.getName() + "." + i + ".material"));
				double boost = cfg.getDouble(w.getName() + "." + i + ".boost");
				String path = cfg.getString(w.getName() + "." + i + ".path", "");
				int points = cfg.getInt(w.getName() + "." + i + ".points");
				new VortexFallBooster(new Location(w, locationX, locationY, locationZ), new Vector(directionX, directionY, directionZ),
						radius, m, boost, points, path);
				i++;
			}
		}
	}
	
	private static void saveAllBoosters(){
		YamlConfiguration cfg = getBoosterConfig();
		Map<World, Integer> m = new HashMap<World, Integer>();
		for(VortexFallBooster b : VortexFallBooster.all){
			World w = b.getLocation().getWorld();
			if(!m.containsKey(w)){
				m.put(w, 0);
			}
			cfg.set(w.getName() + "." + m.get(w) + ".locationX", b.getLocation().getX());
			cfg.set(w.getName() + "." + m.get(w) + ".locationY", b.getLocation().getY());
			cfg.set(w.getName() + "." + m.get(w) + ".locationZ", b.getLocation().getZ());
			cfg.set(w.getName() + "." + m.get(w) + ".directionX", b.getDirection().getX());
			cfg.set(w.getName() + "." + m.get(w) + ".directionY", b.getDirection().getY());
			cfg.set(w.getName() + "." + m.get(w) + ".directionZ", b.getDirection().getZ());
			cfg.set(w.getName() + "." + m.get(w) + ".radius", b.getRadius());
			cfg.set(w.getName() + "." + m.get(w) + ".material", b.getMaterial().toString());
			cfg.set(w.getName() + "." + m.get(w) + ".boost", b.getBoost());
			cfg.set(w.getName() + "." + m.get(w) + ".points", b.getPoints());
			if(b.getPath() != null){
				cfg.set(w.getName() + "." + m.get(w) + ".path", b.getPath().getName());
			}
			m.put(w, m.get(w) + 1);
		}
		saveBoosterConfig();
	}
	
	/**
	 * Saves the boosters to a file.
	 */
	private static void saveBoosterConfig() {
		try {
			boosterConfig.save(boosterFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the booster config from file.
	 */
	private static void loadBoosterConfig(){
		try {
			boosterFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		YamlConfiguration cfg = new YamlConfiguration();
		try {
			cfg.load(boosterFile);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boosterConfig = cfg;
	}
	
	/**
	 * Gets the Configuration File of Boosters.
	 * @return
	 */
	public static YamlConfiguration getBoosterConfig(){
		if(boosterConfig == null){
			loadBoosterConfig();
		}
		return boosterConfig;
	}
}