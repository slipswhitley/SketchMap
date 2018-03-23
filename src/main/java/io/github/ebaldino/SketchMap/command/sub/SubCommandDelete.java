package io.github.ebaldino.SketchMap.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.ebaldino.SketchMap.SketchMapAPI;
import io.github.ebaldino.SketchMap.command.SketchMapSubCommand;
import io.github.ebaldino.SketchMap.map.SketchMap;

public class SubCommandDelete extends SketchMapSubCommand {

	@Override
	public String getSub() {
		return "delete";
	}

	@Override
	public String getPermission() {
		return "sketchmap.delete";
	}

	@Override
	public String getDescription() {
		return "Unload and Delete a SketchMap";
	}

	@Override
	public String getSyntax() {
		return "/sketchmap delete <map-id>";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args, String prefix) {
		
		if(args.length != 2) {
			sender.sendMessage(ChatColor.RED + prefix + "Invalid command Arguments. "
					+ "Try, \"" + getSyntax() + "\"");
			return;
		}
		
		SketchMap map = SketchMapAPI.getMapByID(args[1]);
		
		if(map == null) {
			sender.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" 
					+ args[1].toLowerCase() + "\"");
			return;
		}
		
		
		if(map.isPublicProtected()) {
			sender.sendMessage(ChatColor.RED + prefix + "An External Plugin has requested that"
					+ " this map is protected from public access.");
			return;
		}
		
		String mapID = map.getID();
		
		map.delete();
		sender.sendMessage(ChatColor.AQUA + prefix + "Map \"" + mapID + "\" deleted.");
	}


}
