package io.github.ebaldino.SketchMap.command.sub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.ebaldino.SketchMap.command.SketchMapSubCommand;
import io.github.ebaldino.SketchMap.map.SketchMap;


public class SubCommandList extends SketchMapSubCommand {

	@Override
	public String getSub() {
		return "list";
	}

	@Override
	public String getPermission() {
		return "sketchmap.list";
	}

	@Override
	public String getDescription() {
		return "List all current loaded maps";
	}

	@Override
	public String getSyntax() {
		return "/sketchmap list";
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args, String prefix) {

		sender.sendMessage(ChatColor.GREEN + "List of SketchMaps loaded!");
		List<String> maps = new ArrayList<String>();
		
		if(SketchMap.getLoadedMaps().isEmpty()) {
			sender.sendMessage(ChatColor.RED + prefix + "No maps have been loaded to list.");
			return;
		}
		
		for(SketchMap map : SketchMap.getLoadedMaps()) {
			if(map.isPublicProtected()) {
				continue;
			}
			maps.add(map.getID());
		}

		Collections.sort(maps);
		
		for(String map : maps) {
			
			sender.sendMessage(ChatColor.GREEN + "- " + ChatColor.AQUA + map);
		}
		
	}
	


}
