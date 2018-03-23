package com.mcplugindev.slipswhitley.sketchmap.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.mcplugindev.slipswhitley.sketchmap.command.sub.SubCommandCreate;
import com.mcplugindev.slipswhitley.sketchmap.command.sub.SubCommandDelete;
import com.mcplugindev.slipswhitley.sketchmap.command.sub.SubCommandGet;
import com.mcplugindev.slipswhitley.sketchmap.command.sub.SubCommandHelp;
import com.mcplugindev.slipswhitley.sketchmap.command.sub.SubCommandImport;
import com.mcplugindev.slipswhitley.sketchmap.command.sub.SubCommandList;
import com.mcplugindev.slipswhitley.sketchmap.command.sub.SubCommandPlace;


public abstract class SketchMapSubCommand {

	private static List<SketchMapSubCommand> commands;
	
	public abstract String getSub();
	public abstract String getPermission();
	public abstract String getDescription();
	public abstract String getSyntax();
	
	public abstract void onCommand(CommandSender sender, 
			String[] args, String prefix );
	
	public static void loadCommands() {
		commands = new ArrayList<SketchMapSubCommand>();

		loadCommand(new SubCommandCreate());
		loadCommand(new SubCommandDelete());
		loadCommand(new SubCommandGet());
		loadCommand(new SubCommandHelp());
		loadCommand(new SubCommandImport());
		loadCommand(new SubCommandList());
		loadCommand(new SubCommandPlace());
		
	}
	
	private static void loadCommand(SketchMapSubCommand sub) {
		commands.add(sub);
	}
	
	public static List<SketchMapSubCommand> getCommands() {
		return commands;
	}
}
	