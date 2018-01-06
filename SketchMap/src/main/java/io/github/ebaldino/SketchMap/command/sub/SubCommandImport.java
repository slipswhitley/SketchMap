package io.github.ebaldino.SketchMap.command.sub;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.ebaldino.SketchMap.SketchMapAPI;
import io.github.ebaldino.SketchMap.command.SketchMapSubCommand;
import io.github.ebaldino.SketchMap.file.SketchMapLoader;
import io.github.ebaldino.SketchMap.map.SketchMap;
import io.github.ebaldino.SketchMap.map.SketchMap.BaseFormat;

public class SubCommandImport extends SketchMapSubCommand {

	@Override
	public String getSub() {
		return "import";
	}

	@Override
	public String getPermission() {
		return "sketchmap.import";
	}

	@Override
	public String getDescription() {
		return "Imports a image via the SketchMap plugin directory.";
	}

	@Override
	public String getSyntax() {
		return "/sketchmap import <map-id> <file-name> [XPANES]:[YPANES]";
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args, String prefix) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + prefix + "This command cannot be used from the Console.");
			return;
		}
		
		Player player = (Player) sender;
		
		if(args.length < 3) {
			player.sendMessage(ChatColor.RED + prefix + "Error in Command Syntax. Try, \"" + getSyntax() + "\"");
			return;
		}
		
		if(args[1].length() < 3 || args[1].length() > 16) {
			player.sendMessage(ChatColor.RED + prefix + "Map ID must be between 3 - 16 Characters");
			return;
		}
		
		if(!StringUtils.isAlphanumeric(args[1].replace("_", "").replace("-", ""))) {
			player.sendMessage(ChatColor.RED + prefix + "Map ID must be Alphanumeric");
			return;
		}
		
		if(SketchMapAPI.getMapByID(args[1]) != null) {
			if(SketchMapAPI.getMapByID(args[1]).isPublicProtected()) {
				player.sendMessage(ChatColor.RED + prefix + "An External Plugin has reserved "
						+ "that Map ID. Try a different Map ID");
				return;
			}
			
			player.sendMessage(ChatColor.RED + prefix + "A map by that id already exists.");
			return;
		}
		
		File file = new File(SketchMapLoader.getDataFolder().toString() + "/" + args[2]);
		if(!file.exists()) {
			player.sendMessage(ChatColor.RED + prefix + "Could not find specified image file. "
					+ "Ensure that you have typed the entire file name (Case sensitive including extension) correctly. "
					+ "This file should be located in the SketchMap plugin directory.");
			return;
		}
		
		
		
		
		Integer xPanes = null;
		Integer yPanes = null;
		
		
		if(args.length > 3) {
			String[] split = args[3].split(":");
			if(split.length != 2) {
				player.sendMessage(ChatColor.RED + prefix + "Cannot resize image invalid resize arguments set. "
						+ getSyntax());
				return;
			}
			
			try {
				xPanes = Integer.parseInt(split[0]);
				yPanes = Integer.parseInt(split[1]);
				
			}
			catch (Exception ex) {
				player.sendMessage(ChatColor.RED + prefix + "Cannot resize image invalid resize arguments set. "
						+ getSyntax());
				return;
			}

			
			if(xPanes < 1 || yPanes < 1) {
				player.sendMessage(ChatColor.RED + prefix + "Resize image arguments must be positive. "
						+ getSyntax());
				return;
			}
			
		}
		
		try {
			player.sendMessage(ChatColor.AQUA + prefix + "Downloading Image");
			BufferedImage image = ImageIO.read(file);

			player.sendMessage(ChatColor.AQUA + prefix + "Processing Image");
			
			String ext = file.getName().substring(file.getName().length() - 3);
			BaseFormat format = BaseFormat.fromExtension(ext);
		
			if(format == null) {
				player.sendMessage(ChatColor.RED + prefix + "Sorry, Only JPEG and PNG are supported at this moment. "
						+ "But animated Maps will be coming soon.");
				return;
			}
			
			if(args.length == 3) {
				
				int imageX = image.getWidth();
				int imageY = image.getHeight();
				
				while(imageX % 128 != 0) {
					imageX++;	
				}

				while(imageY % 128 != 0) {
					imageY++;	
				}

				xPanes = imageX / 128;
				yPanes = imageY / 128;
			}
			
			new SketchMap(image, args[1], xPanes, yPanes, false, format);

			
			player.sendMessage(ChatColor.GREEN + prefix + "Map \"" + ChatColor.GOLD 
					+ args[1] + ChatColor.GREEN + "\" Created! " + ChatColor.GOLD 
					+ "Use \"/sketchmap get " + args[1] + "\"" + " to get this map as map items.");
			
			player.sendMessage(ChatColor.AQUA + " Or use \"" + ChatColor.GOLD + "/sketchmap place " 
					+ args[1] + ChatColor.AQUA + "\" to place it directly onto a wall.");
			
		} catch (IOException e) {
			player.sendMessage(ChatColor.RED + prefix + "Unable to load/find image at URL."
					+ " If you think this is a error try uploading this image @ imgur.com.");
			return;
		}
	}
		
		
}
