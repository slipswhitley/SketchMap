package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandDelete extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "delete";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.delete";
    }

    @Override
    public String getDescription()
    {
        return "Unload and Delete a SketchMap";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap delete <map-id>";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        if (args.length != 1)
        {
            sender.sendMessage(
                    ChatColor.RED + prefix + "Invalid command Arguments. " + "Try, \"" + this.getSyntax() + "\"");
            return;
        }
        final SketchMap map = SketchMapAPI.getMapByID(args[0]);
        if (map == null)
        {
            sender.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" + args[0].toLowerCase() + "\"");
            return;
        }
        if (map.isPublicProtected())
        {
            sender.sendMessage(ChatColor.RED + prefix + "An External Plugin has requested that"
                    + " this map is protected from public access.");
            return;
        }
        if (sender instanceof Player && !map.getOwnerUUID().equals(((Player) sender).getUniqueId()))
        {
            if (!SketchMapUtils.hasPermission((Player) sender, "sketchmap.delete.admin"))
            {
                sender.sendMessage(ChatColor.RED + prefix + "You aren't allowed to delete map " + map.getID() + " because you aren't its owner.");
                return;
            }
        }
        final String mapID = map.getID();
        map.delete();
        sender.sendMessage(ChatColor.AQUA + prefix + "Map \"" + mapID + "\" deleted.");
    }
}
