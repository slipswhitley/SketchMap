package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SubCommandPrivacy extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "privacy";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.privacy";
    }

    @Override
    public String getDescription()
    {
        return "Sets the privacy level of the specified map";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap privacy <MAP-ID> <public/private>";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        if (args.length != 2)
        {
            sender.sendMessage(ChatColor.RED + prefix + "Error in Command Syntax. Try, \"" + this.getSyntax() + "\"");
            return;
        }
        final SketchMap sketchMap = SketchMapAPI.getMapByID(args[0]);
        if (sketchMap == null)
        {
            sender.sendMessage(ChatColor.RED + prefix + "No map exists with that id.");
            return;
        }
        if (sender instanceof Player)
        {
            final Player player = (Player) sender;
            if (!SketchMapUtils.hasPermission(player, "sketchmap.privacy.admin"))
            {
                UUID playerUUID = player.getUniqueId();
                UUID ownerUUID = sketchMap.getOwnerUUID();
                if (!ownerUUID.equals(playerUUID))
                {
                    player.sendMessage(ChatColor.RED + prefix + "You are not the owner of that map!");
                    return;
                }
            }
        }

        if (args[1].equalsIgnoreCase("public"))
        {
            sketchMap.setPrivacyLevel(SketchMap.PrivacyLevel.PUBLIC);
        }
        else if (args[1].equalsIgnoreCase("private"))
        {
            sketchMap.setPrivacyLevel(SketchMap.PrivacyLevel.PRIVATE);
        }
        else
        {
            sender.sendMessage(ChatColor.RED + prefix + "Invalid privacy level. You must set the privacy level to 'public' or 'private'.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + prefix + "Privacy level for map " + sketchMap.getID() + " set to " + sketchMap.getPrivacyLevel() + ".");
        sketchMap.save();
    }
}
