package com.mcplugindev.slipswhitley.sketchmap.map;

public class RelativeLocation
{
    private final int x;
    private final int y;

    public RelativeLocation(final int x, final int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.x) + ":" + this.y;
    }

    public static RelativeLocation fromString(final String str)
    {
        return fromStringZeroBase(str);
    }

    public static RelativeLocation fromStringZeroBase(final String str)
    {
        final String[] args = str.split(":");
        if (args.length != 2)
        {
            return null;
        }
        int x;
        int y;
        try
        {
            x = Integer.parseInt(args[0]);
            y = Integer.parseInt(args[1]);
            if (x < 0 || y < 0)
                throw new Exception("negative coordinates are disallowed");
        }
        catch (Exception ex)
        {
            return null;
        }
        return new RelativeLocation(x, y);
    }

    public static RelativeLocation fromStringOneBase(final String str)
    {
        final String[] args = str.split(":");
        if (args.length != 2)
        {
            return null;
        }
        int x;
        int y;
        try
        {
            x = Integer.parseInt(args[0]) - 1;
            y = Integer.parseInt(args[1]) - 1;
            if (x < 0 || y < 0)
                throw new Exception("negative coordinates are disallowed");
        }
        catch (Exception ex)
        {
            return null;
        }
        return new RelativeLocation(x, y);
    }

    /**
     * Compares two RelativeLocation objects to see if they point to the same relative coordinates. Does NOT check
     * whether the coordinates are on the same sketchmap.
     *
     * @param otherLoc RelativeLocation object to be compared
     * @return true if the relative X and Y coordinates are the same for both objects, false otherwise
     */
    public boolean equals(RelativeLocation otherLoc)
    {
        return this.x == otherLoc.x && this.y == otherLoc.y;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }
}
