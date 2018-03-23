package io.github.ebaldino.SketchMap.listener;

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
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
	    
	    // If the player clicks on an emtpy fram with SkatchMap map in hand, 
	    // the frame must receive the map's lore (id)
	    
		if(!(event.getRightClicked() instanceof ItemFrame)) {
			return;
		}
		
		ItemFrame iFrame = (ItemFrame) event.getRightClicked();
		ItemStack iHand = event.getPlayer().getInventory().getItemInMainHand();
		
		if(iHand.getType() != Material.MAP) {
			return;
		}
		
		String lore = null; 
		try {
		    lore = iHand.getItemMeta().getLore().get(0);
		} catch (Exception e) {};		
		
		if(!ChatColor.stripColor(lore).startsWith("SketchMap ID:")) {
			return;
		}
		
		if(iFrame.getItem().getType() != Material.AIR) {
			return;
		}
		
		if(event.isCancelled()) {
			return;
		}
		
		event.setCancelled(true);
		
		
		ItemStack frameItem = iHand.clone();
		frameItem.setAmount(1);
		ItemMeta frameIMeta = frameItem.getItemMeta();
		
		frameIMeta.setDisplayName("");
		frameItem.setItemMeta(frameIMeta);
		
		iFrame.setItem(frameItem);
		
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if(iHand.getAmount() == 1) {
			player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			return;
		}
		
		iHand.setAmount(iHand.getAmount() - 1);
		
		
	}
}
