package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.RelativeLocation;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SubCommandPlace extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "place";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.place";
    }

    @Override
    public String getDescription()
    {
        return "Places a sketchmap at a target area.";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap place <map-id>";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + prefix + "Command cannot be used " + "from the console.");
            return;
        }
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
        final Player player = (Player) sender;
        if (map.getPrivacyLevel() == SketchMap.PrivacyLevel.PRIVATE &&
                !SketchMapUtils.hasPermission(player, "sketchmap.privacy.admin") &&
                !map.getOwnerUUID().equals(player.getUniqueId()) &&
                !map.getAllowedUUID().contains(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + prefix + "You don't have permission to place map " + map.getID() + ".");
            return;
        }

        final Block targetBlock = SketchMapUtils.getTargetBlock(player, 40);
        if (targetBlock.getType() == Material.AIR)
        {
            player.sendMessage(ChatColor.RED + prefix + "Could not find target block. Ensure you are looking"
                    + " at a wall before using this command.");
            return;
        }
        final String direction = this.getDirection(player);
        final BlockFace face = this.getBlockFace(direction);
        final World world = targetBlock.getWorld();
        final int x = targetBlock.getX();
        final int y = targetBlock.getY();
        final int z = targetBlock.getZ();
        //final Set<ItemFrame> iFrames = new HashSet<>();
        final Map<RelativeLocation, MapView> mapCollection = map.getMapCollection();
        for (final RelativeLocation relLoc : mapCollection.keySet())
        {
            final MapView mapView = mapCollection.get(relLoc);
            Location loc = null;
            Location backLoc = null;
            if (direction.equalsIgnoreCase("north"))
            {
                loc = new Location(world, (double) (x + relLoc.getX()), (double) (y - relLoc.getY()), (double) (z + 1));
                backLoc = new Location(world, (double) (x + relLoc.getX()), (double) (y - relLoc.getY()), (double) z);
            }
            if (direction.equalsIgnoreCase("south"))
            {
                loc = new Location(world, (double) (x - relLoc.getX()), (double) (y - relLoc.getY()), (double) (z - 1));
                backLoc = new Location(world, (double) (x - relLoc.getX()), (double) (y - relLoc.getY()), (double) z);
            }
            if (direction.equalsIgnoreCase("east"))
            {
                loc = new Location(world, (double) (x - 1), (double) (y - relLoc.getY()), (double) (z + relLoc.getX()));
                backLoc = new Location(world, (double) x, (double) (y - relLoc.getY()), (double) (z + relLoc.getX()));
            }
            if (direction.equalsIgnoreCase("west"))
            {
                loc = new Location(world, (double) (x + 1), (double) (y - relLoc.getY()), (double) (z - relLoc.getX()));
                backLoc = new Location(world, (double) x, (double) (y - relLoc.getY()), (double) (z - relLoc.getX()));
            }
            if (loc == null)
            {
                return;
            }
            if (loc.getBlock().getType() != Material.AIR || backLoc.getBlock().getType() == Material.AIR)
            {
                player.sendMessage(ChatColor.RED + prefix + "There is not enough room on that wall to place that Sketch Map");
                //for(ItemFrame itemFrame : iFrames)
                //    iFrames.remove(itemFrame);
                return;
            }
            try
            {
                final ItemFrame iFrame = (ItemFrame) world.spawnEntity(loc, EntityType.ITEM_FRAME);
                iFrame.setFacingDirection(face);
                final ItemStack iStack = new ItemStack(Material.MAP, 1);
                iStack.setDurability(SketchMapUtils.getMapID(mapView));
                final ItemMeta iMeta = iStack.getItemMeta();
                //iMeta.setDisplayName(ChatColor.GREEN + "SketchMap ID: " + ChatColor.GOLD + map.getID() +
                //        ChatColor.GREEN + " Pos-X: " + ChatColor.GOLD + relLoc.getX() +
                //        ChatColor.GREEN + " Pos-Y: " + ChatColor.GOLD + relLoc.getY());
                iMeta.setLore((List) Collections.singletonList(ChatColor.GRAY + "SketchMap ID: " + map.getID() + ", X:" + (relLoc.getX() + 1) + ", Y:" + (relLoc.getY() + 1)));
                iStack.setItemMeta(iMeta);
                iFrame.setItem(iStack);
                //iFrames.add(iFrame);
            }
            catch (Exception ex)
            {
                //for(ItemFrame itemFrame : iFrames)
                //    iFrames.remove(itemFrame);
                player.sendMessage(ChatColor.RED + prefix + "Unable to place image on that surface.");
                return;
            }
        }
        player.sendMessage(ChatColor.GREEN + prefix + "Placed SketchMap \"" + ChatColor.GOLD + map.getID()
                + ChatColor.GREEN + "\" at Target Block");
    }

    private BlockFace getBlockFace(final String direction)
    {
        switch (direction)
        {
            case "east":
            {
                return BlockFace.WEST;
            }
            case "west":
            {
                return BlockFace.EAST;
            }
            case "north":
            {
                return BlockFace.SOUTH;
            }
            case "south":
            {
                return BlockFace.NORTH;
            }
            default:
                break;
        }
        return BlockFace.NORTH;
    }

    private String getDirection(final Player player)
    {
        final int degrees = (Math.round(player.getLocation().getYaw()) + 270) % 360;
        if (degrees <= 22)
        {
            return "west";
        }
        else if (degrees <= 112)
        {
            return "north";
        }
        else if (degrees <= 202)
        {
            return "east";
        }
        else if (degrees <= 292)
        {
            return "south";
        }
        else
        {
            return "west";
        }
    }
}
