package net.justonedev.mc.event;

import net.justonedev.mc.InteractiveChestEntity;
import net.justonedev.mc.UserData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class PlayerConnectHandler implements Listener {

	@EventHandler
	public void onDisconnect(PlayerQuitEvent e)
	{
		UserData.InvokePlayerQuit(e.getPlayer());
		//InteractiveChestEntity.spawnArmorStand(e.getPlayer().getLocation());
	}
	
	@EventHandler
	public void onConnect(PlayerJoinEvent e)
	{
		UserData.InvokePlayerJoined(e.getPlayer());
	}

}
