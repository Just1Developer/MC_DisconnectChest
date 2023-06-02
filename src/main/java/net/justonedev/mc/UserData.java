package net.justonedev.mc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class UserData {
	
	static String s_folder = DisconnectChest.Instance.getDataFolder() + "/Userdata/";
	static File folder = new File(s_folder);
	
	// These chests only drop the content in the file, not even a chest themselves. Because that would be like. infinite chests.
	public static final HashMap<Location, String> AllUUIDsByChestLocations = new HashMap<>();
	public static final HashMap<Location, Material> PreviousBlockTypes = new HashMap<>();
	
	private static Location ChestLocationByUUID(String uuid)
	{
		for(Location key : AllUUIDsByChestLocations.keySet())
		{
			if(AllUUIDsByChestLocations.get(key).equals(uuid)) return key;
		}
		return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	}
	
	public static final HashMap<String, Inventory> InventoryData = new HashMap<>();
	private static void Init()
	{
		if(folder.exists()) return;
		folder.mkdirs();
	}
	
	final static int invSize = 45;	// Change to 54 if you want an empty row in between hotbar & rest of the inventory
	
	public static void RemovePlayerChest(Location location)
	{
		if(!AllUUIDsByChestLocations.containsKey(location)) return;
		InventoryData.get(AllUUIDsByChestLocations.get(location)).clear();
		/* Can't do this anymore because of how we're handling inventory wipe on join now
		if(!AllUUIDsByChestLocations.containsKey(location)) return;
		InventoryData.remove(AllUUIDsByChestLocations.get(location));
		AllUUIDsByChestLocations.remove(location);
		 */
	}
	
	public static Inventory GetInventory(String uuid)
	{
		if(InventoryData.containsKey(uuid)) return InventoryData.get(uuid);
		return Bukkit.createInventory(null, invSize, "§8PlayerChest - " + uuid);
	}
	
	public static void InvokePlayerQuit(Player Player)
	{
		Location loc = Player.getLocation();
		if(loc.getBlock().getType() != Material.AIR)
		{
			Location l = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			PreviousBlockTypes.put(l, l.getBlock().getType());
		}
		
		// Spawn chest
		loc.getBlock().setType(Material.CHEST);
		PlayerInventory p_inv = Player.getInventory();
		EnterInventory(Player.getUniqueId().toString(), loc, p_inv);
	}
	
	public static void InvokePlayerJoined(Player Player)
	{
		// Server crashed or sumn
		if(!UserData.InventoryData.containsKey(Player.getUniqueId().toString())) return;
		// If the server did not crash, there should be a saved inventory. Only then wipe the old one and load it.
		Player.getInventory().clear();
		// Despawn chest
		Inventory inv = UserData.GetInventory(Player.getUniqueId().toString());
		while(inv.getViewers().size() > 0)
		{
			inv.getViewers().get(0).closeInventory();
		}
		
		Player.getInventory().setHelmet(inv.getItem(1));
		Player.getInventory().setChestplate(inv.getItem(2));
		Player.getInventory().setLeggings(inv.getItem(3));
		Player.getInventory().setBoots(inv.getItem(4));
		Player.getInventory().setItemInOffHand(inv.getItem(6));
		
		for(int i = 0; i < 9; ++i)
		{
			Player.getInventory().setItem(i, inv.getItem(i+9));
		}
		int delta = inv.getSize() - 36;
		for(int i = 9; i < 36; ++i)
		{
			Player.getInventory().setItem(i, inv.getItem(i+delta));
		}
		
		// Despawn the Chest
		Location loc = Player.getLocation();
		Location l = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(UserData.PreviousBlockTypes.containsKey(l))
		{
			// If its still a chest, restore the old block. Otherwise, drop it
			if(l.getBlock().getType() == Material.CHEST) l.getBlock().setType(UserData.PreviousBlockTypes.get(l));
			else if(l.getWorld() != null) l.getWorld().dropItemNaturally(l, new ItemStack(UserData.PreviousBlockTypes.get(l), 1));
			UserData.PreviousBlockTypes.remove(l);
		}
		else if(l.getBlock().getType() == Material.CHEST) l.getBlock().setType(Material.AIR);
	}
	
	public static void EnterInventory(String uuid, Location ChestLocation, PlayerInventory p_inv)
	{
		Inventory inv = Bukkit.createInventory(null, invSize, "§8PlayerChest - " + uuid);
		
		inv.setItem(1, p_inv.getHelmet());
		inv.setItem(2, p_inv.getChestplate());
		inv.setItem(3, p_inv.getLeggings());
		inv.setItem(4, p_inv.getBoots());
		inv.setItem(6, p_inv.getItemInOffHand());
		
		for(int i = 9; i < 18; ++i)
		{
			inv.setItem(i, p_inv.getItem(i-9));
		}
		for(int i = 0; i < 27; ++i)
		{
			inv.setItem(i + inv.getSize()-27, p_inv.getItem(i+9));
		}
		Location ChestLoc = new Location(ChestLocation.getWorld(), ChestLocation.getBlockX(), ChestLocation.getBlockY(), ChestLocation.getBlockZ());
		InventoryData.put(uuid, inv);
		AllUUIDsByChestLocations.put(ChestLoc, uuid);
	}
	
	public static void Load()
	{
		Init();
		if(folder.listFiles() == null) return;
		
		for(File f : Objects.requireNonNull(folder.listFiles()))
		{
			String uuid = f.getName().substring(0, f.getName().length()-4);
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
			
			Inventory inv = Bukkit.createInventory(null, invSize, "§8PlayerChest - " + uuid);
			
			for(int i = 0; i < inv.getSize(); i++) {
				inv.setItem(i, cfg.getItemStack("invSlot." + i));
			}
			
			InventoryData.put(uuid, inv);
			
			String w = "";
			int x, y, z;
			w = cfg.getString("ChestLocation.World");
			if(w == null || w.isEmpty()) w = Bukkit.getWorlds().get(0).getName();
			x = cfg.getInt("ChestLocation.X");
			y = cfg.getInt("ChestLocation.Y");
			z = cfg.getInt("ChestLocation.Z");
			Location loc = new Location(Bukkit.getWorld(w), x, y, z);
			AllUUIDsByChestLocations.put(loc, uuid);
			
			String prev = cfg.getString("PreviousType");
			if(prev != null && !prev.trim().isEmpty() && !prev.equals("AIR")) PreviousBlockTypes.put(loc, Material.getMaterial(prev));
			
			// remove cfg so it doesnt occupy the file so .delete() deletes it
			cfg = null;
			Runtime.getRuntime().gc();
			f.delete();
		}
		System.out.println("Imported all inventory data");
	}
	
	public static void SaveAllInvs()
	{
		ArrayList<String> saved = new ArrayList<>();
		for(String uuid : InventoryData.keySet())
		{
			// Avoid duplicates when leaving
			if(saved.contains(uuid)) continue;
			saved.add(uuid);
			
			File f = new File(s_folder + uuid + ".yml");
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
			
			Location ChestLoc = ChestLocationByUUID(uuid);
			String s;
			if(ChestLoc.getWorld() == null) s = Bukkit.getWorlds().get(0).getName();
			else s = ChestLoc.getWorld().getName();
			cfg.set("ChestLocation.World", ChestLoc.getWorld().getName());
			cfg.set("ChestLocation.X", ChestLoc.getBlockX());
			cfg.set("ChestLocation.Y", ChestLoc.getBlockY());
			cfg.set("ChestLocation.Z", ChestLoc.getBlockZ());
			
			// Set items
			Inventory inv = InventoryData.get(uuid);
			for(int i = 0; i < inv.getSize(); i++) {
				cfg.set("invSlot." + i, inv.getItem(i));
			}
			
			// Set Previous Block
			if(PreviousBlockTypes.containsKey(ChestLoc))
				cfg.set("PreviousType", PreviousBlockTypes.get(ChestLoc));
			
			// Save file
			try {
				cfg.save(f);
			} catch (IOException e) {
				System.out.println("Failed to save config file.");
			}
		}
	}

}
