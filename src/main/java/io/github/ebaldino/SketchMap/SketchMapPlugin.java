package io.github.ebaldino.SketchMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.ebaldino.SketchMap.command.SketchMapCommand;
import io.github.ebaldino.SketchMap.file.SketchMapLoader;
import io.github.ebaldino.SketchMap.listener.PlayerListener;

public class SketchMapPlugin extends JavaPlugin {
	
	private static SketchMapPlugin plugin;
	
	public void onEnable(){
		plugin = this;
		
		SketchMapUtils.setupPermissions();
		setupCommands();
		setupListeners();
		
		SketchMapLoader.loadMaps();
		
		sendEnabledMessage();
	}

	
	private void sendEnabledMessage() {
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN 
				+ "|                                                   |");
		
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|        " + ChatColor.AQUA 
				+ "SketchMap " + this.getDescription().getVersion() 
				+ " has been Enabled!" + ChatColor.GREEN + "          |");
		
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|        " 
		+ ChatColor.AQUA + "  Authors: SlipsWhitley & Fyrinlight" 
				+ ChatColor.GREEN + "       |");
		
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN 
				+ "|                                                   |");
	}


	private void setupCommands() {
		getCommand("sketchmap").setExecutor(new SketchMapCommand());
	}

	private void setupListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	public static SketchMapPlugin getPlugin() {
		return plugin;
	}
	
}
