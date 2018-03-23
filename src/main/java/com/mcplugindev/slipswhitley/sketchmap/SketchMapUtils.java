package com.mcplugindev.slipswhitley.sketchmap;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SketchMapUtils {

	/**
	 * 
	 * Image Utils
	 * 
	 */
	
	
	public static BufferedImage resize(Image img, Integer width, Integer height) {
		
		img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }

	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    return bimage;
	}
	
	
	public static String imgToBase64String(final RenderedImage img, final String formatName) {
	    final ByteArrayOutputStream os = new ByteArrayOutputStream();
	    try {
	        ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
	        return os.toString(StandardCharsets.ISO_8859_1.name());
	    } catch (final IOException ioe) {
	        throw new UncheckedIOException(ioe);
	    }
	}

	public static BufferedImage base64StringToImg(final String base64String) {
	    try {
	        return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
	    } catch (final IOException ioe) {
	        throw new UncheckedIOException(ioe);
	    }
	}
	
	
	/**
	 * 
	 * Permissions / Vault
	 * 
	 */
	
	
	private static Permission permission;
	
	public static void sendColoredConsoleMessage(String msg) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage(msg);
	}
	
	protected static boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager()
        		.getRegistration(net.milkbowl.vault.permission.Permission.class);
        
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        
        return (permission != null);
    }
	
	
	public static boolean hasPermission(Player player, String permission) {
		if(permission == null) {
			return player.isOp();
		}
		
		return SketchMapUtils.permission.playerHas(player, permission);
	}

	

	/**
	 * Deprecated Methods Here :'c
	 */
	
	@SuppressWarnings("deprecation")
	public static short getMapID(MapView map) {
		return map.getId();
	}
	
	@SuppressWarnings("deprecation")
	public static MapView getMapView(short id) {
		MapView map = Bukkit.getMap(id);
		if(map != null) {
			return map;
		}
		
		return Bukkit.createMap(getDefaultWorld());
	}
	
	@SuppressWarnings("deprecation")
	public static Block getTargetBlock(Player player, int i) {
		return player.getTargetBlock(null, i);
	}
	
	
	/**
	 * 
	 */
	

	public static World getDefaultWorld() {
		return Bukkit.getWorlds().get(0);
	}


	
	
}
