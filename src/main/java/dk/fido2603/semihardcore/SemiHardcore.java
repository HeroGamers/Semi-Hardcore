package dk.fido2603.semihardcore;

import dk.fido2603.semihardcore.listeners.PlayerListener;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SemiHardcore extends JavaPlugin
{
	public static boolean						pluginEnabled							= false;
	public boolean								vaultEnabled							= false;
	public boolean								uhcDayEnabled							= true;
	public Integer								uhcDay									= 2;
	public boolean								isUHCDay								= false;
	public static Server						server									= null;
	public boolean								debug									= false;
	private String 								timeToBanString							= "24h";
	public String 								timeToBanStringUF						= "24 hours";
	public long									timeToBan								= 0;
	private static FileConfiguration			config									= null;
	private static PlayerManager				playerManager							= null;
	private static PermissionsManager			permissionsManager						= null;
	
	private static Economy						economy									= null;
	private Commands							commands								= null;
	
	private static SemiHardcore					plugin;
	private ConsoleCommandSender 				console;

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
	
	public void sendInfoAll(String message) {
		String translatedMessage = ChatColor.translateAlternateColorCodes('&', message);
		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			player.sendMessage(translatedMessage);
		}
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
		
		if (this.uhcDayEnabled) {
			logDebug("Checking to turn off UHC");
			
			if (isUHCDay) {
				getServer().dispatchCommand(console, "gamerule naturalRegeneration true");
				isUHCDay = false;
				log("Turned off UHC!");
			}
		}

		pluginEnabled = false;
	}

	@Override
	public void onEnable()
	{
		PlayerListener playerListener = null;
		plugin = this;
		server = getServer();
		config = getConfig();

		this.console = server.getConsoleSender();
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
				log("Vault not found.");
			}
		}
		else
		{
			log("Vault not found.");
		}
		
		getServer().getPluginManager().registerEvents(playerListener, this);

		loadSettings();
		saveSettings();
		
		if (this.uhcDayEnabled) {
			logDebug("UHC Day is enabled... checking");
			logDebug("Current day of week: " + TimeConverter.getDayOfWeek() + ". UHC Day: " + uhcDay);
			
			// now let's check every half minute, if it's UHC day
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
			{
				public void run()
				{
					if (TimeConverter.getDayOfWeek() == uhcDay && !isUHCDay) {
						log("It's time for UHC day, switching the gamerule");
						logDebug("Day: " + TimeConverter.getDayOfWeek() + " - UHCDay: " + plugin.uhcDay.toString());
						getServer().dispatchCommand(console, "gamerule naturalRegeneration false");
						isUHCDay = true;
						sendInfoAll("&6It is now UHC day! Take care, no more natural regen!");
					}
					else if (!(TimeConverter.getDayOfWeek() == uhcDay) && isUHCDay) {
						log("It's not UHC day anymore, switching the gamerule...");
						logDebug("Day: " + TimeConverter.getDayOfWeek() + " - UHCDay: " + plugin.uhcDay.toString());
						getServer().dispatchCommand(console, "gamerule naturalRegeneration true");
						isUHCDay = false;
						sendInfoAll("&6It is no longer UHC day!");
					}
				}
			}, 20L, 600L); // 1200 is the ideal number of ticks for a minute, so we'll check each half minute - waiting about a second for the first check though, to get the things started
		}

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
		this.timeToBanString = config.getString("Settings.TimeToBan", "24h");
		this.uhcDayEnabled = config.getBoolean("Settings.UHCDayEnabled", true);
		this.uhcDay = config.getInt("Settings.UHCDay", 2);
		
		this.timeToBan = TimeConverter.parseStringToMillis(timeToBanString);
		
		this.timeToBanStringUF = TimeConverter.parseMillisToUFString(timeToBan);
		
	}

	public void saveSettings()
	{
		config.set("Settings.Debug", Boolean.valueOf(this.debug));
		config.set("Settings.TimeToBan", this.timeToBanString);
		config.set("Settings.UHCDayEnabled", Boolean.valueOf(this.uhcDayEnabled));
		config.set("Settings.UHCDay", this.uhcDay);

		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return this.commands.onCommand(sender, cmd, label, args);
	}

}