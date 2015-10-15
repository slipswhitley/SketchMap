package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.file.SketchMapLoader;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	public void onCommand(final CommandSender sender, final String[] args, final String prefix) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + prefix + "This command cannot be used from the Console.");
			return;
		}
		final Player player = (Player) sender;
		if (args.length < 3) {
			player.sendMessage(ChatColor.RED + prefix + "Error in Command Syntax. Try, \"" + this.getSyntax() + "\"");
			return;
		}
		if (args[1].length() < 3 || args[1].length() > 16) {
			player.sendMessage(ChatColor.RED + prefix + "Map ID must be between 3 - 16 Characters");
			return;
		}
		if (!StringUtils.isAlphanumeric(args[1].replace("_", "").replace("-", ""))) {
			player.sendMessage(ChatColor.RED + prefix + "Map ID must be Alphanumeric");
			return;
		}
		if (SketchMapAPI.getMapByID(args[1]) != null) {
			if (SketchMapAPI.getMapByID(args[1]).isPublicProtected()) {
				player.sendMessage(ChatColor.RED + prefix + "An External Plugin has reserved "
						+ "that Map ID. Try a different Map ID");
				return;
			}
			player.sendMessage(ChatColor.RED + prefix + "A map by that id already exists.");
		} else {
			final File file = new File(String.valueOf(SketchMapLoader.getDataFolder().toString()) + "/" + args[2]);
			if (!file.exists()) {
				player.sendMessage(ChatColor.RED + prefix + "Could not find specified image file. "
						+ "Insure that you have typed the entire file name (Case sensitive including extension) correctly. "
						+ "This file should be located in the SketchMap plugin directory.");
				return;
			}
			Integer xFrames = null;
			Integer yFrames = null;
			if (args.length > 3) {
				final String[] split = args[3].split(":");
				if (split.length != 2) {
					player.sendMessage(ChatColor.RED + prefix + "Cannot resize image invalid resize arguments set. "
							+ this.getSyntax());
					return;
				}
				try {
					xFrames = Integer.parseInt(split[0]);
					yFrames = Integer.parseInt(split[1]);
				} catch (Exception ex) {
					player.sendMessage(ChatColor.RED + prefix + "Cannot resize image invalid resize arguments set. "
							+ this.getSyntax());
					return;
				}
				if (xFrames < 1 || yFrames < 1) {
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
			}
			try {
				player.sendMessage(ChatColor.AQUA + prefix + "Downloading Image");
				final BufferedImage image = ImageIO.read(file);
				player.sendMessage(ChatColor.AQUA + prefix + "Processing Image");
				final String ext = file.getName().substring(file.getName().length() - 3);
				final SketchMap.BaseFormat format = SketchMap.BaseFormat.fromExtension(ext);
				if (format == null) {
					player.sendMessage(
							ChatColor.RED + prefix + "Sorry, Only JPEG and PNG are supported at this moment. "
									+ "But animated Maps will be coming soon.");
					return;
				}
				if (args.length == 3) {
					int imageX = image.getWidth();
					int imageY = image.getHeight();
					while (imageX % 128 != 0) {
						++imageX;
					}
					while (imageY % 128 != 0) {
						++imageY;
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
				new SketchMap(image, args[1], xFrames, yFrames, false, format);
				player.sendMessage(ChatColor.GREEN + prefix + "Map \"" + ChatColor.GOLD + args[1] + ChatColor.GREEN
						+ "\" Created! " + ChatColor.GOLD + "Use \"/sketchmap get " + args[1] + "\""
						+ " to get this map as map items.");
				player.sendMessage(ChatColor.AQUA + " Or use \"" + ChatColor.GOLD + "/sketchmap place " + args[1]
						+ ChatColor.AQUA + "\" to place it directly onto a wall.");
			} catch (IOException e) {
				player.sendMessage(ChatColor.RED + prefix + "Unable to load/find image at URL."
						+ " If you think this is a error try uploading this image @ imgur.com.");
			}
		}
	}
}
