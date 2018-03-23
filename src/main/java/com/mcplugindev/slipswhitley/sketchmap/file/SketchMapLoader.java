package com.mcplugindev.slipswhitley.sketchmap.file;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapPlugin;

public class SketchMapLoader {

	private static File mapsDirectory;
	private static File dataFolder;
	
	public static File getDataFolder() {
		if(dataFolder != null) {
			return dataFolder;
		}
		
		dataFolder = SketchMapPlugin.getPlugin().getDataFolder();
		
		if(dataFolder.exists()) {
			return dataFolder;
		}
		
		dataFolder.mkdirs();
		return dataFolder;
	}
	
	public static File getMapsDirectory() {
		if(mapsDirectory != null) {
			return mapsDirectory;
		}
		
		mapsDirectory = new File(getDataFolder().toString() + "/" + "sketchmaps/");
		
		if(mapsDirectory.exists()) {
			return mapsDirectory;
		}
		
		mapsDirectory.mkdirs();
		return mapsDirectory;
	}
	
	public static void loadMaps() {
		for(File file : getMapsDirectory().listFiles()) {
			if(!file.getName().endsWith(".sketchmap")) {
				continue;
			}
			
			try {
				SketchMapAPI.loadSketchMapFromFile(file);
			}
			catch (SketchMapFileException ex) {
				Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
			}
			
		}
	}
	
	
	
	
	
}
