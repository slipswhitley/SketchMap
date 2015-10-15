package com.mcplugindev.slipswhitley.sketchmap;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SketchMapUtils {
	private static Permission permission;

	public static BufferedImage resize(Image img, final Integer width, final Integer height) {
		img = img.getScaledInstance(width, height, 4);
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
		final Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	public static BufferedImage base64StringToImg(final String imageString) {
		BufferedImage image = null;
		try {
			final BASE64Decoder decoder = new BASE64Decoder();
			final byte[] imageByte = decoder.decodeBuffer(imageString);
			final ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static String imgToBase64String(final BufferedImage image, final String type) {
		String imageString = null;
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, type, bos);
			final byte[] imageBytes = bos.toByteArray();
			final BASE64Encoder encoder = new BASE64Encoder();
			imageString = encoder.encode(imageBytes);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}

	public static void sendColoredConsoleMessage(final String msg) {
		final ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage(msg);
	}

	protected static boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>) Bukkit
				.getServicesManager().getRegistration((Class) Permission.class);
		if (permissionProvider != null) {
			SketchMapUtils.permission = permissionProvider.getProvider();
		}
		return SketchMapUtils.permission != null;
	}

	public static boolean hasPermission(final Player player, final String permission) {
		if (permission == null) {
			return player.isOp();
		}
		return SketchMapUtils.permission.playerHas(player, permission);
	}

	/** Returns 0 if the map size is lower than the player's limit
	 Returns a positive number if the map is larger than the player's limit
	 This number will be the player's limit (config or perms as applicable)
	 */
	public static int checkSizeLimits(Player player, int x, int y)
	{
        if (player.isOp())
            return 0;

		int largerDimension = Math.max(x,y);
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
			int maxDim = SketchMapPlugin.getMaxDimension();
			if (largerDimension > maxDim)
			{
				return maxDim;
			}
		}

		return 0;
	}

	public static short getMapID(final MapView map) {
		return map.getId();
	}

	public static MapView getMapView(final short id) {
		final MapView map = Bukkit.getMap(id);
		if (map != null) {
			return map;
		}
		return Bukkit.createMap(getDefaultWorld());
	}

	public static Block getTargetBlock(final Player player, final int i) {
		return player.getTargetBlock((Set) null, i);
	}

	public static World getDefaultWorld() {
		return Bukkit.getWorlds().get(0);
	}
}
