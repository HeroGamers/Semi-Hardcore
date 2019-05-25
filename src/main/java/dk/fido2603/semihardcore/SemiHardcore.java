package dk.fido2603.semihardcore;

import dk.fido2603.semihardcore.listeners.PlayerListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SemiHardcore extends JavaPlugin
{
	public static boolean						pluginEnabled							= false;
	public boolean								vaultEnabled							= false;
	public static Server						server									= null;
	public boolean								debug									= false;
	public Integer 								timeToBan								= 24;
	private static FileConfiguration			config									= null;
	private static PlayerManager				playerManager							= null;
	private static PermissionsManager			permissionsManager						= null;
	
	private static Economy						economy									= null;
	private Commands							commands								= null;
	
	private static SemiHardcore					plugin;

	public static PermissionsManager getPermissionsManager()
	{
		return permissionsManager;
	}

	public static PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	public static Economy getEconomy()
	{
		return economy;
	}

	public void sendInfo(Player player, String message)
	{
		if (player == null)
		{
			log(message);
		}
		else
		{
			player.sendMessage(message);
		}
	}

	public void onDisable()
	{
		saveSettings();
		reloadSettings();

		pluginEnabled = false;
	}

	@Override
	public void onEnable()
	{
		PlayerListener playerListener = null;
		plugin = this;
		server = getServer();
		config = getConfig();

		this.commands = new Commands(this);

		pluginEnabled = true;

		permissionsManager = new PermissionsManager(this);
		playerManager = new PlayerManager(this);
		playerListener = new PlayerListener(this);
		

		PluginManager pm = getServer().getPluginManager();

		// Check for Vault
		if (pm.getPlugin("Vault") != null)
		{
			this.vaultEnabled = true;

			log("Vault detected.");

			RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economy = economyProvider.getProvider();
			}
			else
			{
				plugin.log("Vault not found.");
			}
		}
		else
		{
			log("Vault not found.");
		}
		
		getServer().getPluginManager().registerEvents(playerListener, this);

		loadSettings();
		saveSettings();

		permissionsManager.load();
		playerManager.load();
	}

	public void log(String message)
	{
		plugin.getLogger().info(message);
	}

	public void logDebug(String message)
	{
		if (this.debug)
		{
			plugin.getLogger().info(message);
		}
	}

	public void reloadSettings()
	{
		reloadConfig();
		loadSettings();
		
		getPlayerManager().load();
	}

	public void loadSettings()
	{
		config = getConfig();

		this.debug = config.getBoolean("Settings.Debug", false);
		this.timeToBan = config.getInt("Settings.TimeToBan", 24);
	}

	public void saveSettings()
	{
		config.set("Settings.Debug", Boolean.valueOf(this.debug));
		config.set("Settings.TimeToBan", this.timeToBan);

		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return this.commands.onCommand(sender, cmd, label, args);
	}

}