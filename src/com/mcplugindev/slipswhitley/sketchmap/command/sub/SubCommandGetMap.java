package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.RelativeLocation;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SubCommandGetMap extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "getmap";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.getmap";
    }

    @Override
    public String getDescription()
    {
        return "Gives the player a single map item from the specified Sketchmap. The player must have a blank map in their inventory to use this command. Supports survival mode.";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap getmap <MAP-ID> <<X>:<Y>> (NOTE: coordinates start at ONE)";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + prefix + "Command cannot be used " + "from the console.");
            return;
        }
        Player player = (Player) sender;
        if (args.length != 2)
        {
            player.sendMessage(ChatColor.RED + prefix + "Invalid command Arguments. " + "Try, \"" + this.getSyntax() + "\"");
            return;
        }

        SketchMap map = SketchMapAPI.getMapByID(args[0]);
        if (map == null)
        {
            player.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" + args[0].toLowerCase() + "\"");
            return;
        }

        Set<RelativeLocation> coordsList = map.getMapCollection().keySet();
        RelativeLocation lastCoord = new RelativeLocation(0, 0);
        for (RelativeLocation coord : coordsList)
        {
            if (coord.getX() > lastCoord.getX() || coord.getY() > lastCoord.getY())
                lastCoord = coord;
        }
        RelativeLocation mapCoordToGet = RelativeLocation.fromStringOneBase(args[1]);
        if (mapCoordToGet == null || mapCoordToGet.getX() > lastCoord.getX() || mapCoordToGet.getY() > lastCoord.getY())
        {
            player.sendMessage(ChatColor.RED + prefix + "Invalid coordinates for map " + args[0].toLowerCase() + ": " + args[1] + ". Check the map's dimensions with /sketchmap info <MAP_ID>.");
            return;
        }
        for (RelativeLocation location : map.getMapCollection().keySet())
        {
            if (mapCoordToGet.equals(location))
            {
                mapCoordToGet = location;
                break;
            }
        }

        PlayerInventory playerInventory = player.getInventory();
        HashMap<Integer, ItemStack> notRemoved = playerInventory.removeItem(new ItemStack(Material.EMPTY_MAP, 1));
        if (!notRemoved.isEmpty())
        {
            player.sendMessage(ChatColor.RED + prefix + "You need to have an empty map in your inventory to use this command!");
            return;
        }

        final ItemStack mapStack = new ItemStack(Material.MAP, 1);
        mapStack.setDurability(SketchMapUtils.getMapID(map.getMapCollection().get(mapCoordToGet)));
        final ItemMeta iMeta = mapStack.getItemMeta();
        iMeta.setDisplayName(ChatColor.GREEN + "SketchMap ID: " + ChatColor.GOLD + map.getID()
                + ChatColor.GREEN + " Pos-X: " + ChatColor.GOLD + (mapCoordToGet.getX() + 1) + ChatColor.GREEN + " Pos-Y: "
                + ChatColor.GOLD + (mapCoordToGet.getY() + 1));
        iMeta.setLore((List) Collections.singletonList(ChatColor.GRAY + "SketchMap ID: " + map.getID() + ", X:" + (mapCoordToGet.getX() + 1) + ", Y:" + (mapCoordToGet.getY() + 1)));
        mapStack.setItemMeta(iMeta);

        HashMap<Integer, ItemStack> notAdded = playerInventory.addItem(mapStack);
        if (!notAdded.isEmpty())
        {
            player.sendMessage(ChatColor.RED + prefix + "You need an empty slot in your inventory to put the map item in!");
            HashMap<Integer, ItemStack> notAdded2 = playerInventory.addItem(new ItemStack(Material.EMPTY_MAP, 1));
            if (!notAdded2.isEmpty())
                Bukkit.getLogger().severe("No empty slots for the sketchmap, but no empty slots to give back the empty map! This shouldn't be possible! Please contact the developer soon!");
        }
        else
        {
            player.sendMessage(ChatColor.GREEN + prefix + "Section " + args[1] + " of Sketchmap " + args[0].toLowerCase() + " is now in your inventory.");
        }
    }
}
