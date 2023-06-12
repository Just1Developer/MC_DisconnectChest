package net.justonedev.mc;

import net.justonedev.mc.event.ChestInteract;
import net.justonedev.mc.event.InventoryInteract;
import net.justonedev.mc.event.PlayerConnectHandler;
import net.justonedev.mc.type.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class DisconnectChest extends JavaPlugin {
	
	// Future Plans: Maybe animate block opening with BlockData interfaces
	
	public static DisconnectChest Instance;
	int rotationsched;
	
	@Override
	public void onEnable() {
		Instance = this;
		LoadConfig();
		System.out.println("Using Configuration " + CurrentSetting);
		UserData.Load();
		Bukkit.getPluginManager().registerEvents(new PlayerConnectHandler(), this);
		Bukkit.getPluginManager().registerEvents(new ChestInteract(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryInteract(), this);
		
		// Had this in for a lil bit, but i dont think its necessary. The bug came from somewhere else
		//for(Player p : Bukkit.getOnlinePlayers()) UserData.InvokePlayerJoined(p);
		/*
		rotationsched = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for(String uuid : UserData.AllUUIDsByArmorStandID.keySet())
			{
				ArmorStand as = (ArmorStand) Bukkit.getEntity(UUID.fromString(uuid));
				//TODO set/update rotation
			}
		}, 1, 1);
		*/
	}
	
	@Override
	public void onDisable() {
		// Plugin shutdown logic
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getOpenInventory().getTitle().startsWith("§8PlayerChest - "))
				p.closeInventory();
			//UserData.InvokePlayerQuit(p);
		}
		UserData.SaveAllInvs();
	}

	File config = new File(getDataFolder() + "/config.yml");
	YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);

	public static Configuration CurrentSetting;

	public void LoadConfig()
	{
		if(!config.exists())
		{
			cfg.options().copyDefaults(true);
			cfg.addDefault("UseChestBlocks", true);
			cfg.addDefault("ChestDestroysBlocks", true);
			saveCfg();
		}
		if(cfg.getBoolean("UseChestBlocks"))
			if(cfg.getBoolean("ChestDestroysBlocks")) CurrentSetting = Configuration.BlockChestReplace;
			else CurrentSetting = Configuration.BlockChestEvade;
		else CurrentSetting = Configuration.EntityChest;
	}

	private void saveCfg()
	{
		try {
			cfg.save(config);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
