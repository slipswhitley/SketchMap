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
        }
        catch (Exception ex)
        {
            return null;
        }
        return new RelativeLocation(x, y);
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
