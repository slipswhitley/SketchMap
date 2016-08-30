package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SubCommandUnpermit extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "unpermit";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.privacy";
    }

    @Override
    public String getDescription()
    {
        return "Disallow a player from using /sketchmap place and /sketchmap get with the specified map.";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap unpermit <MAP-ID> <PLAYER-NAME>";
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
            sender.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" + args[0].toLowerCase() + "\"");
            return;
        }
        if (sketchMap.getPrivacyLevel() == SketchMap.PrivacyLevel.PUBLIC)
        {
            sender.sendMessage(ChatColor.RED + prefix + "That map is publicly accessible!");
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

        UUID otherUUID = SketchMapUtils.nameToUUID(args[1]);

        if (!sketchMap.getAllowedUUID().contains(otherUUID))
        {
            sender.sendMessage(ChatColor.RED + prefix + args[1] + " is already not permitted to use sketchmap " + args[0] + ".");
            return;
        }

        sketchMap.removeAllowedUUID(otherUUID);
        sender.sendMessage(ChatColor.DARK_AQUA + prefix + args[1] + " is no longer permitted to use sketchmap " + args[0] + ".");
        sketchMap.save();
    }
}
