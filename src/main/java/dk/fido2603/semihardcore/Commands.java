package dk.fido2603.semihardcore;

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
			this.plugin.log(this.plugin.getDescription().getFullName() + ": Reloaded configuration.");
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
			this.plugin.log(this.plugin.getDescription().getFullName() + ": Saved configuration(s).");
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
	
	private boolean commandHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "---------------- " + plugin.getDescription().getFullName() + " ----------------");
		sender.sendMessage(ChatColor.AQUA + "Made by Fido2603");
		sender.sendMessage(ChatColor.AQUA + "");
		sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/semihardcore help" + ChatColor.AQUA + ", to get a list of available commands.");

		return true;
	}

	private boolean commandList(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "---------------- " + this.plugin.getDescription().getFullName() + " ----------------");
		sender.sendMessage(ChatColor.AQUA + "/semihardcore" + ChatColor.WHITE + " - Info about the plugin");
		if ((sender.isOp()) || (sender.hasPermission("semihardcore.help")))
		{
			sender.sendMessage(ChatColor.AQUA + "/semihardcore help" + ChatColor.WHITE + " - Shows this message");
		}
		if ((sender.isOp()) || (sender.hasPermission("semihardcore.unban")))
		{
			sender.sendMessage(ChatColor.AQUA + "/semihardcore unban <player>" + ChatColor.WHITE + " - Unbans a player from the server, if the cause of the ban is death");
		}
		if ((sender.isOp()) || (sender.hasPermission("semihardcore.reload")))
		{
			sender.sendMessage(ChatColor.AQUA + "/semihardcore reload" + ChatColor.WHITE + " - Reloads configs from disk");
		}
		if ((sender.isOp()) || (sender.hasPermission("semihardcore.save")))
		{
			sender.sendMessage(ChatColor.AQUA + "/semihardcore save" + ChatColor.WHITE + " - Saves modified configs to disk");
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
				return true;
			}
			return false;
		}
		return true;
	}

}