package dk.fido2603.semihardcore;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerManager
{
	private SemiHardcore	plugin;

	private FileConfiguration			semihardcoreConfig				= null;
	private File						semihardcoreConfigFile			= null;
	private long						lastSaveTime					= 0L;
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
		this.lastSaveTime = System.currentTimeMillis();
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

	public void saveTimed()
	{
		if (plugin.instantSave) {
			save();
			return;
		}

		if (System.currentTimeMillis() - this.lastSaveTime < 180000L)
		{
			return;
		}

		save();
	}

	public void newPlayerCheck(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		UUID playerId = player.getUniqueId();

		if (!isDead(playerId)) {
			return;
		}
		if (!shouldPlayerBeUnbanned(playerId)) {
			// Remove join message
			event.setJoinMessage("");
			// Delay the kick, to make sure the player is gone...
			SemiHardcore.server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					String kickMessage = plugin.messageKickPlayerTimeLeft.replace("{timeLeft}", TimeConverter.parseMillisToUFString(plugin.timeToBan-timeDiff(playerId)));
					player.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickMessage));
				}
			}, 20L);
			plugin.logDebug("Tried to keep player, " + player.getName() +", away from joining, as they are banned!");
			return;
		}
		unbanPlayer(playerId);
	}

	public void unbanPlayer(UUID playerId) {
		this.semihardcoreConfig.set(playerId.toString() + ".IsDead", false);

		saveTimed();
	}
	
	public void unbanPlayer(Player player, String bannedPlayer) {
		UUID unbanPlayerId = getUUID(bannedPlayer);

		if (unbanPlayerId == null) {
			if (player == null) {
	 			this.plugin.log(this.plugin.getDescription().getFullName() + ": Error unbanning player: Player doesn't exist in dead-players.yml!");
	 		}
	 		else {
	 			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.RED + "Error unbanning player: " + ChatColor.WHITE + "Player doesn't exist in dead-players.yml!");
	 		}
			return;
		}

		if (!isBanned(unbanPlayerId)) {
	 		if (player == null) {
	 			this.plugin.log(this.plugin.getDescription().getFullName() + ": Error unbanning player: Player is not banned!");
	 		}
	 		else {
	 			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.RED + "Error unbanning player: " + ChatColor.WHITE + "Player is not banned!");
	 		}
			return;
		}

		unbanPlayer(unbanPlayerId);

		if (player == null) {
			this.plugin.log(this.plugin.getDescription().getFullName() + ": Unbanned " + bannedPlayer + "!");
		}
		else {
			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Unbanned " + bannedPlayer + "!");
		}
	}

	public boolean isDead(UUID playerId) {
		return this.semihardcoreConfig.getBoolean(playerId.toString() + ".IsDead");
	}

	public String getDeathTime(UUID playerId) {
		return this.semihardcoreConfig.getString(playerId.toString() + ".LastDeath");
	}

	public String getTimeSinceDeathTime(UUID playerId) {
		return TimeConverter.parseMillisToUFString(timeDiff(playerId));
	}

	public String getDeaths(UUID playerId) {
		return this.semihardcoreConfig.getString(playerId.toString() + ".Deaths");
	}

	public boolean isBanned(UUID playerId) {
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

	private long timeDiff(UUID playerId) {
		Date deathTime = new Date();
		Date currentTime = new Date();

		String deathTimeStr = getDeathTime(playerId);

		try 
		{
			deathTime = new SimpleDateFormat(datePattern).parse(deathTimeStr);
		} 
		catch (ParseException e) 
		{
			plugin.log("Error while parsing datetime for player...: " + e.getMessage());
			return 0;
		}  

		long diff = Math.abs(currentTime.getTime() - deathTime.getTime());
	    //long diff = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);

	    plugin.logDebug("Diff in milliseconds: " + diff);

	    return diff;
	}

	public boolean banPlayer(Player player, UUID playerId)
	{
		if (isBanned(playerId)) {
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

		// Delay the kick, to not have the console make a "Removing entity while ticking!" Exception
		SemiHardcore.server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				String kickMessage = plugin.messageKickPlayerOnBan.replace("{banTime}",plugin.timeToBanStringUF);
				player.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickMessage));
			}
		}, 1L);

		this.semihardcoreConfig.set(playerId.toString() + ".Name", player.getName());

		String banMessage = plugin.messageBanPlayer.replace("{playerName}", player.getName()).replace("{timeBanned}", plugin.timeToBanStringUF);
		plugin.sendInfoAll(banMessage);

		saveTimed();

		return true;
	}

	public UUID getUUID(String playerName) {
		Map<String, Object> deadPlayers = this.semihardcoreConfig.getValues(true);
		UUID playerId = null;
		for (Map.Entry<String, Object> entry : deadPlayers.entrySet()) {
			if (entry.getValue() instanceof String) {
				this.plugin.logDebug("Entry key: " + entry.getKey());
				this.plugin.logDebug("Entry value: " + entry.getValue());
				if (entry.getKey().contains(".Name") && entry.getValue().toString().equalsIgnoreCase(playerName)) {
					playerId = UUID.fromString(entry.getKey().replace(".Name", ""));
					break;
				}
		    }
		}

		if (playerId == null) {
			this.plugin.logDebug("Error finding player: Player doesn't exist in dead-players.yml!");
		}

		return playerId;
	}
}
