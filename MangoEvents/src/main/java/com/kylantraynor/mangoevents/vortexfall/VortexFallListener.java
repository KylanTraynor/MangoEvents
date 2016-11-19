package com.kylantraynor.mangoevents.vortexfall;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class VortexFallListener implements Listener{
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(event.getPlayer().isGliding() && !event.getFrom().getBlock().equals(event.getTo().getBlock())){
			showClosest(event.getPlayer());
			showAllVisiblePaths(event.getPlayer());
			updateAllPaths(event.getPlayer());
		}
	}

	private void updateAllPaths(Player player) {
		for(VortexFallPath p : VortexFallPath.getAll()){
			p.updatePlayer(player);
		}
	}

	private void showClosest(Player player) {
		VortexFallBooster b = VortexFallBooster.getClosest(player.getLocation());
		for(VortexFallBooster i : VortexFallBooster.all){
			if(i.equals(b)){
				continue;
			} else if(i.getPath() != null) {
				if(i.getPath().getVisibility()){
					continue;
				}
			}
			i.hide(player);
		}
		if(b != null){
			b.show(player);
			if(b.isInside(player.getLocation())){
				b.boost(player);
			}
		}
	}
	
	private void showAllVisiblePaths(Player player){
		for(VortexFallPath path : VortexFallPath.getAll()){
			if(path.getVisibility()){
				path.show(player);
			}
		}
	}
}
