package com.mcplugindev.slipswhitley.sketchmap.command;

import com.mcplugindev.slipswhitley.sketchmap.command.sub.*;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class SketchMapSubCommand
{
    private static List<SketchMapSubCommand> commands;

    public abstract String getSub();

    public abstract String getPermission();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void onCommand(final CommandSender p0, final String[] p1, final String p2);

    public static void loadCommands()
    {
        SketchMapSubCommand.commands = new ArrayList<>();
        loadCommand(new SubCommandCreate());
        loadCommand(new SubCommandDelete());
        loadCommand(new SubCommandGet());
        loadCommand(new SubCommandHelp());
        loadCommand(new SubCommandImport());
        loadCommand(new SubCommandList());
        loadCommand(new SubCommandPlace());
        loadCommand(new SubCommandPrivacy());
        loadCommand(new SubCommandPermit());
        loadCommand(new SubCommandUnpermit());
        loadCommand(new SubCommandInfo());
        loadCommand(new SubCommandSetOwner());
        loadCommand(new SubCommandReload());
        loadCommand(new SubCommandGetMap());
    }

    private static void loadCommand(final SketchMapSubCommand sub)
    {
        SketchMapSubCommand.commands.add(sub);
    }

    public static List<SketchMapSubCommand> getCommands()
    {
        return SketchMapSubCommand.commands;
    }
}
