package net.justonedev.mc;

import net.justonedev.mc.event.ChestInteract;
import net.justonedev.mc.event.InventoryInteract;
import net.justonedev.mc.event.PlayerConnectHandler;
import net.justonedev.mc.type.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class DisconnectChest extends JavaPlugin {
	
	public static DisconnectChest Instance;
	
	@Override
	public void onEnable() {
		Instance = this;
		LoadConfig();
		UserData.Load();
		Bukkit.getPluginManager().registerEvents(new PlayerConnectHandler(), this);
		Bukkit.getPluginManager().registerEvents(new ChestInteract(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryInteract(), this);
		
		// Had this in for a lil bit, but i dont think its necessary. The bug came from somewhere else
		//for(Player p : Bukkit.getOnlinePlayers()) UserData.InvokePlayerJoined(p);
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
