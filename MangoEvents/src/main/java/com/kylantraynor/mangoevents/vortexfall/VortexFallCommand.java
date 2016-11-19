package com.kylantraynor.mangoevents.vortexfall;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VortexFallCommand implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1){ args = new String[]{"HELP"};}
		switch(args[0].toUpperCase()){
		case "HELP":
			sender.sendMessage("/VortexFall CreateBooster <radius> <material> <boost> <points> <pathname>");
			return true;
		case "CREATEBOOSTER":
			if(args.length >= 5 && sender instanceof Player){
				int radius = Integer.parseInt(args[1]);
				Material material = Material.getMaterial(args[2]);
				double boost = Double.parseDouble(args[3]);
				int points = Integer.parseInt(args[4]);
				String path = "";
				if(args.length > 5){
					path = args[5];
				}
				new VortexFallBooster(((Player) sender).getLocation(), ((Player)sender).getEyeLocation().getDirection(), radius,
						material, boost, points, path);
				return true;
			}
			return true;
		case "REMOVEBOOSTER":
			if(sender instanceof Player){
				VortexFallBooster b = VortexFallBooster.getClosest(((Entity) sender).getLocation());
				if(b != null){
					b.remove();
				}
			}
			return true;
		case "SETPATH":
			if(sender instanceof Player && args.length > 1){
				VortexFallBooster b = VortexFallBooster.getClosest(((Entity) sender).getLocation());
				if(b != null){
					b.setPath(VortexFallPath.get(args[1]));
					sender.sendMessage("Path set!");
				}
			}
			return true;
		case "TOGGLE":
			if(sender instanceof Player && args.length > 1){
				switch(args[1].toUpperCase()){
				case "PATHVISIBILITY":
					if(args.length > 2){
						VortexFallPath path = VortexFallPath.get(args[2]);
						if(path != null){
							path.setVisibility(!path.getVisibility());
							sender.sendMessage("Visibility of " + path.getName() + ": " + path.getVisibility());
						}
					}
					break;
				case "FINISHLINE":
					VortexFallBooster b = VortexFallBooster.getClosest(((Entity) sender).getLocation());
					if(b != null){
						b.setFinishLine(!b.isFinishLine());
						sender.sendMessage("Is Finish Line? " + b.isFinishLine());
					}
					break;
				}
			}
			return true;
		}
		return false;
	}

}
