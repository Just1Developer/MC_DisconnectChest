package net.justonedev.mc;

import net.justonedev.mc.event.ChestInteract;
import net.justonedev.mc.event.InventoryInteract;
import net.justonedev.mc.event.PlayerConnectHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisconnectChest extends JavaPlugin {
	
	public static DisconnectChest Instance;
	
	@Override
	public void onEnable() {
		Instance = this;
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
}
