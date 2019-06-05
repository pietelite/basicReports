package me.PietElite.basicReports.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.General;

public class ReportsCommand implements CommandExecutor, TabCompleter {

	private BasicReports plugin;
	private static final List<String> REPORTS_COMMANDS = Arrays.asList("set","list","reload","help");
	
	public static ReportsCommand initialize(BasicReports plugin) {
		ReportsCommand instance = new ReportsCommand();
		instance.plugin = plugin;
		plugin.getCommand("reports").setExecutor(instance);
		plugin.getCommand("reports").setTabCompleter(instance);
		return instance;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		if (args.length == 1) {
			return StringUtil.copyPartialMatches(args[0], REPORTS_COMMANDS, new LinkedList<String>());
		}
		
		return new LinkedList<String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Non-players are not allowed to execute this command");
			return false;
		}
		
		Player player = (Player) sender;
		
		try {
			if (player.hasPermission("reports")) {
				player.sendMessage("You executed the reports command.");
				switch (args[0]) {
				case "help":
					//give a list of commands
					return true;
				case "list":
					//give a list of all unchecked reports
					return true;
				case "listall":
					//give a list of all reports
					return true;
				case "set":
					//toggle certain states
					switch (args[1]) {
					case "help":
						//give a list of commands
						return true;
					case "devmode":
						switch (args[2]) {
						case "true":
							plugin.getFileManager().getConfigConfig().set("debug.dev_mode", true);
							plugin.getFileManager().reload();
							player.sendMessage(General.taggedChat("&aYou just enabled developer mode"));
							return true;
						case "false":
							plugin.getFileManager().getConfigConfig().set("debug.dev_mode", false);
							plugin.getFileManager().reload();
							player.sendMessage(General.taggedChat("&aYou just disabled developer mode"));
							return true;
						default:
							General.sendInvalidBoolean(plugin, player, "reports");
							return true;
						}
					default:
						return false;
					}
				default:
					return false;
				}
			} else {
				General.sendNoPermission(plugin, player, "reports");
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			General.sendInvalidArguments(plugin, player, "reports");
			return false;
		}
	}
}
