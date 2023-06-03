package net.justonedev.mc.event;

import net.justonedev.mc.UserData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryInteract implements Listener {

	@EventHandler
	public void InventorySync(InventoryClickEvent e)
	{
		if(e.getClickedInventory() != null && e.getView().getTitle().startsWith("§8PlayerChest - "))
		{
			//
			if(e.getClickedInventory().getSize() == 54 && e.getRawSlot() < 27 && e.getRawSlot() >= 18) e.setCancelled(true);
			int r = e.getRawSlot();
			// Denied Slots
			if(r == 0 || r == 5 || r == 7) e.setCancelled(true);
			else if(r == 8)
			{
				e.setCancelled(true);
				// Previous block before the chest - item
				if(e.getCurrentItem() == null) return;
				if(!e.getCurrentItem().hasItemMeta()) return;
				if(!e.getCurrentItem().getItemMeta().hasDisplayName()) return;
				
				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§f ")) return;	// Cancel as usual
				
				// Item in the slot is an item that was previously there
				// If holding something, deny it
				if(e.getCursor() == null) return;	// Bug, this should not happen
				if(e.getCursor().getType() != Material.AIR) return;
				
				e.getWhoClicked().setItemOnCursor(new ItemStack(e.getCurrentItem().getType()));
				e.getClickedInventory().setItem(8, UserData.EmptyStack);
			}
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
