package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubCommandList extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "list";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.list";
    }

    @Override
    public String getDescription()
    {
        return "List all current loaded maps";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap list [playername/#] -- /sketchmap list <playername> [#]";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        final List<String> maps = new ArrayList<>();
        if (SketchMap.getLoadedMaps().isEmpty())
        {
            sender.sendMessage(ChatColor.RED + prefix + "No maps have been loaded to list.");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + prefix + "Loading list of sketchmaps...");

        if (args.length == 0)
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                for (final SketchMap map : SketchMap.getLoadedMaps())
                {
                    if ((map.getPrivacyLevel() == SketchMap.PrivacyLevel.PRIVATE &&
                            !map.getOwnerUUID().equals(player.getUniqueId()) &&
                            !map.getAllowedUUID().contains(player.getUniqueId()) &&
                            !SketchMapUtils.hasPermission(player, "sketchmap.privacy.admin")))
                        continue;
                    if (map.isPublicProtected())
                        continue;
                    maps.add(map.getID());
                }
            }
            else
            {
                for (final SketchMap map : SketchMap.getLoadedMaps())
                {
                    if (map.isPublicProtected())
                        continue;
                    maps.add(map.getID());
                }
            }
        }
        else if (args.length == 1)
        {
            boolean pageNumber = true;

            for (char c : args[0].toCharArray())
            {
                if (!Character.isDigit(c) && c != '-')
                {
                    if (!Character.isAlphabetic(c) && c != '_')
                    {
                        sender.sendMessage(ChatColor.RED + prefix + "Error in Command Syntax. Try, \"" + this.getSyntax() + "\"");
                        return;
                    }
                    pageNumber = false;
                    break;
                }
            }

            if (pageNumber)
            {
                if (sender instanceof Player)
                {
                    Player player = (Player) sender;
                    for (final SketchMap map : SketchMap.getLoadedMaps())
                    {
                        if ((map.getPrivacyLevel() == SketchMap.PrivacyLevel.PRIVATE &&
                                !map.getOwnerUUID().equals(player.getUniqueId()) &&
                                !map.getAllowedUUID().contains(player.getUniqueId()) &&
                                !SketchMapUtils.hasPermission(player, "sketchmap.privacy.admin")))
                            continue;
                        if (map.isPublicProtected())
                            continue;
                        maps.add(map.getID());
                    }
                }
                else
                {
                    for (final SketchMap map : SketchMap.getLoadedMaps())
                    {
                        if (map.isPublicProtected())
                            continue;
                        maps.add(map.getID());
                    }
                }
            }
            else
            {
                OfflinePlayer listPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);

                if (listPlayer == null)
                {
                    sender.sendMessage(ChatColor.RED + prefix + "Couldn't find player '" + args[0] + "'");
                    return;
                }

                if (sender instanceof Player && !listPlayer.getUniqueId().equals(((Player) sender).getUniqueId()) &&
                        !SketchMapUtils.hasPermission(((Player) sender), "sketchmap.privacy.admin"))
                {
                    sender.sendMessage(ChatColor.RED + prefix + "You don't have permission to do that");
                    return;
                }

                for (final SketchMap map : SketchMap.getLoadedMaps())
                {
                    if (!map.getOwnerUUID().equals(listPlayer.getUniqueId()))
                        continue;
                    if (map.isPublicProtected())
                        continue;
                    maps.add(map.getID());
                }
            }
        }
        else
        {
            OfflinePlayer listPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);

            if (listPlayer == null)
            {
                sender.sendMessage(ChatColor.RED + prefix + "Couldn't find player '" + args[0] + "'");
                return;
            }

            if (sender instanceof Player && !listPlayer.getUniqueId().equals(((Player) sender).getUniqueId()) &&
                    !SketchMapUtils.hasPermission(((Player) sender), "sketchmap.privacy.admin"))
            {
                sender.sendMessage(ChatColor.RED + prefix + "You don't have permission to do that");
                return;
            }

            for (final SketchMap map : SketchMap.getLoadedMaps())
            {
                if (!map.getOwnerUUID().equals(listPlayer.getUniqueId()))
                    continue;
                if (map.isPublicProtected())
                    continue;
                maps.add(map.getID());
            }
        }

        Collections.sort(maps);

        int pageSize = 15;
        int numPages = (maps.size() / pageSize) + (maps.size() % pageSize == 0 ? 0 : 1); // This works the same as array length (last page number is one less than number of pages, yay zero!)

        int pageToShow;
        if (args.length == 0)
            pageToShow = 0;
        else if (args.length == 1)
        {
            boolean pageNum = true;
            for (char c : args[0].toCharArray())
            {
                if (!Character.isDigit(c) && c != '-')
                {
                    if (!Character.isAlphabetic(c) && c != '_')
                    {
                        sender.sendMessage(ChatColor.RED + prefix + "Error in Command Syntax. Try, \"" + this.getSyntax() + "\"");
                        return;
                    }
                    pageNum = false;
                    break;
                }
            }
            if (pageNum)
                pageToShow = Integer.parseInt(args[0]) - 1;
            else
                pageToShow = 0;
        }
        else
        {
            try
            {
                pageToShow = Integer.parseInt(args[1]) - 1;
            }
            catch (NumberFormatException e)
            {
                sender.sendMessage(ChatColor.RED + prefix + "'" + args[1] + "' is not a page number. Try again.");
                return;
            }
        }

        if (pageToShow >= numPages)
            pageToShow = numPages - 1;
        if (pageToShow < 0)
            pageToShow = 0;

        int pageStart = pageSize * pageToShow;
        int pageEnd = pageSize * (pageToShow + 1) - 1;
        if (pageEnd > maps.size())
            pageEnd = maps.size() - 1;

        sender.sendMessage(ChatColor.RED + prefix + ChatColor.GRAY + "Displaying page " + (pageToShow + 1) + " of " + numPages);
        for (int mapIndex = pageStart; mapIndex <= pageEnd; mapIndex++)
        {
            sender.sendMessage(ChatColor.GREEN + "- " + ChatColor.AQUA + maps.get(mapIndex));
        }
        if (pageToShow + 1 < numPages)
            sender.sendMessage(ChatColor.RED + "Type " + ChatColor.BLUE + "/sketchmap list " + (args.length != 0 && args[0].equalsIgnoreCase("mine") ? "mine " : "") + (pageToShow + 2) + ChatColor.RED + " to see the next page");
    }
}
