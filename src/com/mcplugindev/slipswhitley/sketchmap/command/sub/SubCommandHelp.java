package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapPlugin;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;

public class SubCommandHelp extends SketchMapSubCommand {
	@Override
	public String getSub() {
		return "help";
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Display general plugin information";
	}

	@Override
	public String getSyntax() {
		return "/sketchmap help";
	}

	@Override
	public void onCommand(final CommandSender sender, final String[] args, final String prefix) {
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.GREEN + "SketchMap Version "
				+ SketchMapPlugin.getPlugin().getDescription().getVersion() + " - Authors " + ChatColor.GOLD
				+ "SlipsWhitley" + ChatColor.GREEN + " & " + ChatColor.GOLD + "Fyrinlight");
		sender.sendMessage(ChatColor.AQUA + " SketchMap is a plugin designed to allow players to put images"
				+ " from the web onto a single or array of maps. These maps can be added to ItemFrames to complete "
				+ "the image and create awesome visual displays in vanilla minecraft.");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.GOLD + "SketchMap Commands:");
		for (final SketchMapSubCommand command : SketchMapSubCommand.getCommands()) {
			sender.sendMessage(ChatColor.GREEN + "- " + ChatColor.AQUA + command.getSyntax() + ChatColor.GOLD + " - "
					+ ChatColor.GREEN + command.getDescription());
		}
		sender.sendMessage(" ");
	}
}
