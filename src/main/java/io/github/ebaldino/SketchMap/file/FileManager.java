package io.github.ebaldino.SketchMap.file;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.ebaldino.SketchMap.SketchMapUtils;
import io.github.ebaldino.SketchMap.map.RelativeLocation;
import io.github.ebaldino.SketchMap.map.SketchMap;

public class FileManager {

	private SketchMap sketchMap;
	
	private File mapFile;
	private YamlConfiguration mapConfig;
	
	public FileManager(SketchMap sketchMap) {
		this.sketchMap = sketchMap;	
		
		mapFile = new File(SketchMapLoader.getMapsDirectory() + "/" + sketchMap.getID() + ".sketchmap");

		if(!mapFile.exists()) {
			try {
				mapFile.createNewFile();
				mapConfig = YamlConfiguration.loadConfiguration(mapFile);
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.WARNING, 
						"[SketchMap] Unable to create/load SketchMap file \"" + mapFile.getName() + "\" in SketchMaps folder.", ex);
				return;
			}	
		}

		try {
		    SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "Loading map file: " + mapFile);
			mapConfig = YamlConfiguration.loadConfiguration(mapFile);
		}
		catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, 
					"[SketchMap] Unable to load SketchMap file \"" + mapFile.getName() + "\" in SketchMaps folder.", ex);
		}
	}

	public void save() {
	    // save() is quick

		if(mapConfig == null) {
			return;
		}
		
		mapConfig.set("x-panes", sketchMap.getLengthX());
		mapConfig.set("y-panes", sketchMap.getLengthY());
		mapConfig.set("public-protected", sketchMap.isPublicProtected());
		
		List<String> mapCollection = new ArrayList<String>();
		for(RelativeLocation loc : sketchMap.getMapCollection().keySet()) {
			mapCollection.add(loc.toString() + " " + SketchMapUtils.getMapID(sketchMap.getMapCollection().get(loc)));
		}

		mapConfig.set("map-collection", mapCollection);
		mapConfig.set("base-format", sketchMap.getBaseFormat().toString());
		
		// First make sure the image doesn't have any transparent pixels
		BufferedImage img = sketchMap.getImage();
		if( img.getColorModel().getTransparency() != Transparency.OPAQUE) {
		    img = SketchMapUtils.fillTransparentPixels(img, Color.WHITE);
		}		    
		mapConfig.set("map-image", SketchMapUtils.imgToBase64String(img, sketchMap.getBaseFormat().getExtension()));
		//mapConfig.set("map-image", SketchMapUtils.imgToBase64String(sketchMap.getImage(), sketchMap.getBaseFormat().getExtension()));
		
		try {
			mapConfig.save(mapFile);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, 
					"[SketchMap] Unable to save SketchMap file \"" + mapFile.getName() + "\" in SketchMaps folder.", e);
		}
		
	}

	public void deleteFile() {
		mapFile.delete();
	}

}
