package com.kylantraynor.mangoevents.vortexfall;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VortexFallBooster {
	
	public static List<VortexFallBooster> all = new ArrayList<VortexFallBooster>();
	
	private Location location;
	private Vector direction;
	private int radius;
	private Material material;
	private double boost;
	private int points;
	private VortexFallPath path;
	private List<Player> players = new ArrayList<Player>();
	private boolean isFinishLine = false;
	
	public VortexFallBooster(Location location,Vector vector, int radius, Material material, double boost, int points, String path){
		this.location = location.clone();
		this.direction = vector.clone();
		this.radius = radius;
		this.material = material;
		this.boost = boost;
		this.points = points;
		
		this.path = VortexFallPath.get(path);
		
		all.add(this);
	}
	
	public static VortexFallBooster getClosest(Location l){
		double distance = 100;
		VortexFallBooster closest = null;
		for(VortexFallBooster b : all){
			if(l.getWorld().equals(b.getLocation().getWorld())){
				if(l.distance(b.getLocation()) <= distance){
					distance = l.distance(b.getLocation());
					closest = b;
				}
			}
		}
		return closest;
	}
	
	/**
	 * Builds a visual representation of the booster to the player.
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void show(Player player){
		if(player.getLocation().distance(this.location) > 150){
			hide(player);
			return;
		}
		if(getPlayers().contains(player)){
			return;
		}
		int vertices = 4 * getRadius();
		Vector c = getLocation().toVector();
		Vector n = getDirection().clone();
		Vector u = getOrthogonalVector(n).normalize();
		Vector v = (n.crossProduct(u)).normalize();
		for(double angle = 0; angle <= Math.PI * 2; angle += (Math.PI * 2) / vertices){
			Vector v1 = u.clone().multiply(getRadius() * Math.cos(angle));
			Vector v2 = v.clone().multiply(getRadius() * Math.sin(angle));
			Vector p = c.clone().add(v1).add(v2);
			Location l = new Location(getLocation().getWorld(), p.getX(), p.getY(), p.getZ());
			player.sendBlockChange(l, getMaterial(), (byte) 0);
		}
		getPlayers().add(player);
	}
	/**
	 * Removes the visual representation of the booster for the player.
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	public void hide(Player player){
		if(!getPlayers().contains(player)){
			return;
		}
		int vertices = 4 * getRadius();
		Vector c = getLocation().toVector();
		Vector n = getDirection().clone();
		Vector u = getOrthogonalVector(n).normalize();
		Vector v = (n.crossProduct(u)).normalize();
		for(double angle = 0; angle <= Math.PI * 2; angle += (Math.PI * 2) / vertices){
			Vector v1 = u.clone().multiply(getRadius() * Math.cos(angle));
			Vector v2 = v.clone().multiply(getRadius() * Math.sin(angle));
			Vector p = c.clone().add(v1).add(v2);
			Location l = new Location(getLocation().getWorld(), p.getX(), p.getY(), p.getZ());
			player.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData());
		}
		getPlayers().remove(player);
	}
	
	/**
	 * Checks if the given location is inside the booster.
	 * @param location
	 * @return
	 */
	public boolean isInside(Location location){
		if(getLocation().getWorld().equals(location.getWorld())){
			if(getLocation().distance(location) <= getRadius()){
				boolean foundPositive = false;
				boolean foundNegative = false;
				Vector v1 = location.getBlock().getLocation().toVector();
				Vector v2 = getLocation().toVector();
				for(double x = 0; x <= 1; x++){
					for(double y = 0; y <= 1; y++){
						for(double z = 0; z <= 1; z++){
							if(foundPositive && foundNegative) return true;
							Vector v3 = v1.clone().add(new Vector(x, y, z));
							if(getNormalizedDot(v3.subtract(v2), getDirection()) < 0){
								foundNegative = true;
							} else {
								foundPositive = true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	public double getNormalizedDot(Vector v1, Vector v2){
		v1 = v1.normalize();
		v2 = v2.normalize();
		return v1.dot(v2);
	}
	/**
	 * Triggers the boost for the player.
	 * @param player
	 */
	public void boost(Player player){
		if(player.isGliding()){
			player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().normalize().multiply(getBoost())));
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
			if(getPath() != null){
				getPath().checkPoint(player, this);
				if(isFinishLine()){
					getPath().finishPlayer(player);
				}
			}
		}
	}
	
	
	public Vector getOrthogonalVector(Vector v){
		Vector result = null;
		if(v.getZ() != 0){
			result = new Vector(1, 0, (-v.getX())/v.getZ());
		} else if(v.getY() != 0){
			result = new Vector(1, (-v.getX())/v.getY(), 0);
		} else if(v.getX() != 0){
			result = new Vector((-v.getZ())/v.getX(), 0, 1);
		}
		return result;
	}
	/**
	 * Gets the Location of the booster.
	 * @return
	 */
	public Location getLocation() { return location; }
	/**
	 * Sets the Location of the booster.
	 * @param location
	 */
	public void setLocation(Location location) { this.location = location; }
	/**
	 * Gets the direction of the booster.
	 * @return
	 */
	public Vector getDirection() { return direction; }
	/**
	 * Sets the direction of the booster.
	 * @param direction
	 */
	public void setDirection(Vector direction) { this.direction = direction; }
	/**
	 * Gets the radius of the booster.
	 * @return
	 */
	public int getRadius() { return radius; }
	/**
	 * Sets the radius of the booster.
	 * @param radius
	 */
	public void setRadius(int radius) { this.radius = radius; }
	/**
	 * Gets the material of the booster.
	 * @return
	 */
	public Material getMaterial() { return material; }
	/**
	 * Sets the Material of the booster.
	 * @param material
	 */
	public void setMaterial(Material material) { this.material = material; }
	/**
	 * Gets the boost of the booster.
	 * @return
	 */
	public double getBoost() { return boost; }
	/**
	 * Sets the boost of the booster.
	 * @param boost
	 */
	public void setBoost(double boost) { this.boost = boost; }
	/**
	 * Gets the amount of points given by the booster.
	 * @return
	 */
	public int getPoints() { return points; }
	/**
	 * Sets the amount of points given by the booster.
	 * @param points
	 */
	public void setPoints(int points) { this.points = points; }
	/**
	 * Gets a list of player to which this booster is displayed.
	 * @return
	 */
	public List<Player> getPlayers() { return players; }
	/**
	 * Removes the Booster from the list of existing boosters.
	 */
	public void remove() {
		for(Player p : getPlayers().toArray(new Player[getPlayers().size()])){
			hide(p);
		}
		all.remove(this);
	}

	public VortexFallPath getPath() {
		return path;
	}

	public void setPath(VortexFallPath path) {
		this.path = path;
	}

	public boolean isFinishLine() {
		return isFinishLine;
	}

	public void setFinishLine(boolean isFinishLine) {
		this.isFinishLine = isFinishLine;
	}

	public static List<VortexFallBooster> getAllOn(VortexFallPath vortexFallPath) {
		List<VortexFallBooster> list = new ArrayList<VortexFallBooster>();
		if(vortexFallPath == null) return list;
		for(VortexFallBooster booster : all){
			if(booster.getPath() != null){
				if(booster.getPath().equals(vortexFallPath)){
					list.add(booster);
				}
			}
		}
		return list;
	}
}
