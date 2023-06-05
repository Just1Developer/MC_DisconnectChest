package net.justonedev.mc.event;

import net.justonedev.mc.UserData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectHandler implements Listener {

	@EventHandler
	public void onDisconnect(PlayerQuitEvent e)
	{
		UserData.InvokePlayerQuit(e.getPlayer());
	}
	
	@EventHandler
	public void onConnect(PlayerJoinEvent e)
	{
		UserData.InvokePlayerJoined(e.getPlayer());
	}

}
