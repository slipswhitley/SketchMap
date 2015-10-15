package com.mcplugindev.slipswhitley.sketchmap.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.map.RelativeLocation;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;

public class FileManager {
	private SketchMap sketchMap;
	private File mapFile;
	private YamlConfiguration mapConfig;

	public FileManager(final SketchMap sketchMap) {
		this.sketchMap = sketchMap;
		this.mapFile = new File(SketchMapLoader.getMapsDirectory() + "/" + sketchMap.getID() + ".sketchmap");
		if (!this.mapFile.exists()) {
			try {
				this.mapFile.createNewFile();
				this.mapConfig = YamlConfiguration.loadConfiguration(this.mapFile);
			} catch (Exception ex) {
				Bukkit.getLogger().log(Level.WARNING, "[SketchMap] Unable to create/load SketchMap file \""
						+ this.mapFile.getName() + "\" in SketchMaps folder.", ex);
				return;
			}
		}
		try {
			this.mapConfig = YamlConfiguration.loadConfiguration(this.mapFile);
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "[SketchMap] Unable to load SketchMap file \""
					+ this.mapFile.getName() + "\" in SketchMaps folder.", ex);
		}
	}

	public void save() {
		if (this.mapConfig == null) {
			return;
		}
		this.mapConfig.set("x-panes", (Object) this.sketchMap.getLengthX());
		this.mapConfig.set("y-panes", (Object) this.sketchMap.getLengthY());
		this.mapConfig.set("public-protected", (Object) this.sketchMap.isPublicProtected());
		final List<String> mapCollection = new ArrayList<String>();
		for (final RelativeLocation loc : this.sketchMap.getMapCollection().keySet()) {
			mapCollection.add(String.valueOf(loc.toString()) + " "
					+ SketchMapUtils.getMapID(this.sketchMap.getMapCollection().get(loc)));
		}
		this.mapConfig.set("map-collection", (Object) mapCollection);
		this.mapConfig.set("base-format", (Object) this.sketchMap.getBaseFormat().toString());
		this.mapConfig.set("map-image", (Object) SketchMapUtils.imgToBase64String(this.sketchMap.getImage(),
				this.sketchMap.getBaseFormat().getExtension()));
		try {
			this.mapConfig.save(this.mapFile);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "[SketchMap] Unable to save SketchMap file \""
					+ this.mapFile.getName() + "\" in SketchMaps folder.", e);
		}
	}

	public void deleteFile() {
		this.mapFile.delete();
	}
}
