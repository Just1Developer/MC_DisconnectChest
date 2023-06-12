package net.justonedev.mc.event;

import net.justonedev.mc.UserData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

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
				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§0 ")) return;	// Cancel as usual (non-dropping type)
				
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
				
				/*
				if(e.getCurrentItem() == null) return;
				if(!e.getCurrentItem().hasItemMeta()) return;
				if(!e.getCurrentItem().getItemMeta().hasDisplayName()) return;

				if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§f ") && e.getCursor() == null) return;	// Cancel as usual
				if(e.getCursor() == null) return;
				 */
				Material CursorType = e.getCursor() == null ? Material.AIR : e.getCursor().getType();
				Material SlotType = e.getCurrentItem() == null ? Material.AIR : e.getCurrentItem().getType();


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
				
				// Allow click if conditions are met:
				// 1. The cursor is a valid item
				// 2. The clicked slot contains an item and the cursor is either air or 1. is true
				// 3. If the clicked slot is an item and the cursor is air, insert the filler item to prevent shift-clicks
				
				
				if(validItems.contains(CursorType))
				{
					// Handles putting an item down and/or swapping
					ItemStack slotitem = e.getCurrentItem();
					e.setCurrentItem(e.getCursor());
					
					if(!slotitem.hasItemMeta() || !slotitem.getItemMeta().getDisplayName().startsWith(UserData.SlotFillerPrefix)) e.getWhoClicked().setItemOnCursor(slotitem);
					else e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
				}
				else if(validItems.contains(SlotType) && CursorType == Material.AIR)	// No need to check if cursor is valid because it obv isn't
				{
					// Handles pick up item if cursor is empty and fill up empty slot
					e.getWhoClicked().setItemOnCursor(e.getCurrentItem());
					
					if(r == 1) e.setCurrentItem(UserData.HeadFiller);
					else if(r == 2) e.setCurrentItem(UserData.ChestFiller);
					else if(r == 3) e.setCurrentItem(UserData.LegsFiller);
					else e.setCurrentItem(UserData.FootFiller);
				}
			}
		}
	}
	
	/*
	int angleI = 0;
	EulerAngle[] angles = {
		new EulerAngle(0, 0, 0),
		new EulerAngle(135, 0, 0),
			new EulerAngle(0, 0, 0),
			new EulerAngle(135, 0, 0),
			new EulerAngle(135, 0, 0),
	};
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		if(e.getBlock().getType() != Material.DIAMOND_BLOCK) return;
		e.setCancelled(true);
		ArmorStand stand = (ArmorStand) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.ARMOR_STAND);
		
		/*
		stand.setBodyPose(angles[angleI + 1]);
		stand.setLeftLegPose(angles[angleI]);
		stand.setRightLegPose(angles[angleI]);
		* /
		
		EulerAngle angle = new EulerAngle(135, 0, 0);
		stand.setBodyPose(angle);
		stand.setLeftLegPose(angle);
		stand.setRightLegPose(angle);
		
		stand.setSmall(true);
		stand.getEquipment().setHelmet(new ItemStack(Material.CHEST));
		Bukkit.broadcastMessage("Set Angle (" + angleI + "): " + angles[angleI]);
		angleI++;
		if(angleI >= angles.length - 1) angleI = 0;
	}
	*/
}
