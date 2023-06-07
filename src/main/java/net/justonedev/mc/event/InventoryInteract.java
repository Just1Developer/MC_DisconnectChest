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

import java.util.ArrayList;
import java.util.List;

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
			// Disallow everything except for specific armor for armor slots
			else if(r >= 1 && r <= 4)
			{
				//Armor
				e.setCancelled(true);
				// Previous block before the chest - item
				if(e.getCurrentItem() == null) return;
				if(!e.getCurrentItem().hasItemMeta()) return;
				if(!e.getCurrentItem().getItemMeta().hasDisplayName()) return;

				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§f ") && e.getCursor() == null) return;	// Cancel as usual
				if(e.getCursor() == null) return;

				ArrayList<Material> validItems = new ArrayList<>();

				// Set items
				{
					if (r == 1) {
						validItems.add(Material.LEATHER_HELMET);
						validItems.add(Material.CHAINMAIL_HELMET);
						validItems.add(Material.IRON_HELMET);
						validItems.add(Material.GOLDEN_HELMET);
						validItems.add(Material.DIAMOND_HELMET);
						validItems.add(Material.NETHERITE_HELMET);
						validItems.add(Material.PUMPKIN);
					} else if (r == 2) {
						validItems.add(Material.LEATHER_CHESTPLATE);
						validItems.add(Material.CHAINMAIL_CHESTPLATE);
						validItems.add(Material.IRON_CHESTPLATE);
						validItems.add(Material.GOLDEN_CHESTPLATE);
						validItems.add(Material.DIAMOND_CHESTPLATE);
						validItems.add(Material.NETHERITE_CHESTPLATE);
						validItems.add(Material.ELYTRA);
					} else if (r == 3) {
						validItems.add(Material.LEATHER_LEGGINGS);
						validItems.add(Material.CHAINMAIL_LEGGINGS);
						validItems.add(Material.IRON_LEGGINGS);
						validItems.add(Material.GOLDEN_LEGGINGS);
						validItems.add(Material.DIAMOND_LEGGINGS);
						validItems.add(Material.NETHERITE_LEGGINGS);
					} else {
						validItems.add(Material.LEATHER_BOOTS);
						validItems.add(Material.CHAINMAIL_BOOTS);
						validItems.add(Material.IRON_BOOTS);
						validItems.add(Material.GOLDEN_BOOTS);
						validItems.add(Material.DIAMOND_BOOTS);
						validItems.add(Material.NETHERITE_BOOTS);
					}
				}

				if(validItems.contains(e.getCursor().getType()))
				{
					ItemStack cursor = e.getCursor();
					if(!e.getCurrentItem().getItemMeta().getDisplayName().startsWith(UserData.SlotFillerPrefix)) e.getWhoClicked().setItemOnCursor(e.getCurrentItem());
					e.setCurrentItem(cursor);
				}
				else if(!e.getCurrentItem().getItemMeta().getDisplayName().startsWith(UserData.SlotFillerPrefix))
				{
					ItemStack cursor = e.getCursor();
					if(e.getCursor().getType() == Material.AIR) e.getWhoClicked().setItemOnCursor(e.getCurrentItem());
					else return;
					// Set back default item
					switch (r)
					{
						case 1:
							e.setCurrentItem(UserData.HeadFiller);
							break;
						case 2:
							e.setCurrentItem(UserData.ChestFiller);
							break;
						case 3:
							e.setCurrentItem(UserData.LegsFiller);
							break;
						case 4:
							e.setCurrentItem(UserData.FootFiller);
							break;
					}
				}
			}
		}
	}
}
