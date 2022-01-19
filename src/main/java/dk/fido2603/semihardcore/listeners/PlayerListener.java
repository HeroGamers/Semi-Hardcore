package dk.fido2603.semihardcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dk.fido2603.semihardcore.SemiHardcore;

public class PlayerListener implements Listener
{
	private SemiHardcore	plugin	= null;

	public PlayerListener(SemiHardcore plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if (!SemiHardcore.pluginEnabled)
		{
			return;
		}
		if (plugin.uhcDayEnabled && plugin.isUHCDay) {
			plugin.logDebug("Trying to send player the It is UHC day message!");
			plugin.sendInfo(event.getPlayer(), plugin.messageLoginUHCDay);
		}
		if (!event.getPlayer().isOp() && (!SemiHardcore.getPermissionsManager().hasPermission(event.getPlayer(), "semihardcore.exempt"))) {
			plugin.logDebug("No op or exempt, checking player");
			SemiHardcore.getPlayerManager().newPlayerCheck(event);
			return;
		}
		plugin.logDebug("Player had op or exempt...");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (!event.getEntity().isOp() && (!SemiHardcore.getPermissionsManager().hasPermission(event.getEntity(), "semihardcore.exempt"))) {
			plugin.logDebug("No op or exempt, banning player");
			SemiHardcore.getPlayerManager().banPlayer(event.getEntity(), event.getEntity().getUniqueId());
			return;
		}
		plugin.logDebug("Player had op or exempt...");
	}
	
	@EventHandler()
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		if (SemiHardcore.getPlayerManager().isBanned(event.getPlayer().getUniqueId())) {
			event.setQuitMessage("");
			plugin.logDebug("tried to remove leave message...");
			return;
		}
	}
}
