package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapPlugin;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SubCommandCreate extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "create";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.create";
    }

    @Override
    public String getDescription()
    {
        return "Creates a new map from specified URL and Map ID";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap create <map-id> <URL> [<XFRAMES>:<YFRAMES>] [public/private]";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        if (! (sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + prefix + "This command cannot be used from the Console.");
            return;
        }
        final Player player = (Player) sender;
        if (args.length < 2)
        {
            player.sendMessage(ChatColor.RED + prefix + "Error in Command Syntax. Try, \"" + this.getSyntax() + "\"");
            return;
        }
        if (args[0].length() < 3 || args[0].length() > 16)
        {
            player.sendMessage(ChatColor.RED + prefix + "Map ID must be between 3 - 16 Characters");
            return;
        }
        if (! StringUtils.isAlphanumeric(args[0].replace("_", "").replace("-", "")))
        {
            player.sendMessage(ChatColor.RED + prefix + "Map ID must be Alphanumeric");
            return;
        }
        if (SketchMapAPI.getMapByID(args[0]) == null)
        {
            // Since the map doesn't already exist, check player's ownership limits
            if (!SketchMapUtils.checkAllowedMoreMaps(player))
            {
                player.sendMessage(ChatColor.RED + prefix + "You have reached your limit for number of sketchmaps owned. If you want to make another, delete one of your existing maps.");
                return;
            }

            URL url;
            try
            {
                url = new URL(args[1]);
            }
            catch (MalformedURLException ex)
            {
                player.sendMessage(ChatColor.RED + prefix + "Unable to load image. URL appears invalid");
                return;
            }
            Integer xFrames = null;
            Integer yFrames = null;
            SketchMap.PrivacyLevel privacyLevel = SketchMapPlugin.getPlugin().getDefaultPrivacyLevel();
            if (args.length > 2)
            {
                final String[] split = args[2].split(":");
                if (split.length != 2)
                {
                    player.sendMessage(ChatColor.RED + prefix + "Cannot resize image. Invalid resize arguments set. "
                            + this.getSyntax());
                    return;
                }
                try
                {
                    xFrames = Integer.parseInt(split[0]);
                    yFrames = Integer.parseInt(split[1]);
                }
                catch (Exception ex2)
                {
                    player.sendMessage(ChatColor.RED + prefix + "Cannot resize image. Invalid resize arguments set. "
                            + this.getSyntax());
                    return;
                }
                if (xFrames < 1 || yFrames < 1)
                {
                    player.sendMessage(
                            ChatColor.RED + prefix + "Resize image arguments must be positive. " + this.getSyntax());
                    return;
                }
                int limit = SketchMapUtils.checkSizeLimits(player, xFrames, yFrames);
                if (limit > 0)
                {
                    player.sendMessage(ChatColor.RED + prefix + "Image size exceeds maximum frame dimensions. Your maximum sketchmap size is " + limit + "x" + limit + " frames.");
                    return;
                }

                // Check for privacy level arg
                if (args.length > 3)
                {
                    if (SketchMapUtils.hasPermission(player, "sketchmap.privacy"))
                    {
                        if (args[3].equalsIgnoreCase("public"))
                        {
                            privacyLevel = SketchMap.PrivacyLevel.PUBLIC;
                        }
                        else if (args[3].equalsIgnoreCase("private"))
                        {
                            privacyLevel = SketchMap.PrivacyLevel.PRIVATE;
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + prefix + "Privacy level must be 'public' or 'private'. " + this.getSyntax());
                            return;
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED + prefix + "You do not have permission to set privacy levels on sketchmaps.");
                        return;
                    }
                }
            }
            try
            {
                player.sendMessage(ChatColor.AQUA + prefix + "Downloading Image");
                final BufferedImage image = ImageIO.read(url);
                player.sendMessage(ChatColor.AQUA + prefix + "Processing Image");
                //final String ext = url.getFile().substring(url.getFile().length() - 3);
                final SketchMap.BaseFormat format = SketchMap.BaseFormat.PNG;
                if (format == null)
                {
                    player.sendMessage(ChatColor.RED + prefix + "Only JPEG and PNG are supported.");
                    return;
                }
                if (args.length == 2)
                {
                    int imageX = image.getWidth();
                    int imageY = image.getHeight();
                    while (imageX % 128 != 0)
                    {
                        ++ imageX;
                    }
                    while (imageY % 128 != 0)
                    {
                        ++ imageY;
                    }
                    xFrames = imageX / 128;
                    yFrames = imageY / 128;
                }
                int limit = SketchMapUtils.checkSizeLimits(player, xFrames, yFrames);
                if (limit > 0)
                {
                    player.sendMessage(ChatColor.RED + prefix + "Image size exceeds maximum frame dimensions. Your maximum sketchmap size is " + limit + "x" + limit + " frames.");
                    return;
                }
                new SketchMap(image, args[0], player.getUniqueId(), privacyLevel, xFrames, yFrames, false, format);
                player.sendMessage(ChatColor.GREEN + prefix + "Map \"" + ChatColor.GOLD + args[0] + ChatColor.GREEN
                        + "\" Created! " + ChatColor.GOLD + "Use \"/sketchmap get " + args[0] + "\""
                        + " to get this map as map items.");
                player.sendMessage(ChatColor.AQUA + " Or use \"" + ChatColor.GOLD + "/sketchmap place " + args[0]
                        + ChatColor.AQUA + "\" to place it directly onto a wall.");
            }
            catch (IOException e)
            {
                player.sendMessage(ChatColor.RED + prefix + "Unable to load/find image at URL."
                        + " If you think this is a error try uploading this image @ imgur.com.");
            }
            return;
        }
        if (SketchMapAPI.getMapByID(args[0]).isPublicProtected())
        {
            player.sendMessage(ChatColor.RED + prefix + "An External Plugin has reserved "
                    + "that Map ID. Try a different Map ID");
            return;
        }
        player.sendMessage(ChatColor.RED + prefix + "A map by that id already exists.");
    }
}
