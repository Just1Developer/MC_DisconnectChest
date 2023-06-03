package net.justonedev.mc.event;

import net.justonedev.mc.UserData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChestInteract implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Location loc = e.getBlock().getLocation();
		Location l = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(!UserData.AllUUIDsByChestLocations.containsKey(l)) return;
		if(e.getBlock().getType() != Material.CHEST) return;
		
		e.setDropItems(false);
		World w = e.getBlock().getWorld();
		for(ItemStack itemStack : UserData.InventoryData.get(UserData.AllUUIDsByChestLocations.get(l)).getContents())
		{
			if(itemStack == null || itemStack.getType() == Material.AIR || itemStack.hasItemMeta()) continue;
			if(itemStack.getItemMeta().getDisplayName().equals("§7Previous Item") && itemStack.getItemMeta().hasLore() && itemStack.getItemMeta().getLore().size() == 2)
				w.dropItemNaturally(l, new ItemStack(itemStack.getType(), 1));
			else w.dropItemNaturally(l, itemStack);
		}
		UserData.RemovePlayerChest(l);
		//if(UserData.PreviousBlockTypes.containsKey(l))
		//{
		//	l.getBlock().setType(UserData.PreviousBlockTypes.get(l));
		//	UserData.PreviousBlockTypes.remove(l);
		//}
		
		/* Nope, if we do the following, these chests act as bedrock
		e.setCancelled(true);
		e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_WOOD_BREAK, 5f, 1f);
		 */
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Location loc = e.getBlock().getLocation();
		Location l = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(!UserData.AllUUIDsByChestLocations.containsKey(l)) return;
		if(e.getBlock().getType() != Material.CHEST) return;
		
		// If someone places the chest there again, the chest (because it also does not drop) should not be taken away from the inventory
		e.setCancelled(true);
		e.getBlock().setType(Material.CHEST);
	}
	
	// Big question is if the main
	
	@EventHandler
	public void OnInventoryOpen(PlayerInteractEvent e)
	{
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.getClickedBlock() == null) return;
		if(e.getClickedBlock().getType() != Material.CHEST) return;
		if(e.getPlayer().isSneaking()) return;
		Location loc = e.getClickedBlock().getLocation();
		Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(!UserData.AllUUIDsByChestLocations.containsKey(blockLoc)) return;
		
		e.setCancelled(true);
		e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_CHEST_OPEN, 5f, 1f);
		e.getPlayer().openInventory(UserData.InventoryData.get(UserData.AllUUIDsByChestLocations.get(e.getClickedBlock().getLocation())));
	}
}
