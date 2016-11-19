package com.kylantraynor.mangoevents.vortexfall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class VortexFallPath {
	
	private static List<VortexFallPath> all = new ArrayList<VortexFallPath>();
	
	private String name = "";
	
	private Map<Player, DateTime> startingTimes = new HashMap<Player, DateTime>();

	private boolean visibility = false;

	private Map<Player, DateTime> checkpointsTime = new HashMap<Player, DateTime>();
	private Map<Player, VortexFallBooster> checkpoints = new HashMap<Player, VortexFallBooster>();
	
	public VortexFallPath(String name){
		this.name = name;
		all.add(this);
	}

	public static VortexFallPath get(String path) {
		if(path.isEmpty()) return null;
		for(VortexFallPath p : all){
			if(p.getName().equalsIgnoreCase(path)){
				return p;
			}
		}
		return new VortexFallPath(path);
	}

	String getName() {
		return name;
	}

	public boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(boolean b) {
		this.visibility = b;
	}

	public static List<VortexFallPath> getAll() {
		return all;
	}

	public void show(Player player) {
		for(VortexFallBooster booster : VortexFallBooster.getAllOn(this)){
			booster.show(player);
		}
	}
	
	public void hide(Player player){
		for(VortexFallBooster booster : VortexFallBooster.getAllOn(this)){
			booster.hide(player);
		}
	}

	public Map<Player, DateTime> getStartingTimes() {
		return startingTimes;
	}

	public void setStartingTimes(Map<Player, DateTime> startingTimes) {
		this.startingTimes = startingTimes;
	}

	public void checkPoint(Player player, VortexFallBooster vortexFallBooster) {
		if(!startingTimes.containsKey(player)){
			startingTimes.put(player, DateTime.now());
		}
		if(this.equals(vortexFallBooster.getPath())){
			checkpointsTime.put(player, DateTime.now());
			checkpoints.put(player, vortexFallBooster);
		}
	}
	
	public void updatePlayer(Player player){
		if(player == null) return;
		if(checkpointsTime.containsKey(player)){
			if(checkpointsTime.get(player).isBefore(DateTime.now().minusSeconds(5))){
				cancelPlayer(player);
			}
		}
	}

	private void cancelPlayer(Player player) {
		startingTimes.remove(player);
		checkpointsTime.remove(player);
		checkpoints.remove(player);
		if(this.hasFinishLine()){
			Bukkit.getServer().broadcastMessage(player.getName() + " took too long between boosters!");
		}
	}

	private boolean hasFinishLine() {
		for(VortexFallBooster booster : VortexFallBooster.getAllOn(this)){
			if(booster.isFinishLine()){
				return true;
			}
		}
		return false;
	}

	public void finishPlayer(Player player) {
		DateTime start = startingTimes.get(player);
		Interval in = new Interval(start, checkpointsTime.get(player));
		if(in.toDuration().getMillis() == 0){
			startingTimes.remove(player);
			checkpointsTime.remove(player);
			checkpoints.remove(player);
			return;
		}
		Bukkit.getServer().broadcastMessage(player.getName() + " finished " + this.getName() + " in " + 
				in.toDuration().getStandardMinutes() + " minutes, " +
				(in.toDuration().getStandardSeconds() % 60) + " seconds and " +
				(in.toDuration().getMillis() % 1000) + " milliseconds.");
		startingTimes.remove(player);
		checkpointsTime.remove(player);
		checkpoints.remove(player);
	}
}
