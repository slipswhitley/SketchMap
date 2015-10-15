package com.mcplugindev.slipswhitley.sketchmap.command.sub;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapAPI;
import com.mcplugindev.slipswhitley.sketchmap.command.SketchMapSubCommand;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;

public class SubCommandGet extends SketchMapSubCommand {
	@Override
	public String getSub() {
		return "get";
	}

	@Override
	public String getPermission() {
		return "sketchmap.get";
	}

	@Override
	public String getDescription() {
		return "Get a SketchMap as Map Items";
	}

	@Override
	public String getSyntax() {
		return "/sketchmap get <map-id>";
	}

	@Override
	public void onCommand(final CommandSender sender, final String[] args, final String prefix) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + prefix + "This command cannot be used " + "from the console.");
			return;
		}
		if (args.length != 2) {
			sender.sendMessage(
					ChatColor.RED + prefix + "Invalid command Arguments. " + "Try, \"" + this.getSyntax() + "\"");
			return;
		}
		final SketchMap map = SketchMapAPI.getMapByID(args[1]);
		if (map == null) {
			sender.sendMessage(ChatColor.RED + prefix + "Could not find Map \"" + args[1].toLowerCase() + "\"");
			return;
		}
		if (map.isPublicProtected()) {
			sender.sendMessage(ChatColor.RED + prefix + "An External Plugin has requested that"
					+ " this map is protected from public access.");
			return;
		}
		final Player player = (Player) sender;
		final List<ItemStack> items = SketchMapAPI.getOrderedItemSet(map);
		int inventorySize;
		for (inventorySize = items.size() + 1; inventorySize % 9 != 0; ++inventorySize) {
		}
		final Inventory inv = Bukkit.createInventory((InventoryHolder) null, inventorySize,
				ChatColor.DARK_GREEN + "SketchMap ID: " + ChatColor.DARK_GRAY + map.getID());
		for (final ItemStack iStack : items) {
			inv.addItem(new ItemStack[] { iStack });
		}
		player.openInventory(inv);
		player.sendMessage(ChatColor.GREEN + prefix + "SketchMap ItemSet Generated \"" + ChatColor.GOLD + map.getID()
				+ ChatColor.GREEN + "\"");
	}
}
