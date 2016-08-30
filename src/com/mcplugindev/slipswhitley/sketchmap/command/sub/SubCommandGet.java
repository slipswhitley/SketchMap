package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SubCommandGet extends SketchMapSubCommand
{
    @Override
    public String getSub()
    {
        return "get";
    }

    @Override
    public String getPermission()
    {
        return "sketchmap.get";
    }

    @Override
    public String getDescription()
    {
        return "Get a SketchMap as Map Items";
    }

    @Override
    public String getSyntax()
    {
        return "/sketchmap get <map-id>";
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args, final String prefix)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + prefix + "This command cannot be used " + "from the console.");
            return;
        }
        if (args.length != 1)
        {
            sender.sendMessage(
                    ChatColor.RED + prefix + "Invalid command Arguments. " + "Try, \"" + this.getSyntax() + "\"");
            return;
        }
        final SketchMap map = SketchMapAPI.getMapByID(args[0]);
        if (map == null)
        {
            sender.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" + args[0].toLowerCase() + "\"");
            return;
        }
        if (map.isPublicProtected())
        {
            sender.sendMessage(ChatColor.RED + prefix + "An External Plugin has requested that"
                    + " this map is protected from public access.");
            return;
        }
        final Player player = (Player) sender;
        if (map.getPrivacyLevel() == SketchMap.PrivacyLevel.PRIVATE &&
                !SketchMapUtils.hasPermission(player, "sketchmap.privacy.admin") &&
                !map.getOwnerUUID().equals(player.getUniqueId()) &&
                !map.getAllowedUUID().contains(player.getUniqueId()))
        {
            player.sendMessage(ChatColor.RED + prefix + "You don't have permission to get map " + map.getID() + ".");
            return;
        }

        final List<ItemStack> items = SketchMapAPI.getOrderedItemSet(map);
        int inventorySize = items.size() + 1;
        if (inventorySize % 9 != 0)
            inventorySize += 9 - (inventorySize % 9);
        final Inventory inv = Bukkit.createInventory(null, inventorySize,
                ChatColor.DARK_GREEN + "SketchMap ID: " + ChatColor.DARK_GRAY + map.getID());
        items.stream().forEach(itemStack -> inv.addItem(itemStack));
        player.openInventory(inv);
        player.sendMessage(ChatColor.GREEN + prefix + "SketchMap ItemSet Generated \"" + ChatColor.GOLD + map.getID()
                + ChatColor.GREEN + "\"");
    }
}
