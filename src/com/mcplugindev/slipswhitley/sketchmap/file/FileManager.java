package com.mcplugindev.slipswhitley.sketchmap.file;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FileManager
{
    private final SketchMap sketchMap;
    private final File mapFile;
    private YamlConfiguration mapConfig;

    public FileManager(final SketchMap sketchMap)
    {
        this.sketchMap = sketchMap;
        this.mapFile = new File(SketchMapLoader.getMapsDirectory() + "/" + sketchMap.getID() + ".sketchmap");
        if (!this.mapFile.exists())
        {
            try
            {
                this.mapFile.createNewFile();
                this.mapConfig = YamlConfiguration.loadConfiguration(this.mapFile);
            }
            catch (Exception ex)
            {
                Bukkit.getLogger().log(Level.WARNING, "[SketchMap] Unable to create/load SketchMap file \""
                        + this.mapFile.getName() + "\" in SketchMaps folder.", ex);
                return;
            }
        }
        try
        {
            this.mapConfig = YamlConfiguration.loadConfiguration(this.mapFile);
        }
        catch (Exception ex)
        {
            Bukkit.getLogger().log(Level.WARNING, "[SketchMap] Unable to load SketchMap file \""
                    + this.mapFile.getName() + "\" in SketchMaps folder.", ex);
        }
    }

    public void save()
    {
        if (this.mapConfig == null)
        {
            return;
        }
        this.mapConfig.set("owner-uuid", this.sketchMap.getOwnerUUID().toString());
        this.mapConfig.set("privacy-level", this.sketchMap.getPrivacyLevel().toString());
        final List<String> allowedUUID = new ArrayList<>();
        this.sketchMap.getAllowedUUID().stream().forEach(uuid -> allowedUUID.add(uuid.toString()));
        this.mapConfig.set("allowed-uuids", allowedUUID);
        this.mapConfig.set("x-panes", this.sketchMap.getLengthX());
        this.mapConfig.set("y-panes", this.sketchMap.getLengthY());
        this.mapConfig.set("public-protected", this.sketchMap.isPublicProtected());
        final List<String> mapCollection = new ArrayList<>();
        this.sketchMap.getMapCollection().keySet().stream().forEach(relativeLocation ->
                mapCollection.add(relativeLocation.toString() + " " +
                        SketchMapUtils.getMapID(this.sketchMap.getMapCollection().get(relativeLocation))));
        this.mapConfig.set("map-collection", mapCollection);
        this.mapConfig.set("base-format", SketchMap.BaseFormat.PNG.toString());
        this.mapConfig.set("map-image", SketchMapUtils.imgToBase64String(this.sketchMap.getImage(),
                SketchMap.BaseFormat.PNG.getExtension()));
        try
        {
            this.mapConfig.save(this.mapFile);
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.WARNING, "[SketchMap] Unable to save SketchMap file \""
                    + this.mapFile.getName() + "\" in SketchMaps folder.", e);
        }
    }

    public void deleteFile()
    {
        this.mapFile.delete();
    }
}
