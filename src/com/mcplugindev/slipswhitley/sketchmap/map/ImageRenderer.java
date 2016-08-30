package com.mcplugindev.slipswhitley.sketchmap.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class ImageRenderer extends MapRenderer
{
    private final BufferedImage image;
    private Boolean imageRendered;

    public ImageRenderer(final BufferedImage image)
    {
        this.image = image;
        this.imageRendered = false;
    }

    public void render(final MapView view, final MapCanvas canvas, final Player player)
    {
        if (this.imageRendered)
        {
            return;
        }
        canvas.drawImage(0, 0, this.image);
        this.imageRendered = true;
    }
}
