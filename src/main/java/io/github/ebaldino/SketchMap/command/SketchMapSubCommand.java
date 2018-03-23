package io.github.ebaldino.SketchMap.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.ebaldino.SketchMap.command.sub.SubCommandCreate;
import io.github.ebaldino.SketchMap.command.sub.SubCommandDelete;
import io.github.ebaldino.SketchMap.command.sub.SubCommandGet;
import io.github.ebaldino.SketchMap.command.sub.SubCommandHelp;
import io.github.ebaldino.SketchMap.command.sub.SubCommandImport;
import io.github.ebaldino.SketchMap.command.sub.SubCommandList;
import io.github.ebaldino.SketchMap.command.sub.SubCommandPlace;


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
	