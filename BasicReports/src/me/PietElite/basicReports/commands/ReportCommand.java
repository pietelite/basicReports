package me.PietElite.basicReports.commands;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.CommandHelpMap;
import me.PietElite.basicReports.utils.General;
import me.PietElite.basicReports.utils.data.Report;

public class ReportCommand implements CommandExecutor,TabCompleter {

	private BasicReports plugin;
	
	public static ReportCommand initialize(BasicReports plugin) {
		ReportCommand instance = new ReportCommand();
		instance.plugin = plugin;
		plugin.getCommand("report").setExecutor(instance);
		plugin.getCommand("report").setTabCompleter(instance);
		return instance;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> list = new LinkedList<String>();
		if (args.length == 1) {
			list = plugin.getFileManager().getReportTypes();
			list.add("help");
			return StringUtil.copyPartialMatches(args[0], list, new LinkedList<String>());
		}
		
		if (args.length == 2 && plugin.getFileManager().getReportTypes().contains(args[0]) && args[1].equals("")) {
			list.clear();
			list.add("<message>");
			return list;
		}
		
		return new LinkedList<String>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		if (plugin.getDatabaseManager().hasError()) {
			String disabledCommandMessage = "This command has been disabled because there is an error in your database.";
			if (sender instanceof Player) {
				((Player) sender).sendMessage(General.chat("&c" + disabledCommandMessage));
			} else {
				sender.sendMessage(disabledCommandMessage);
			}
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Non-players are not allowed to execute this command");
			return false;
		}
		
		Player player = (Player) sender;
		
		try {
			if (player.hasPermission("BasicReports.report")) {
				
				switch (args[0]) {
				case "?":
				case "help":
					CommandHelpMap reportHelpMap = new CommandHelpMap(plugin);
					reportHelpMap.put("help", "Displays the possible arguments for the report command");
					for (String type : plugin.getFileManager().getReportTypes()) {
						reportHelpMap.put(type + " <message>", "Leaves a report for staff");
					}
					player.sendMessage(General.chat(reportHelpMap.toChatString(), player.getName(), command.getName()));
					return true;
				default:
					if (args.length <= 1 || !plugin.getFileManager().getReportTypes().contains(args[0])) {
						General.sendInvalidArguments(plugin, player, command.getName());
						return false;
					} else {
						String reportMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
						plugin.getDatabaseManager().addReport(new Report(player, args[0], reportMessage, new Date(), 
								player.getLocation()));
						player.sendMessage(General.chat("&fYou reported a &d" + args[0] + "&f type report: &7" + reportMessage));
						return true;
					}
				}
			} else {
				General.sendNoPermission(plugin, player, command.getName());
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			General.sendInvalidArguments(plugin, player, command.getName());
			return false;
		}
	}

}
