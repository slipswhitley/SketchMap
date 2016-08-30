package com.mcplugindev.slipswhitley.sketchmap.file;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapPlugin;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.logging.Level;

public class SketchMapLoader
{
    private static File mapsDirectory;
    private static File dataFolder;

    public static File getDataFolder()
    {
        if (SketchMapLoader.dataFolder != null)
        {
            return SketchMapLoader.dataFolder;
        }
        SketchMapLoader.dataFolder = SketchMapPlugin.getPlugin().getDataFolder();
        if (SketchMapLoader.dataFolder.exists())
        {
            return SketchMapLoader.dataFolder;
        }
        SketchMapLoader.dataFolder.mkdirs();
        return SketchMapLoader.dataFolder;
    }

    public static File getMapsDirectory()
    {
        if (SketchMapLoader.mapsDirectory != null)
        {
            return SketchMapLoader.mapsDirectory;
        }
        SketchMapLoader.mapsDirectory = new File(String.valueOf(getDataFolder().toString()) + "/" + "sketchmaps/");
        if (SketchMapLoader.mapsDirectory.exists())
        {
            return SketchMapLoader.mapsDirectory;
        }
        SketchMapLoader.mapsDirectory.mkdirs();
        return SketchMapLoader.mapsDirectory;
    }

    public static void loadMaps()
    {
        File[] listFiles = getMapsDirectory().listFiles();
        int failed = 0;
        for (int i = 0; i < listFiles.length; i++)
        {
            File file = listFiles[i];
            if (file.getName().endsWith(".sketchmap"))
            {
                try
                {
                    SketchMapAPI.loadSketchMapFromFile(file);
                }
                catch (SketchMapFileException ex)
                {
                    Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
                    failed++;
                }
            }
            if (i % 10 == 9)
                SketchMapUtils.sendColoredConsoleMessage(ChatColor.YELLOW + "[SketchMap] " + (i + 1 - failed) + "/" + listFiles.length + " sketchmaps loaded.");
        }
        SketchMapUtils.sendColoredConsoleMessage(ChatColor.YELLOW + "[SketchMap] Finished! " + (listFiles.length - failed) + " sketchmaps loaded");
    }

    public static void unloadMaps()
    {
        SketchMap.disable();
    }
}
