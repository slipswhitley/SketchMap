package io.github.ebaldino.SketchMap.map;

import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageRenderer extends MapRenderer {

	private BufferedImage image;
	private Boolean imageRendered;
	
	
	public ImageRenderer(BufferedImage image) {
		this.image = image;
		this.imageRendered = false;
	}
	
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(imageRendered) {
			return;
		}
		
		canvas.drawImage(0, 0, image);
		this.imageRendered = true;
	}
	
	

}
