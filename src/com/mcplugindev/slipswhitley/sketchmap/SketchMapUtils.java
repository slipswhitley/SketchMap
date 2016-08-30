package com.mcplugindev.slipswhitley.sketchmap;

import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.RegisteredServiceProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class SketchMapUtils
{
    private static Permission permission;

    public static BufferedImage resize(Image img, final Integer width, final Integer height)
    {
        img = img.getScaledInstance(width, height, 4);
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }
        final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
        final Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    public static BufferedImage base64StringToImg(final String imageString)
    {
        BufferedImage image;
        try
        {
            final BASE64Decoder decoder = new BASE64Decoder();
            final byte[] imageByte = decoder.decodeBuffer(imageString);
            final ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return image;
    }

    public static String imgToBase64String(final BufferedImage image, final String type)
    {
        String imageString = null;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(image, type, bos);
            final byte[] imageBytes = bos.toByteArray();
            final BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return imageString;
    }

    public static void sendColoredConsoleMessage(final String msg)
    {
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage(msg);
    }

    protected static boolean setupPermissions()
    {
        final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>) Bukkit
                .getServicesManager().getRegistration((Class) Permission.class);
        if (permissionProvider != null)
        {
            SketchMapUtils.permission = permissionProvider.getProvider();
        }
        return SketchMapUtils.permission != null;
    }

    public static boolean hasPermission(final Player player, final String permission)
    {
        if (permission == null)
        {
            return player.isOp();
        }
        return SketchMapUtils.permission.playerHas(player, permission);
    }

    /**
     * @param player The player to check the sketchmap size limit for.
     * @param x      The X-dimension of the map being compared to the player's size limit.
     * @param y      The Y-dimension of the map being compared to the player's size limit.
     * @return Returns 0 if the map size is lower than the player's limit. Returns a positive number equal to the player's limit (from the config or permissions as applicable) if the map is larger than the player's limit.
     */
    public static int checkSizeLimits(Player player, int x, int y)
    {
        if (player.isOp())
            return 0;

        int largerDimension = Math.max(x, y);
        if (hasPermission(player, "sketchmap.size.defaultexempt"))
        {
            for (int i = 1; i < largerDimension; i++)
            {
                if (hasPermission(player, "sketchmap.size." + i))
                {
                    return i;
                }
            }
        }
        else
        {
            int maxDim = SketchMapPlugin.getPlugin().getMaxDimension();
            if (largerDimension > maxDim)
            {
                return maxDim;
            }
        }

        return 0;
    }

    /**
     * Checks to see if a player owns fewer maps than they are allowed to own (by config or by perms)
     *
     * @param player The player being checked
     * @return True if the player's ownership limit is higher than the number of maps they currently own. False otherwise.
     */
    public static boolean checkAllowedMoreMaps(Player player)
    {
        int ownedMaps = 0;
        for (final SketchMap map : SketchMap.getLoadedMaps())
        {
            if (map.getOwnerUUID().equals(player.getUniqueId()))
                ownedMaps++;
        }

        // If player is exempted from the default limit
        if (hasPermission(player, "sketchmap.ownlimit.defaultexempt"))
        {
            // If player has a limit perm lower than or equal to their current number of maps, not allowed
            for (int i = 1; i <= ownedMaps; i++)
            {
                if (hasPermission(player, "sketchmap.ownlimit." + i))
                    return false;
            }
        }

            // If config has no default limit, allowed (note that the perms will override the config)
        else if (SketchMapPlugin.getPlugin().getMaxOwnedMaps() == 0)
        {
            return true;
        }
        else
        {
            // If player owns maps greater than the default limit and there is an actual default limit, not allowed
            if (SketchMapPlugin.getPlugin().getMaxOwnedMaps() != 0)
                if (ownedMaps > SketchMapPlugin.getPlugin().getMaxOwnedMaps())
                    return false;
        }

        // If nothing else, allowed
        return true;
    }

    public static short getMapID(final MapView map)
    {
        return map.getId();
    }

    public static MapView getMapView(final short id)
    {
        final MapView map = Bukkit.getMap(id);
        if (map != null)
        {
            return map;
        }
        return Bukkit.createMap(getDefaultWorld());
    }

    public static Block getTargetBlock(final Player player, final int i)
    {
        return player.getTargetBlock((Set) null, i);
    }

    public static World getDefaultWorld()
    {
        return Bukkit.getWorlds().get(0);
    }

    public static UUID nameToUUID(String name)
    {
        return Bukkit.getServer().getOfflinePlayer(name).getUniqueId();
    }

    public static String uuidToName(UUID uuid)
    {
        return Bukkit.getServer().getOfflinePlayer(uuid).getName();
    }
}
