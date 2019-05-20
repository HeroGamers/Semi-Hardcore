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
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("reload"))
				{
					commandReload(player);
				}
				else if (args[0].equalsIgnoreCase("save"))
				{
					commandSave(player);
				}
				else
				{
					//well
				}
				return true;
			}
			return false;
		}
		return true;
	}

}