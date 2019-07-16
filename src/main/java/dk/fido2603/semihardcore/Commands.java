package dk.fido2603.semihardcore;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements Listener
{
	private SemiHardcore	plugin;

	Commands(SemiHardcore p)
	{
		this.plugin = p;
	}

	public void commandReload(Player player)
	{
		if (player == null)
		{
			this.plugin.reloadSettings();
			this.plugin.log("Reloaded configuration.");
		}
		else if (player.isOp() || (SemiHardcore.getPermissionsManager().hasPermission(player, "semihardcore.reload")))
		{
			this.plugin.reloadSettings();
			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
		}
	}

	public void commandSave(Player player)
	{
		if (player == null)
		{
			SemiHardcore.getPlayerManager().save();
			this.plugin.log("Saved configuration(s).");
		}
		else if (player.isOp() || (SemiHardcore.getPermissionsManager().hasPermission(player, "semihardcore.save")))
		{
			SemiHardcore.getPlayerManager().save();
			player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Saved configuration(s).");
		}
	}
	
	public void commandUnban(Player player, String[] args) {
		if (player == null || player.isOp() || SemiHardcore.getPermissionsManager().hasPermission(player, "semihardcore.save")) {
			String unbanPlayer = args[1];
			SemiHardcore.getPlayerManager().unbanPlayer(player, unbanPlayer);
		}
	}
	
	public void commandInfo(Player player, String[] args) {
		if (player == null || player.isOp() || SemiHardcore.getPermissionsManager().hasPermission(player, "semihardcore.info")) {
			String playerName = args[1];
			UUID playerUUID = SemiHardcore.getPlayerManager().getUUID(playerName);

			if (player == null) {
				this.plugin.log("---------------- " + plugin.getDescription().getFullName() + " ----------------");
				this.plugin.log("Playerinfo for " + playerName);
				this.plugin.log("");

				if (playerUUID == null) {
					this.plugin.log("The given player could not be found in the list of dead players!");
					return;
				}

				this.plugin.log("Banned: " + SemiHardcore.getPlayerManager().isBanned(playerUUID));
				this.plugin.log("Deaths: " + SemiHardcore.getPlayerManager().getDeaths(playerUUID));
				this.plugin.log("Time of last death: " + SemiHardcore.getPlayerManager().getDeathTime(playerUUID));
				this.plugin.log("Time since last death: " + SemiHardcore.getPlayerManager().getTimeSinceDeathTime(playerUUID));

				return;
			}

			player.sendMessage(ChatColor.YELLOW + "---------------- " + plugin.getDescription().getFullName() + " ----------------");
			player.sendMessage(ChatColor.AQUA + "Playerinfo for " + ChatColor.WHITE + playerName);
			player.sendMessage(ChatColor.AQUA + "");

			if (playerUUID == null) {
				player.sendMessage(ChatColor.RED + "The given player could not be found in the list of dead players!");
				return;
			}

			player.sendMessage(ChatColor.AQUA + "Banned: " + ChatColor.WHITE + SemiHardcore.getPlayerManager().isBanned(playerUUID));
			player.sendMessage(ChatColor.AQUA + "Deaths: " + ChatColor.WHITE + SemiHardcore.getPlayerManager().getDeaths(playerUUID));
			player.sendMessage(ChatColor.AQUA + "Time of last death: " + ChatColor.WHITE + SemiHardcore.getPlayerManager().getDeathTime(playerUUID));
			player.sendMessage(ChatColor.AQUA + "Time since last death: " + ChatColor.WHITE + SemiHardcore.getPlayerManager().getTimeSinceDeathTime(playerUUID));

			return;
		}
	}

	private boolean commandHelp(Player player)
	{
		if (player == null) {
			this.plugin.log("---------------- " + plugin.getDescription().getFullName() + " ----------------");
			this.plugin.log("Made by Fido2603");
			this.plugin.log("");
			if (plugin.isUHCDay) {
				this.plugin.log("It is currently UHC Day! No natural regeneration!");
				this.plugin.log("");
			}
			this.plugin.log("Use /semihardcore help, to get a list of available commands.");

			return true;
		}
		player.sendMessage(ChatColor.YELLOW + "---------------- " + plugin.getDescription().getFullName() + " ----------------");
		player.sendMessage(ChatColor.AQUA + "Made by Fido2603");
		player.sendMessage(ChatColor.AQUA + "");
		if (plugin.isUHCDay) {
			player.sendMessage(ChatColor.AQUA + "It is currently UHC Day! No natural regeneration!");
			player.sendMessage(ChatColor.AQUA + "");
		}
		player.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/semihardcore help" + ChatColor.AQUA + ", to get a list of available commands.");

		return true;
	}

	private boolean commandList(Player player)
	{
		if (player == null) {
			this.plugin.log("---------------- " + this.plugin.getDescription().getFullName() + " ----------------");
			this.plugin.log("/semihardcore - Info about the plugin");
			this.plugin.log("/semihardcore help - Shows this message");
			this.plugin.log("/semihardcore unban <player> - Unbans a player from the server, if the cause of the ban is death");
			this.plugin.log("/semihardcore info <player> - Gets info about a player");
			this.plugin.log("/semihardcore reload - Reloads configs from disk");
			this.plugin.log("/semihardcore save - Saves modified configs to disk");

			return true;
		}

		player.sendMessage(ChatColor.YELLOW + "---------------- " + this.plugin.getDescription().getFullName() + " ----------------");
		player.sendMessage(ChatColor.AQUA + "/semihardcore" + ChatColor.WHITE + " - Info about the plugin");
		if ((player.isOp()) || (player.hasPermission("semihardcore.help")))
		{
			player.sendMessage(ChatColor.AQUA + "/semihardcore help" + ChatColor.WHITE + " - Shows this message");
		}
		if ((player.isOp()) || (player.hasPermission("semihardcore.unban")))
		{
			player.sendMessage(ChatColor.AQUA + "/semihardcore unban <player>" + ChatColor.WHITE + " - Unbans a player from the server, if the cause of the ban is death");
		}
		if ((player.isOp()) || (player.hasPermission("semihardcore.info"))) {
			player.sendMessage(ChatColor.AQUA + "/semihardcore info <player>" + ChatColor.WHITE + " - Gets info about a player");
		}
		if ((player.isOp()) || (player.hasPermission("semihardcore.reload")))
		{
			player.sendMessage(ChatColor.AQUA + "/semihardcore reload" + ChatColor.WHITE + " - Reloads configs from disk");
		}
		if ((player.isOp()) || (player.hasPermission("semihardcore.save")))
		{
			player.sendMessage(ChatColor.AQUA + "/semihardcore save" + ChatColor.WHITE + " - Saves modified configs to disk");
		}

		return true;
	}

	// What happens when a command is run?
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		if (sender instanceof Player)
		{
			player = (Player) sender;
		}

		if ((label.equalsIgnoreCase("semihardcore")) || (label.equalsIgnoreCase("sh")))
		{
			if (args.length == 0) {
				commandHelp(player);
				return true;
			}
			else if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("reload"))
				{
					commandReload(player);
				}
				else if (args[0].equalsIgnoreCase("save"))
				{
					commandSave(player);
				}
				else if (args[0].equalsIgnoreCase("help")) {
					commandList(player);
				}
				return true;
			}
			else if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("unban"))
				{
					commandUnban(player, args);
				}
				if (args[0].equalsIgnoreCase("info"))
				{
					commandInfo(player, args);
				}
				return true;
			}
			return false;
		}
		return true;
	}

}