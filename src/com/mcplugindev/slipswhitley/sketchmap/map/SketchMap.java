package com.mcplugindev.slipswhitley.sketchmap.map;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.file.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.*;

public class SketchMap
{
    private final BufferedImage image;
    private final String mapID;
    private UUID ownerUUID;
    private PrivacyLevel privacyLevel;
    private List<UUID> allowedUUID;
    private final Integer xPanes;
    private final Integer yPanes;
    private Boolean publicProtected;
    private final BaseFormat format;
    private final Map<RelativeLocation, MapView> mapCollection;
    private final FileManager fileManager;
    private static Set<SketchMap> sketchMaps;

    public SketchMap(final BufferedImage image, final String mapID, final UUID ownerUUID, final PrivacyLevel privacyLevel, final int xPanes, final int yPanes,
                     final boolean publicProtected, final BaseFormat format)
    {
        this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
        this.mapID = mapID;
        this.ownerUUID = ownerUUID;
        this.privacyLevel = privacyLevel;
        this.allowedUUID = new ArrayList<>();
        this.xPanes = xPanes;
        this.yPanes = yPanes;
        this.publicProtected = publicProtected;
        this.format = format;
        this.mapCollection = new HashMap<>();
        this.fileManager = new FileManager(this);
        getLoadedMaps().add(this);
        this.loadSketchMap();
        save();
    }

    private void loadSketchMap()
    {
        for (int x = 0; x < this.xPanes; ++x)
        {
            for (int y = 0; y < this.yPanes; ++y)
            {
                this.initMap(x, y, Bukkit.createMap(SketchMapUtils.getDefaultWorld()));
            }
        }
    }

    public SketchMap(final BufferedImage image, final String mapID, final UUID ownerUUID, final PrivacyLevel privacyLevel, final List<UUID> allowedUUID, final int xPanes, final int yPanes,
                     final boolean publicProtected, final BaseFormat format, final Map<Short, RelativeLocation> mapCollection)
    {
        this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
        this.mapID = mapID;
        this.ownerUUID = ownerUUID;
        this.privacyLevel = privacyLevel;
        this.allowedUUID = allowedUUID;
        this.xPanes = xPanes;
        this.yPanes = yPanes;
        this.publicProtected = publicProtected;
        this.format = format;
        this.mapCollection = new HashMap<>();
        this.fileManager = new FileManager(this);
        getLoadedMaps().add(this);
        this.loadSketchMap(mapCollection);
        save();
    }

    private void loadSketchMap(final Map<Short, RelativeLocation> mapCollection)
    {
        for (final Short mapID : mapCollection.keySet())
        {
            final RelativeLocation loc = mapCollection.get(mapID);
            this.initMap(loc.getX(), loc.getY(), SketchMapUtils.getMapView(mapID));
        }
    }

    private void initMap(final int x, final int y, final MapView mapView)
    {
        final BufferedImage subImage = this.image.getSubimage(x * 128, y * 128, 128, 128);
        mapView.getRenderers().stream().forEach(mapRenderer -> mapView.removeRenderer(mapRenderer));
        mapView.addRenderer(new ImageRenderer(subImage));
        this.mapCollection.put(new RelativeLocation(x, y), mapView);
    }

    public String getID()
    {
        return this.mapID;
    }

    public UUID getOwnerUUID()
    {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID newOwnerUUID)
    {
        this.ownerUUID = newOwnerUUID;
    }

    public PrivacyLevel getPrivacyLevel()
    {
        return this.privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel)
    {
        this.privacyLevel = privacyLevel;
    }

    public List<UUID> getAllowedUUID()
    {
        return this.allowedUUID;
    }

    /**
     * You are responsible for checking whether the UUID is already in the list before adding it!
     */
    public void addAllowedUUID(UUID toAdd)
    {
        this.allowedUUID.add(toAdd);
    }

    /**
     * You are responsible for checking whether the UUID is actually in the list before removing it!
     */
    public void removeAllowedUUID(UUID toRemove)
    {
        this.allowedUUID.remove(toRemove);
    }

    public BufferedImage getImage()
    {
        return this.image;
    }

    public int getLengthX()
    {
        return this.xPanes;
    }

    public int getLengthY()
    {
        return this.yPanes;
    }

    public boolean isPublicProtected()
    {
        return this.publicProtected;
    }

    public Map<RelativeLocation, MapView> getMapCollection()
    {
        return this.mapCollection;
    }

    public BaseFormat getBaseFormat()
    {
        return this.format;
    }

    public void delete()
    {
        this.fileManager.deleteFile();
        getLoadedMaps().remove(this);
    }

    public void save()
    {
        this.fileManager.save();
    }

    public static Set<SketchMap> getLoadedMaps()
    {
        if (SketchMap.sketchMaps == null)
        {
            SketchMap.sketchMaps = new HashSet<>();
        }
        return SketchMap.sketchMaps;
    }

    public static void disable()
    {
        SketchMap.sketchMaps = null;
    }

    public enum BaseFormat
    {
        PNG, JPEG;

        public String getExtension()
        {
            if (this == BaseFormat.PNG)
                return "png";
            if (this == BaseFormat.JPEG)
                return "jpg";
            return null;
        }

        public static BaseFormat fromExtension(final String ext)
        {
            if (ext.equalsIgnoreCase("png"))
                return BaseFormat.PNG;
            if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("peg"))
                return BaseFormat.JPEG;
            return null;
        }
    }

    public enum PrivacyLevel
    {
        PUBLIC, PRIVATE
    }
}
