package dk.fido2603.semihardcore;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerManager
{
	private SemiHardcore	plugin;

	private FileConfiguration			semihardcoreConfig				= null;
	private File						semihardcoreConfigFile			= null;
	private String						datePattern						= "HH:mm:ss dd-MM-yyyy";
	
	PlayerManager(SemiHardcore plugin)
	{
		this.plugin = plugin;
	}

	public void load()
	{
		if (this.semihardcoreConfigFile == null)
		{
			this.semihardcoreConfigFile = new File(this.plugin.getDataFolder(), "dead-players.yml");
		}
		this.semihardcoreConfig = YamlConfiguration.loadConfiguration(this.semihardcoreConfigFile);

		this.plugin.log("Loaded " + this.semihardcoreConfig.getKeys(false).size() + " player who have died.");
	}

	public void save()
	{
		if ((this.semihardcoreConfig == null) || (this.semihardcoreConfigFile == null))
		{
			return;
		}
		try
		{
			this.semihardcoreConfig.save(this.semihardcoreConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + this.semihardcoreConfigFile + ": " + ex.getMessage());
		}
	}
	
	public void newPlayerCheck(Player player)
	{
		UUID playerId = player.getUniqueId();
		
		if (!isDead(playerId)) {
			return;
		}
		if (!shouldPlayerBeUnbanned(playerId)) {
			player.kickPlayer("You still have time left on your death cooldown: " + (plugin.timeToBan-timeDiff(playerId)) + " hours!");
			return;
		}
		unbanPlayer(playerId);
	}
	
	public void unbanPlayer(UUID playerId) {
		this.semihardcoreConfig.set(playerId.toString() + ".IsDead", false);
	}
	
	public boolean isDead(UUID playerId) {
		return this.semihardcoreConfig.getBoolean(playerId.toString() + ".IsDead");
	}
	
	public boolean shouldPlayerBeUnbanned(UUID playerId) {
		long diff = timeDiff(playerId);
	    
	    if (diff >= plugin.timeToBan) {
	    	plugin.logDebug("Player can now be unbanned... :)");
	    	return true;
	    }
		
		return false;
	}
	
	public long timeDiff(UUID playerId) {
		Date deathTime = new Date();
		Date currentTime = new Date();
		
		String deathTimeStr = this.semihardcoreConfig.getString(playerId.toString() + ".LastDeath");
		
		try 
		{
			deathTime = new SimpleDateFormat(datePattern).parse(deathTimeStr);
		} 
		catch (ParseException e) 
		{
			plugin.log("Error while parsing datetime for player...: " + e.getMessage());
			return 0;
		}  
		
		long diffInMillis = Math.abs(currentTime.getTime() - deathTime.getTime());
	    long diff = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);
	    
	    plugin.logDebug("Diff in hours: " + diff);
	    
	    return diff;
	}
	
	public boolean banPlayer(Player player, UUID playerId)
	{
		Boolean banned = this.semihardcoreConfig.getBoolean(playerId.toString() + ".IsDead");
		
		if (banned == true)
		{
			plugin.log("Tried to ban a killed player that is already banned... UUID: " + playerId.toString());
			return false;
		}
		
		int deaths = this.semihardcoreConfig.getInt(playerId.toString() + ".Deaths");
		plugin.logDebug("Current deaths: " + deaths);
		deaths++;
		this.semihardcoreConfig.set(playerId.toString() + ".Deaths", deaths);
		
		Date currentTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		this.semihardcoreConfig.set(playerId.toString() + ".LastDeath", sdf.format(currentTime));
		
		this.semihardcoreConfig.set(playerId.toString() + ".IsDead", true);
		player.kickPlayer("You have been set on a cooldown for: " + plugin.timeToBan.toString() + " hours!");
		
		return true;
	}
}