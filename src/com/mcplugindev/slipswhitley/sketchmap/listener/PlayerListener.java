package com.mcplugindev.slipswhitley.sketchmap.listener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame)) {
			return;
		}
		final ItemFrame iFrame = (ItemFrame) event.getRightClicked();
		final ItemStack iHand = event.getPlayer().getItemInHand();
		if (iHand.getType() != Material.MAP || iHand.getItemMeta() == null || iHand.getItemMeta().getLore() == null
				|| iHand.getItemMeta().getLore().isEmpty()) {
			return;
		}
		final String lore = iHand.getItemMeta().getLore().get(0);
		if (!ChatColor.stripColor(lore).startsWith("SketchMap ID:")) {
			return;
		}
		if (iFrame.getItem().getType() != Material.AIR) {
			return;
		}
		if (event.isCancelled()) {
			return;
		}
		event.setCancelled(true);
		final ItemStack frameItem = iHand.clone();
		frameItem.setAmount(1);
		final ItemMeta frameIMeta = frameItem.getItemMeta();
		frameIMeta.setDisplayName("");
		frameItem.setItemMeta(frameIMeta);
		iFrame.setItem(frameItem);
		final Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if (iHand.getAmount() == 1) {
			player.getInventory().setItemInHand(new ItemStack(Material.AIR));
			return;
		}
		iHand.setAmount(iHand.getAmount() - 1);
	}
}
