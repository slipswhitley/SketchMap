package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapPlugin;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SubCommandReload extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "reload";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.reload";
    }

    @Override
    public String getDescription()
    {
        return "Reloads the plugin";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap reload";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        sender.sendMessage(ChatColor.DARK_PURPLE + prefix + "Reloading...");
        SketchMapPlugin.getPlugin().reload();
        sender.sendMessage(ChatColor.DARK_PURPLE + prefix + "Reload complete!");
    }
}
