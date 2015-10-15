package com.mcplugindev.slipswhitley.sketchmap.map;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.file.FileManager;

public class SketchMap {
	private BufferedImage image;
	private String mapID;
	private Integer xPanes;
	private Integer yPanes;
	private Boolean publicProtected;
	private BaseFormat format;
	private Map<RelativeLocation, MapView> mapCollection;
	private FileManager fileManager;
	private static Set<SketchMap> sketchMaps;

	public SketchMap(final BufferedImage image, final String mapID, final int xPanes, final int yPanes,
			final boolean publicProtected, final BaseFormat format) {
		this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
		this.mapID = mapID;
		this.xPanes = xPanes;
		this.yPanes = yPanes;
		this.publicProtected = publicProtected;
		this.format = format;
		this.mapCollection = new HashMap<RelativeLocation, MapView>();
		this.fileManager = new FileManager(this);
		getLoadedMaps().add(this);
		this.loadSketchMap();
		this.fileManager.save();
	}

	private void loadSketchMap() {
		for (int x = 0; x < this.xPanes; ++x) {
			for (int y = 0; y < this.yPanes; ++y) {
				this.initMap(x, y, Bukkit.createMap(SketchMapUtils.getDefaultWorld()));
			}
		}
	}

	public SketchMap(final BufferedImage image, final String mapID, final int xPanes, final int yPanes,
			final boolean publicProtected, final BaseFormat format, final Map<Short, RelativeLocation> mapCollection) {
		this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
		this.mapID = mapID;
		this.xPanes = xPanes;
		this.yPanes = yPanes;
		this.publicProtected = publicProtected;
		this.format = format;
		this.mapCollection = new HashMap<RelativeLocation, MapView>();
		this.fileManager = new FileManager(this);
		getLoadedMaps().add(this);
		this.loadSketchMap(mapCollection);
		this.fileManager.save();
	}

	private void loadSketchMap(final Map<Short, RelativeLocation> mapCollection) {
		for (final Short mapID : mapCollection.keySet()) {
			final RelativeLocation loc = mapCollection.get(mapID);
			this.initMap(loc.getX(), loc.getY(), SketchMapUtils.getMapView(mapID));
		}
	}

	private void initMap(final int x, final int y, final MapView mapView) {
		final BufferedImage subImage = this.image.getSubimage(x * 128, y * 128, 128, 128);
		for (final MapRenderer rend : mapView.getRenderers()) {
			mapView.removeRenderer(rend);
		}
		mapView.addRenderer((MapRenderer) new ImageRenderer(subImage));
		this.mapCollection.put(new RelativeLocation(x, y), mapView);
	}

	public String getID() {
		return this.mapID;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public int getLengthX() {
		return this.xPanes;
	}

	public int getLengthY() {
		return this.yPanes;
	}

	public boolean isPublicProtected() {
		return this.publicProtected;
	}

	public Map<RelativeLocation, MapView> getMapCollection() {
		return this.mapCollection;
	}

	public BaseFormat getBaseFormat() {
		return this.format;
	}

	public void delete() {
		this.fileManager.deleteFile();
		getLoadedMaps().remove(this);
		try {
			this.finalize();
		} catch (Throwable t) {
		}
	}

	public void save() {
		this.fileManager.save();
	}

	public static Set<SketchMap> getLoadedMaps() {
		if (SketchMap.sketchMaps == null) {
			SketchMap.sketchMaps = new HashSet<SketchMap>();
		}
		return SketchMap.sketchMaps;
	}

	public enum BaseFormat {
		PNG("PNG", 0), JPEG("JPEG", 1);

		private BaseFormat(final String s, final int n) {
		}

		public String getExtension() {
			if (this == BaseFormat.PNG) {
				return "png";
			}
			if (this == BaseFormat.JPEG) {
				return "jpg";
			}
			return null;
		}

		public static BaseFormat fromExtension(final String ext) {
			if (ext.equalsIgnoreCase("png")) {
				return BaseFormat.PNG;
			}
			if (ext.equalsIgnoreCase("jpg")) {
				return BaseFormat.JPEG;
			}
			return null;
		}
	}
}
