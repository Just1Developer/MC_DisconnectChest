package net.justonedev.mc;

import net.justonedev.mc.type.Configuration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserData {
	
	static String s_folder = DisconnectChest.Instance.getDataFolder() + "/Userdata/";
	static File folder = new File(s_folder);
	
	// These chests only drop the content in the file, not even a chest themselves. Because that would be like. infinite chests.
	public static final HashMap<Location, String> AllUUIDsByChestLocations = new HashMap<>();
	
	public static final HashMap<String, String> AllUUIDsByArmorStandID = new HashMap<>();
	
	public static final HashMap<String, String> PlayerNamesByUUID = new HashMap<>();
	public static final HashMap<String, Configuration> InvSaveSettingByUUID = new HashMap<>();
	
	private static Location ChestLocationByUUID(String uuid)
	{
		for(Location key : AllUUIDsByChestLocations.keySet())
		{
			if(AllUUIDsByChestLocations.get(key).equals(uuid)) return key;
		}
		return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	}

	private static String ArmorStandIDByUUID(String uuid)
	{
		for(String key : AllUUIDsByArmorStandID.keySet())
		{
			if(AllUUIDsByArmorStandID.get(key).equals(uuid)) return key;
		}
		return "";
	}
	
	public static ItemStack EmptyStack, HeadFiller, ChestFiller, LegsFiller, FootFiller, SilkTouchPickaxe;
	public static final String SlotFillerPrefix = "§7Armor Slot - ";
	
	public static final HashMap<String, Inventory> InventoryData = new HashMap<>();
	//public static final HashMap<String, BlockDataClass> PrevBlockData = new HashMap<>();
	private static void Init()
	{
		EmptyStack = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
		ItemMeta meta = EmptyStack.getItemMeta();
		if(meta != null) meta.setDisplayName("§f ");
		EmptyStack.setItemMeta(meta);

		HeadFiller = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
		meta = HeadFiller.getItemMeta();
		if(meta != null) meta.setDisplayName(SlotFillerPrefix + "Helmet");
		HeadFiller.setItemMeta(meta);

		ChestFiller = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
		meta = ChestFiller.getItemMeta();
		if(meta != null) meta.setDisplayName(SlotFillerPrefix + "Chestplate");
		ChestFiller.setItemMeta(meta);

		LegsFiller = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
		meta = LegsFiller.getItemMeta();
		if(meta != null) meta.setDisplayName(SlotFillerPrefix + "Leggings");
		LegsFiller.setItemMeta(meta);

		FootFiller = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
		meta = FootFiller.getItemMeta();
		if(meta != null) meta.setDisplayName(SlotFillerPrefix + "Boots");
		FootFiller.setItemMeta(meta);
		
		SilkTouchPickaxe = new ItemStack(Material.NETHERITE_PICKAXE, 1);
		meta = SilkTouchPickaxe.getItemMeta();
		if(meta != null) meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		SilkTouchPickaxe.setItemMeta(meta);
		
		if(folder.exists()) return;
		folder.mkdirs();
	}
	
	final static int invSize = 45;	// Change to 54 if you want an empty row in between hotbar & rest of the inventory
	
	public static void RemovePlayerChest(Location location)
	{
		if(!AllUUIDsByChestLocations.containsKey(location)) return;
		InventoryData.get(AllUUIDsByChestLocations.get(location)).clear();
		// Do we do this?
		AllUUIDsByChestLocations.remove(location);
	}
	
	public static void RemovePlayerChest(String ArmorStandUUID)
	{
		if(!AllUUIDsByArmorStandID.containsKey(ArmorStandUUID)) return;
		InventoryData.get(AllUUIDsByArmorStandID.get(ArmorStandUUID)).clear();
	}
	
	public static Inventory GetInventory(String uuid)
	{
		if(InventoryData.containsKey(uuid)) return InventoryData.get(uuid);
		return Bukkit.createInventory(null, invSize, "§8PlayerChest - " + uuid);
	}
	
	public static void InvokePlayerQuit(Player Player)
	{
		Location loc = Player.getLocation();
		Material Type = Material.AIR;
		if(loc.getBlock().getType() != Material.AIR)
		{
			Location l = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			if(DisconnectChest.CurrentSetting == Configuration.BlockChestEvade)
			{
				System.out.println("First location: " + l + "   [" + l.getBlock().getType() + "]");
				while(l.getBlockY() <= Player.getWorld().getMaxHeight() && l.getBlock().getType() != Material.AIR)
					l = new Location(l.getWorld(), l.getBlockX(), l.getBlockY() + 1, l.getBlockZ());
			}
			//PreviousBlockTypes.put(l, l.getBlock().getType());
			// e.g. Water doesnt drop anything
			Collection<ItemStack> drops = l.getBlock().getDrops(SilkTouchPickaxe);
			if(!drops.isEmpty()) Type = drops.toArray(new ItemStack[0])[0].getType();   //e.getBlock().getType();
			loc = l;
		}

		// Spawn chest
		String entityID = "";
		if(DisconnectChest.CurrentSetting == Configuration.EntityChest)
		{
			Type = Material.AIR;	// Entity obv doesnt destroy block
			entityID = InteractiveChestEntity.spawnChest(Player);
		}

		PlayerInventory p_inv = Player.getInventory();
		EnterInventory(Player.getName(), Player.getUniqueId().toString(), loc, entityID, p_inv, Type);

		if(DisconnectChest.CurrentSetting != Configuration.EntityChest)
		{
			loc.getBlock().setType(Material.CHEST);
		}
	}
	
	public static void InvokePlayerJoined(Player Player)
	{
		// Cache player name for inv titles after server restart
		if(!PlayerNamesByUUID.containsKey(Player.getUniqueId().toString())) PlayerNamesByUUID.put(Player.getUniqueId().toString(), Player.getName());
		
		// Server crashed or sumn
		if(!InventoryData.containsKey(Player.getUniqueId().toString())) return;
		// If the server did not crash, there should be a saved inventory. Only then wipe the old one and load it.
		Player.getInventory().clear();
		// Despawn chest
		Inventory inv = GetInventory(Player.getUniqueId().toString());
		while(inv.getViewers().size() > 0)
		{
			inv.getViewers().get(0).closeInventory();
		}
		
		if(inv.getItem(1) != null && (!inv.getItem(1).hasItemMeta() || !inv.getItem(1).getItemMeta().getDisplayName().startsWith(UserData.SlotFillerPrefix))) Player.getInventory().setHelmet(inv.getItem(1));
		if(inv.getItem(2) != null && (!inv.getItem(2).hasItemMeta() || !inv.getItem(2).getItemMeta().getDisplayName().startsWith(UserData.SlotFillerPrefix))) Player.getInventory().setChestplate(inv.getItem(2));
		if(inv.getItem(3) != null && (!inv.getItem(3).hasItemMeta() || !inv.getItem(3).getItemMeta().getDisplayName().startsWith(UserData.SlotFillerPrefix))) Player.getInventory().setLeggings(inv.getItem(3));
		if(inv.getItem(4) != null && (!inv.getItem(4).hasItemMeta() || !inv.getItem(4).getItemMeta().getDisplayName().startsWith(UserData.SlotFillerPrefix))) Player.getInventory().setBoots(inv.getItem(4));
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

		// Remove Stored Inventory Data, no?
		InventoryData.remove(Player.getUniqueId().toString());
		
		Configuration CurrentSaveSetting;
		if(InvSaveSettingByUUID.containsKey(Player.getUniqueId().toString()))
		{
			CurrentSaveSetting = InvSaveSettingByUUID.get(Player.getUniqueId().toString());
			InvSaveSettingByUUID.remove(Player.getUniqueId().toString());
		}
		else CurrentSaveSetting = DisconnectChest.CurrentSetting;
		
		// Despawn the Chest
		Location loc = Player.getLocation();
		if(CurrentSaveSetting == Configuration.BlockChestEvade)
		{
			// Search for correct location
			for(Location loc2 : AllUUIDsByChestLocations.keySet())
			{
				if(!AllUUIDsByChestLocations.get(loc2).equals(Player.getUniqueId().toString())) continue;
				loc = loc2;
				break;
			}
		}
		
		if(CurrentSaveSetting == Configuration.EntityChest)
		{
			for(Entity e : Player.getWorld().getNearbyEntities(loc, 5, 5, 5))
			{
				if(!AllUUIDsByArmorStandID.containsKey(e.getUniqueId().toString())) continue;
				if(!AllUUIDsByArmorStandID.get(e.getUniqueId().toString()).equals(Player.getUniqueId().toString())) continue;
				// Entity found, despawn
				// Remove entry from list of course
				AllUUIDsByArmorStandID.remove(e.getUniqueId().toString());
				e.remove();
				break;
			}
		}
		else
		{
			Location l = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			
			// Remove entry from list of course
			AllUUIDsByChestLocations.remove(l);
			
			if(inv.getItem(8) != null && inv.getItem(8).getType() != Material.AIR && !inv.getItem(8).getItemMeta().getDisplayName().equals("§f "))
			{
				if(l.getBlock().getType() == Material.CHEST)
				{
					l.getBlock().setType(inv.getItem(8).getType());

					// ?. Like i mustve had a reason for this but ?
					ArrayList<String> lore = new ArrayList<>();
					if(inv.getItem(8).getItemMeta().hasLore()) lore.addAll(inv.getItem(8).getItemMeta().getLore());

					for (String _lore : lore)
					{
						if(!_lore.contains("%0")) continue;
						// All combined into 1 line:
						String[] data = _lore.split("%0");
						for(String s : data)
						{
							if(s.startsWith("Type:")) {
								Material m = Material.valueOf(s.substring(5));
								l.getBlock().setType(m);
							}
							
							if(s.startsWith("§8STxt:") && l.getBlock().getState() instanceof Sign) {
								Sign sign = (Sign) l.getBlock().getState();
								String[] text = s.substring(7).split("%2");
								sign.setLine(0, text[0].replace("%1", "%"));
								sign.setLine(1, text[1].replace("%1", "%"));
								sign.setLine(2, text[2].replace("%1", "%"));
								sign.setLine(3, text[3].replace("%1", "%"));
								sign.update();
							}
							
							if(s.startsWith("SData:") && l.getBlock().getState() instanceof Sign)
							{
								Sign sign = (Sign) l.getBlock().getState();
								sign.setGlowingText(s.contains(":gt:"));
								sign.setEditable(s.contains(":et:"));
								sign.setColor(DyeColor.valueOf(s.split("color=")[1]));
								sign.update();
							}
							
							if(s.startsWith("§8Face:") && l.getBlock().getBlockData() instanceof Directional) {
								Directional blockdata = (Directional) l.getBlock().getBlockData();
								blockdata.setFacing(BlockFace.valueOf(s.substring(7)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Age:") && l.getBlock().getBlockData() instanceof Ageable) {
								Ageable blockdata = (Ageable) l.getBlock().getBlockData();
								blockdata.setAge(Integer.parseInt(s.substring(6)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Atc:") && l.getBlock().getBlockData() instanceof Attachable) {
								Attachable blockdata = (Attachable) l.getBlock().getBlockData();
								blockdata.setAttached(Boolean.parseBoolean(s.substring(6)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Bi:") && l.getBlock().getBlockData() instanceof Bisected) {
								Bisected blockdata = (Bisected) l.getBlock().getBlockData();
								blockdata.setHalf(Bisected.Half.valueOf(s.substring(5)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8FaceAtc:") && l.getBlock().getBlockData() instanceof FaceAttachable) {
								FaceAttachable blockdata = (FaceAttachable) l.getBlock().getBlockData();
								blockdata.setAttachedFace(FaceAttachable.AttachedFace.valueOf(s.substring(10)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Hang:") && l.getBlock().getBlockData() instanceof Hangable) {
								Hangable blockdata = (Hangable) l.getBlock().getBlockData();
								blockdata.setHanging(Boolean.parseBoolean(s.substring(7)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Lvl:") && l.getBlock().getBlockData() instanceof Levelled) {
								Levelled blockdata = (Levelled) l.getBlock().getBlockData();
								blockdata.setLevel(Integer.parseInt(s.substring(6)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Pwr:") && l.getBlock().getBlockData() instanceof Powerable) {
								Powerable blockdata = (Powerable) l.getBlock().getBlockData();
								blockdata.setPowered(Boolean.parseBoolean(s.substring(6)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Rail:") && l.getBlock().getBlockData() instanceof Rail) {
								Rail blockdata = (Rail) l.getBlock().getBlockData();
								blockdata.setShape(Rail.Shape.valueOf(s.substring(8)));
								blockdata.setWaterlogged(s.charAt(7) == 't');
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Wtr:") && l.getBlock().getBlockData() instanceof Waterlogged) {
								Waterlogged blockdata = (Waterlogged) l.getBlock().getBlockData();
								blockdata.setWaterlogged(Boolean.parseBoolean(s.substring(6)));
								l.getBlock().setBlockData(blockdata);
							}
							
							// Unsure about this: (necessity)
							
							if(s.startsWith("§8Snow:") && l.getBlock().getBlockData() instanceof Snowable) {
								Snowable blockdata = (Snowable) l.getBlock().getBlockData();
								blockdata.setSnowy(Boolean.parseBoolean(s.substring(7)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Light:") && l.getBlock().getBlockData() instanceof Lightable) {
								Lightable blockdata = (Lightable) l.getBlock().getBlockData();
								blockdata.setLit(Boolean.parseBoolean(s.substring(8)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Ornt:") && l.getBlock().getBlockData() instanceof Orientable) {
								Orientable blockdata = (Orientable) l.getBlock().getBlockData();
								blockdata.setAxis(Axis.valueOf(s.substring(7)));
								l.getBlock().setBlockData(blockdata);
							}
							
							if(s.startsWith("§8Rot:") && l.getBlock().getBlockData() instanceof Rotatable) {
								Rotatable blockdata = (Rotatable) l.getBlock().getBlockData();
								blockdata.setRotation(BlockFace.valueOf(s.substring(6)));
								l.getBlock().setBlockData(blockdata);
							}
						}
					}

					if(l.getBlock().getType().toString().contains("SIGN"))
					{
						// Will this work? No idea. But I think somehow something
						// like this worked in 1.8
						Sign s = (Sign) l.getBlock().getState();
						String text = s.getLine(0) + "\n" + s.getLine(1) + "\n" + s.getLine(2) + "\n" + s.getLine(3);
						lore.add("§8Sign Text: " + text);
					}

				}
				else if(l.getWorld() != null) l.getWorld().dropItemNaturally(l, new ItemStack(inv.getItem(8).getType(), 1));
			}
			else if(l.getBlock().getType() == Material.CHEST) l.getBlock().setType(Material.AIR);
		}
	}
	
	public static void EnterInventory(String playerName, String uuid, Location ChestLocation, String EntityID, PlayerInventory p_inv, Material PreviousMaterial)
	{
		Inventory inv = Bukkit.createInventory(null, invSize, "§8PlayerChest - " + playerName);
		
		ItemStack stack = EmptyStack;
		if(PreviousMaterial != Material.AIR)
		{
			Block block = ChestLocation.getBlock();
			stack = new ItemStack(PreviousMaterial, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName("§7Previous Item");
			ArrayList<String> lore = new ArrayList<>();
			lore.add("§8If left here, will be placed");
			lore.add("§8back upon the player rejoining.");
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			String data = "";
			
			// Add data
			
			if(block.getType() != PreviousMaterial)	// If the drop item is different, save the og item
			{
				data += "§8%0Type:" + block.getType();
			}
			
			if(PreviousMaterial.toString().contains("SIGN"))
			{
				// Will this work? No idea. But I think somehow something
				// like this worked in 1.8
				Sign s = (Sign) block.getState();
				String text = s.getLine(0).replace("%", "%1") + "%2" +
						s.getLine(1).replace("%", "%1") + "%2" +
						s.getLine(2).replace("%", "%1") + "%2" +
						s.getLine(3).replace("%", "%1");
				//lore.add("§8Sign Text: " + text);
				//lore.add("§8SData:g" + (s.isGlowingText() ? "t" : "f") + ":e" + (s.isEditable() ? "t" : "f") + ":color-" + s.getColor());
				data += "§8%0§8STxt:" + text;
				data += "%0" + "SData:g" + (s.isGlowingText() ? "t" : "f") + ":e" + (s.isEditable() ? "t" : "f") + ":color=" + s.getColor();
			}
			
			if(block.getBlockData() instanceof Directional) {
				Directional blockmeta = (Directional) block.getBlockData();
				//lore.add("§8Facing: " + bmeta.getFacing());
				data += "%0§8Face:" + blockmeta.getFacing();
			}
			
			if(block.getBlockData() instanceof Ageable) {
				Ageable blockmeta = (Ageable) block.getBlockData();
				data += "%0§8Age:" + blockmeta.getAge();
			}
			
			if(block.getBlockData() instanceof Attachable) {
				Attachable blockmeta = (Attachable) block.getBlockData();
				data += "%0§8Atc:" + blockmeta.isAttached();
			}
			
			if(block.getBlockData() instanceof Bisected) {
				Bisected blockmeta = (Bisected) block.getBlockData();
				data += "%0§8Bi:" + blockmeta.getHalf();
			}
			
			if(block.getBlockData() instanceof FaceAttachable) {
				FaceAttachable blockmeta = (FaceAttachable) block.getBlockData();
				data += "%0§8FaceAtc:" + blockmeta.getAttachedFace();
			}
			
			if(block.getBlockData() instanceof Hangable) {
				Hangable blockmeta = (Hangable) block.getBlockData();
				data += "%0§8Hang:" + blockmeta.isHanging();
			}
			
			if(block.getBlockData() instanceof Levelled) {
				Levelled blockmeta = (Levelled) block.getBlockData();
				data += "%0§8Lvl:" + blockmeta.getLevel();
			}
			
			if(block.getBlockData() instanceof Powerable) {
				Powerable blockmeta = (Powerable) block.getBlockData();
				data += "%0§8Pwr:" + blockmeta.isPowered();
			}
			
			if(block.getBlockData() instanceof Rail) {
				Rail blockmeta = (Rail) block.getBlockData();
				data += "%0§8Rail:" + (blockmeta.isWaterlogged() ? "t" : "f") + blockmeta.getShape();
			}
			
			if(block.getBlockData() instanceof Waterlogged) {
				Waterlogged blockmeta = (Waterlogged) block.getBlockData();
				data += "%0§8Wtr:" + blockmeta.isWaterlogged();
			}
			
			// Unsure abt necessity of these:
			
			if(block.getBlockData() instanceof Snowable) {
				Snowable blockmeta = (Snowable) block.getBlockData();
				data += "%0§8Snow:" + blockmeta.isSnowy();
			}
			
			if(block.getBlockData() instanceof Lightable) {
				Lightable blockmeta = (Lightable) block.getBlockData();
				data += "%0§8Light:" + blockmeta.isLit();
			}
			
			if(block.getBlockData() instanceof Orientable) {
				Orientable blockmeta = (Orientable) block.getBlockData();
				data += "%0§8Ornt:" + blockmeta.getAxis();
			}
			
			if(block.getBlockData() instanceof Rotatable) {
				Rotatable blockmeta = (Rotatable) block.getBlockData();
				data += "%0§8Rot:" + blockmeta.getRotation();
			}
			
			lore.add(data);
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		else if(ChestLocation.getBlock().getType() != PreviousMaterial)	// If the drop item is different, save the og item
		{
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName("§0 ");	// Change display name because "§f " is ignored when joining
			ArrayList<String> lore = new ArrayList<>();
			lore.add("§8%0Type:" + ChestLocation.getBlock().getType());
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		
		inv.setItem(0, EmptyStack);
		inv.setItem(5, EmptyStack);
		inv.setItem(7, EmptyStack);
		inv.setItem(8, stack);
		
		inv.setItem(1, p_inv.getHelmet());
		inv.setItem(2, p_inv.getChestplate());
		inv.setItem(3, p_inv.getLeggings());
		inv.setItem(4, p_inv.getBoots());
		inv.setItem(6, p_inv.getItemInOffHand());
		if(inv.getItem(1) == null) inv.setItem(1, HeadFiller);
		if(inv.getItem(2) == null) inv.setItem(2, ChestFiller);
		if(inv.getItem(3) == null) inv.setItem(3, LegsFiller);
		if(inv.getItem(4) == null) inv.setItem(4, FootFiller);
		
		for(int i = 9; i < 18; ++i)
		{
			inv.setItem(i, p_inv.getItem(i-9));
		}
		for(int i = 0; i < 27; ++i)
		{
			inv.setItem(i + inv.getSize()-27, p_inv.getItem(i+9));
		}
		
		InvSaveSettingByUUID.put(uuid, DisconnectChest.CurrentSetting);
		
		Location ChestLoc = new Location(ChestLocation.getWorld(), ChestLocation.getBlockX(), ChestLocation.getBlockY(), ChestLocation.getBlockZ());
		InventoryData.put(uuid, inv);
		if(DisconnectChest.CurrentSetting == Configuration.EntityChest) AllUUIDsByArmorStandID.put(EntityID, uuid);
		else AllUUIDsByChestLocations.put(ChestLoc, uuid);
	}
	
	public static void Load()
	{
		Init();
		if(folder.listFiles() == null) return;
		
		for(File f : Objects.requireNonNull(folder.listFiles()))
		{
			String uuid = f.getName().substring(0, f.getName().length()-4);
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

			Player p = Bukkit.getPlayer(UUID.fromString(uuid));
			String playername = cfg.getString("Playername");
			if(playername == null || playername.isEmpty())
			{
				playername = p != null ? p.getName() : uuid;
			}
			else
			{
				PlayerNamesByUUID.put(uuid, playername);
			}
			
			Configuration SaveConfig;
			String cfg_SaveConfig = cfg.getString("SaveConfiguration");
			
			if(cfg_SaveConfig != null && !cfg_SaveConfig.isEmpty()) SaveConfig = Configuration.valueOf(cfg_SaveConfig);
			else SaveConfig = Configuration.BlockChestEvade;	// Will still search for chest, other way around doesn't work
			InvSaveSettingByUUID.put(uuid, SaveConfig);
			
			Inventory inv = Bukkit.createInventory(null, invSize, "§8PlayerChest - " + playername);
			
			for(int i = 0; i < inv.getSize(); i++) {
				inv.setItem(i, cfg.getItemStack("invSlot." + i));
			}
			
			InventoryData.put(uuid, inv);
			
			if(SaveConfig == Configuration.EntityChest) {
				AllUUIDsByArmorStandID.put(cfg.getString("EntityUUID"), uuid);
			}
			else
			{
				String w;
				int x, y, z;
				w = cfg.getString("ChestLocation.World");
				if(w == null || w.isEmpty()) w = Bukkit.getWorlds().get(0).getName();
				x = cfg.getInt("ChestLocation.X");
				y = cfg.getInt("ChestLocation.Y");
				z = cfg.getInt("ChestLocation.Z");
				Location loc = new Location(Bukkit.getWorld(w), x, y, z);
				System.out.println("UUID: " + uuid.substring(0, 5) + "..., Loc: " + loc + ")");
				AllUUIDsByChestLocations.put(loc, uuid);
			}
			
			//String prev = cfg.getString("PreviousType");
			//if(prev != null && !prev.trim().isEmpty() && !prev.equals("AIR")) PreviousBlockTypes.put(loc, Material.getMaterial(prev));
			
			// remove cfg so it doesnt occupy the file so .delete() deletes it *(necessary?)*
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
			
			Player p = Bukkit.getPlayer(UUID.fromString(uuid));
			if(p != null) cfg.set("Playername", p.getName());
			else if(PlayerNamesByUUID.containsKey(uuid)) cfg.set("Playername", PlayerNamesByUUID.get(uuid));
			
			String saveSetting = DisconnectChest.CurrentSetting.toString();
			if(InvSaveSettingByUUID.containsKey(uuid)) saveSetting = InvSaveSettingByUUID.get(uuid).toString();
			cfg.set("SaveConfiguration", saveSetting);

			if(AllUUIDsByArmorStandID.containsValue(uuid))
			{
				cfg.set("SaveType", "Entity");
				cfg.set("EntityUUID", ArmorStandIDByUUID(uuid));
			}
			else
			{
				cfg.set("SaveType", "Block");
				Location ChestLoc = ChestLocationByUUID(uuid);
				String s;
				if(ChestLoc.getWorld() == null) s = Bukkit.getWorlds().get(0).getName();
				else s = ChestLoc.getWorld().getName();
				cfg.set("ChestLocation.World", ChestLoc.getWorld().getName());
				cfg.set("ChestLocation.X", ChestLoc.getBlockX());
				cfg.set("ChestLocation.Y", ChestLoc.getBlockY());
				cfg.set("ChestLocation.Z", ChestLoc.getBlockZ());
			}
			
			// Set items
			Inventory inv = InventoryData.get(uuid);
			while(inv.getViewers().size() > 0)
			{
				inv.getViewers().get(0).closeInventory();
			}
			for(int i = 0; i < inv.getSize(); i++) {
				cfg.set("invSlot." + i, inv.getItem(i));
			}
			
			// Save file
			try {
				cfg.save(f);
			} catch (IOException e) {
				System.out.println("Failed to save config file.");
			}
		}
	}
}
