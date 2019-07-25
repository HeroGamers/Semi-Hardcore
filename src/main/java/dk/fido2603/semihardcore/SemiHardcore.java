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
	
	public boolean								uhcDayEnabled							= false;
	public Integer								uhcDay									= 2;
	public boolean								isUHCDay								= false;
	
	public boolean								playSoundEnabled						= true;
	private String								uhcStartSound							= "entity.wither.spawn";
	private String								uhcEndSound								= "entity.ender_dragon.growl";
	
	public String								messageLoginUHCDay						= "&4-> &6It is currently UHC day! Take care, there is no natural regen!&4 <-";
	public String								messageBanPlayer						= "&c{playerName} has been banned for &4{timeBanned}&c!";
	public String								messageKickPlayerOnBan					= "&4You have been set on a cooldown for: &c{banTime}&4!";
	public String								messageKickPlayerTimeLeft				= "&cYou still have time left on your death cooldown: &4{timeLeft}&c!";
	private String								messageStartUHCDay						= "&4-> &6It is now UHC day! Take care, no more natural regen! &4<-";
	private String								messageEndUHCDay						= "&4-> &6It is no longer UHC day! &4<-";
	
	public static Server						server									= null;
	public boolean								debug									= false;
	public boolean								instantSave								= false;
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
		String translatedMessage = ChatColor.translateAlternateColorCodes('&', message);
		if (player == null)
		{
			log(translatedMessage);
		}
		else
		{
			player.sendMessage(translatedMessage);
		}
	}
	
	public void playSoundAll(String sound) {
		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
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

		permissionsManager = new PermissionsManager(this);
		
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
						sendInfoAll(plugin.messageStartUHCDay);
						if (plugin.playSoundEnabled) {
							plugin.playSoundAll(plugin.uhcStartSound);
						}
					}
					else if (!(TimeConverter.getDayOfWeek() == uhcDay) && isUHCDay) {
						log("It's not UHC day anymore, switching the gamerule...");
						logDebug("Day: " + TimeConverter.getDayOfWeek() + " - UHCDay: " + plugin.uhcDay.toString());
						getServer().dispatchCommand(console, "gamerule naturalRegeneration true");
						isUHCDay = false;
						sendInfoAll(plugin.messageEndUHCDay);
						if (plugin.playSoundEnabled) {
							plugin.playSoundAll(plugin.uhcEndSound);
						}
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
			plugin.getLogger().info("[Debug] " + message);
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
		this.instantSave = config.getBoolean("Settings.InstantSave", false);
		this.timeToBanString = config.getString("Settings.TimeToBan", "24h");
		
		this.uhcDayEnabled = config.getBoolean("Misc.UHCDayEnabled", false);
		this.uhcDay = config.getInt("Misc.UHCDay", 2);
		this.playSoundEnabled = config.getBoolean("Misc.PlaySounds", true);
		this.uhcStartSound = config.getString("Misc.Sound_UHCStart", "entity.ender_dragon.growl");
		this.uhcEndSound = config.getString("Misc.Sound_UHCEnd", "entity.wither.spawn");
		
		this.messageLoginUHCDay = config.getString("Messages.LoginUHCDay", "&4-> &6It is currently UHC day! Take care, there is no natural regen!&4 <-");
		this.messageBanPlayer = config.getString("Messages.BanPlayer", "&c{playerName} has been banned for &4{timeBanned}&c!");
		this.messageKickPlayerOnBan = config.getString("Messages.KickPlayerOnBan", "&4You have been set on a cooldown for: &c{banTime}&4!");
		this.messageKickPlayerTimeLeft = config.getString("Messages.PlayerTimeLeft", "&cYou still have time left on your death cooldown: &4{timeLeft}&c!");
		this.messageStartUHCDay = config.getString("Messages.StartUHCDay", "&4-> &6It is now UHC day! Take care, no more natural regen! &4<-");
		this.messageEndUHCDay = config.getString("Messages.EndUHCDay", "&4-> &6It is no longer UHC day! &4<-");
		
		this.timeToBan = TimeConverter.parseStringToMillis(timeToBanString);
		
		this.timeToBanStringUF = TimeConverter.parseMillisToUFString(timeToBan);
		
	}

	public void saveSettings()
	{
		config.set("Settings.Debug", Boolean.valueOf(this.debug));
		config.set("Settings.InstantSave", Boolean.valueOf(this.instantSave));
		config.set("Settings.TimeToBan", this.timeToBanString);
		
		config.set("Misc.UHCDayEnabled", Boolean.valueOf(this.uhcDayEnabled));
		config.set("Misc.UHCDay", this.uhcDay);
		config.set("Misc.PlaySounds", this.playSoundEnabled);
		config.set("Misc.Sound_UHCStart", this.uhcStartSound);
		config.set("Misc.Sound_UHCEnd", this.uhcEndSound);
		
		config.set("Messages.LoginUHCDay", this.messageLoginUHCDay);
		config.set("Messages.BanPlayer", this.messageBanPlayer);
		config.set("Messages.KickPlayerOnBan", this.messageKickPlayerOnBan);
		config.set("Messages.PlayerTimeLeft", this.messageKickPlayerTimeLeft);
		config.set("Messages.StartUHCDay", this.messageStartUHCDay);
		config.set("Messages.EndUHCDay", this.messageEndUHCDay);

		saveConfig();
		getPlayerManager().save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return this.commands.onCommand(sender, cmd, label, args);
	}

}