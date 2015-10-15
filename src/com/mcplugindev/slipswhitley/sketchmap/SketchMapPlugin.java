package com.mcplugindev.slipswhitley.sketchmap;

import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapCommand;
import com.mcplugindev.slipswhitley.sketchmap.file.SketchMapLoader;
import com.mcplugindev.slipswhitley.sketchmap.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SketchMapPlugin extends JavaPlugin {
	private static SketchMapPlugin plugin;
    private FileConfiguration config = getConfig();

    private static int maxDimension;

	public void onEnable() {
		SketchMapPlugin.plugin = this;
		if(!SketchMapUtils.setupPermissions())
            SketchMapUtils.sendColoredConsoleMessage(ChatColor.RED + "CAUTION: No permissions provider detected. Any op can create sketchmaps of any size, but very large ones are NOT recommended!");
        this.setupConfig();
        this.loadConfig();
		this.setupCommands();
		this.setupListeners();
		SketchMapLoader.loadMaps();
		this.sendEnabledMessage();
	}

    private void loadConfig() {
        maxDimension = config.getInt("default-max-dimension");
    }

    private void sendEnabledMessage() {
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|                                                   |");
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|        "
                                                         + ChatColor.AQUA + " SketchMap "
                                                     + this.getDescription().getVersion() + " has been Enabled!" // M.m.b
                                                                                          + ChatColor.GREEN + "         |");
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|        "
                                                         + ChatColor.AQUA + " Authors: SlipsWhitley & Fyrinlight"
                                                                                           + ChatColor.GREEN + "        |");
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|        "
                                                         + ChatColor.AQUA + "Updated and modified by Trainphreak"
                                                                                           + ChatColor.GREEN + "        |");
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|                                                   |");
	}

	private void setupCommands() {
		this.getCommand("sketchmap").setExecutor(new SketchMapCommand());
	}

    private void setupConfig() {
        config.addDefault("default-max-dimension", 5);
        config.options().copyDefaults(true);
        saveConfig();
    }

	private void setupListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public static SketchMapPlugin getPlugin() {
		return SketchMapPlugin.plugin;
	}

    public static int getMaxDimension()
    {
        return maxDimension;
    }
}
