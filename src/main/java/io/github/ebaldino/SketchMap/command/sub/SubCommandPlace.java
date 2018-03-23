package io.github.ebaldino.SketchMap.command.sub;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.bukkit.map.MapView;

import io.github.ebaldino.SketchMap.SketchMapAPI;
import io.github.ebaldino.SketchMap.SketchMapUtils;
import io.github.ebaldino.SketchMap.command.SketchMapSubCommand;
import io.github.ebaldino.SketchMap.map.RelativeLocation;
import io.github.ebaldino.SketchMap.map.SketchMap;

public class SubCommandPlace extends SketchMapSubCommand {

	@Override
	public String getSub() {
		return "place";
	}

	@Override
	public String getPermission() {
		return "sketchmap.place";
	}
	
	@Override
	public String getDescription() {
		return "Places a sketchmap at a target area.";
	}

	@Override
	public String getSyntax() {
		return "/sketchmap place <map-id>";
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args, String prefix) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + prefix + "Command cannot be used "
					+ "from the console.");
			return;
		}
		
		if(args.length != 2) {
			sender.sendMessage(ChatColor.RED + prefix + "Invalid command Arguments. "
					+ "Try, \"" + getSyntax() + "\"");
			return;
		}
		
		SketchMap map = SketchMapAPI.getMapByID(args[1]);
		
		if(map == null) {
			sender.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" 
					+ args[1].toLowerCase() + "\"");
			return;
		}
		
		if(map.isPublicProtected()) {
			sender.sendMessage(ChatColor.RED + prefix + "An External Plugin has requested that"
					+ " this map is protected from public access.");
			return;
		}
		
		Player player = (Player) sender;
		
		Block targetBlock = SketchMapUtils.getTargetBlock(player, 40);
		if(targetBlock.getType() == Material.AIR) {
			player.sendMessage(ChatColor.RED + prefix + "Could not find target block. Ensure you are looking"
					+ " at a wall before using this command.");
			return;
		}
		
		String direction = getDirection(player);
		BlockFace face = getBlockFace(direction);
		
		World world = targetBlock.getWorld();
		int x = targetBlock.getX();
		int y = targetBlock.getY();
		int z = targetBlock.getZ();
		
		Set<ItemFrame> iFrames = new HashSet<ItemFrame>();
		
		Map<RelativeLocation, MapView> mapCollection = map.getMapCollection();
		 
		for(RelativeLocation relLoc : mapCollection.keySet()) {
			MapView mapView = mapCollection.get(relLoc);
			
			Location loc = null;
			Location backLoc = null;
			
			if(direction.equalsIgnoreCase("north")) {
				loc = new Location(world, x + relLoc.getX(), y - relLoc.getY() , z + 1);
				backLoc = new Location(world, x + relLoc.getX(), y - relLoc.getY() , z);
			}
			if(direction.equalsIgnoreCase("south")) {
				loc = new Location(world, x - relLoc.getX(), y - relLoc.getY() , z - 1);
				backLoc = new Location(world, x - relLoc.getX(), y - relLoc.getY() , z);
			}
			if(direction.equalsIgnoreCase("east")) {
				loc = new Location(world, x - 1 , y - relLoc.getY() , z + relLoc.getX());
				backLoc = new Location(world, x , y - relLoc.getY() , z + relLoc.getX());
			}
			if(direction.equalsIgnoreCase("west")) {
				loc = new Location(world, x + 1, y - relLoc.getY() , z - relLoc.getX());
				backLoc = new Location(world, x, y - relLoc.getY() , z - relLoc.getX());
			}
			
			if(loc == null) {
				return;
			}
			
			if(loc.getBlock().getType() != Material.AIR || backLoc.getBlock().getType() == Material.AIR) {
				player.sendMessage(ChatColor.RED + prefix + "There is not enough room on that wall to place that Sketch Map");
				for(ItemFrame iFrame : iFrames) {
					iFrame.remove();
				}
				return;
			}
			
			try {
				ItemFrame iFrame = (ItemFrame) world.spawnEntity(loc, EntityType.ITEM_FRAME);
				iFrame.setFacingDirection(face);
				
				ItemStack iStack = new ItemStack(Material.MAP, 1);
				iStack.setDurability(SketchMapUtils.getMapID(mapView));
				
				iFrame.setItem(iStack);
				iFrames.add(iFrame);
				
			}
			catch(Exception ex) {
				for(ItemFrame iFrame : iFrames) {
					iFrame.remove();
				}
				
				player.sendMessage(ChatColor.RED + prefix + "Unable to place image on that surface.");
				return;
			}
		}
		
		
		player.sendMessage(ChatColor.GREEN + prefix + "Placed SketchMap \"" + ChatColor.GOLD
				+ map.getID() + ChatColor.GREEN + "\" at Target Block");
		
		
		
		
	}
	private BlockFace getBlockFace(String direction) {
		switch(direction) {
		case "north":
			return BlockFace.SOUTH;
		case "south":
			return BlockFace.NORTH;
		case "east":
			return BlockFace.WEST;
		case "west":
			return BlockFace.EAST;
		}
		
		return BlockFace.NORTH;
	}

	
	private String getDirection(Player player) {
        int degrees = (Math.round(player.getLocation().getYaw()) + 270) % 360;
        if (degrees <= 22) return "west";
        if (degrees <= 112) return "north";
        if (degrees <= 202) return "east";
        if (degrees <= 292) return "south";
        if (degrees <= 359) return "west";
        return null;
    }

}
