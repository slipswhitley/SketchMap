package com.mcplugindev.slipswhitley.sketchmap.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;

import java.util.Arrays;

public class SketchMapCommand implements CommandExecutor {
	public SketchMapCommand() {
		SketchMapSubCommand.loadCommands();
	}

	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		final String prefix = "[SketchMap] ";
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + prefix + "Command Arguments required. Try, \"/sketchmap help\"");
			return true;
		}
		for (final SketchMapSubCommand command : SketchMapSubCommand.getCommands()) {
			if (!command.getSub().equalsIgnoreCase(args[0])) {
				continue;
			}
			if (command.getPermission() != null && sender instanceof Player
					&& !SketchMapUtils.hasPermission((Player) sender, command.getPermission())) {
				sender.sendMessage(ChatColor.RED + prefix + "You do not have permission to use this command.");
				return true;
			}

            if(args.length > 1)
            {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length); // Strip the subcommand off the front
                command.onCommand(sender, newArgs, prefix);
            }
            else
            {
                command.onCommand(sender, new String[] {}, prefix); // Sending no args if the only arg was the subcommand
            }
			return true;
		}
		sender.sendMessage(ChatColor.RED + prefix + "Unknown Command. Try, \"/sketchmap help\"");
		return true;
	}
}
