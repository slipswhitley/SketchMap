package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SubCommandInfo extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "info";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.list";
    }

    @Override
    public String getDescription()
    {
        return "Lists information about the specified sketchmap";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap info <MAP-ID>";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        if (args.length != 1)
        {
            sender.sendMessage(ChatColor.RED + prefix + "Invalid command Arguments. " + "Try, \"" + this.getSyntax() + "\"");
            return;
        }
        SketchMap map = SketchMapAPI.getMapByID(args[0]);
        if (map == null)
        {
            sender.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" + args[0].toLowerCase() + "\"");
            return;
        }

        final String ownerName = SketchMapUtils.uuidToName(map.getOwnerUUID());
        final int xDim = map.getLengthX();
        final int yDim = map.getLengthY();
        final List<UUID> allowedUUID = map.getAllowedUUID();
        final StringBuilder allowedNames = new StringBuilder();
        for (UUID uuid : allowedUUID)
        {
            allowedNames.append(SketchMapUtils.uuidToName(uuid));
            allowedNames.append(", ");
        }
        if (allowedUUID.size() != 0)
        {
            allowedNames.deleteCharAt(allowedNames.length() - 1);
            allowedNames.deleteCharAt(allowedNames.length() - 1);
        }
        else
        {
            allowedNames.append("<none>");
        }

        sender.sendMessage(ChatColor.RED + prefix + "Info for map \"" + map.getID() + "\"");
        sender.sendMessage(ChatColor.GREEN + "Owner: " + ChatColor.AQUA + ownerName);
        sender.sendMessage(ChatColor.GREEN + "Size : " + ChatColor.AQUA + xDim + "x" + yDim);
        if (sender instanceof Player)
        {
            final Player player = (Player) sender;
            if (!map.getOwnerUUID().equals(player.getUniqueId()))
                if (!SketchMapUtils.hasPermission(player, "sketchmap.privacy.admin"))
                    return; // Don't display list of allowed players to players that aren't the owner or who lack sketchmap.list.admin
        }

        // List will display to console, the sketchmap's owner, and players with sketchmap.list.admin
        if (map.getPrivacyLevel() == SketchMap.PrivacyLevel.PRIVATE)
            sender.sendMessage(ChatColor.GREEN + "Allowed players: " + ChatColor.AQUA + allowedNames);
        else
            sender.sendMessage(ChatColor.GREEN + "This map is publicly available.");
    }
}
