package net.justonedev.mc.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryInteract implements Listener {

	@EventHandler
	public void InventorySync(InventoryClickEvent e)
	{
		// we can also store the invs in a hashmap and then use getViewers
		
		if(e.getClickedInventory() != null && e.getView().getTitle().startsWith("§8PlayerChest - "))
		{
			//
			if(e.getClickedInventory().getSize() == 54 && e.getRawSlot() < 27 && e.getRawSlot() >= 18) e.setCancelled(true);
			int r = e.getRawSlot();
			// Denied Slots
			if(r == 0 || r == 5 || r == 7 || r == 8) e.setCancelled(true);
		}
	}
	
	/*
	public void syncInvs(String invTitle, Inventory newInventory)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			//if(p.getOpenInventory().) continue;
			
		}
	}
	 */

}
