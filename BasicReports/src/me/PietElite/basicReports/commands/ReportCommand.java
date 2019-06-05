package me.PietElite.basicReports.commands;

import java.util.Arrays;
import java.util.Date;
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
import me.PietElite.basicReports.utils.CommandHelpMap;
import me.PietElite.basicReports.utils.General;
import me.PietElite.basicReports.utils.data.DatabaseManager;

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
		
		if (args.length == 1) {
			List<String> list = getReportTypes();
			list.add("help");
			return StringUtil.copyPartialMatches(args[0], list, new LinkedList<String>());
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
			if (player.hasPermission("report")) {
				
				switch (args[0]) {
				case "?":
				case "help":
					CommandHelpMap reportHelpMap = new CommandHelpMap(plugin);
					reportHelpMap.put("help", "Displays the possible arguments for the report command");
					for (String type : getReportTypes()) {
						reportHelpMap.put(type + " <message>", "Leaves a report for staff");
					}
					player.sendMessage(General.chat(reportHelpMap.toChatString(), player.getName(), "Report"));
					return true;
				default:
					if (args.length <= 1 || !getReportTypes().contains(args[0])) {
						General.sendInvalidArguments(plugin, player, "report");
						return false;
					} else {
						String reportMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
						plugin.getDatabaseManager().add(player.getUniqueId().toString(), args[0], reportMessage, new Date(), 
								player.getLocation().toVector().toString(), player.getLocation().getWorld().getName());
						player.sendMessage(General.chat("&fYou reported a &d" + args[0] + "&f type report: &7" + reportMessage));
						return true;
					}
				}
			} else {
				General.sendNoPermission(plugin, player, "report");
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			General.sendInvalidArguments(plugin, player, "report");
			return false;
		}
	}
	
	public List<String> getReportTypes() {
		List<?> reportTypes = plugin.getFileManager().getConfigConfig().getList("report_types");
		List<String> output = new LinkedList<String>();
		
		for (Object item : reportTypes) {
			
			if (item instanceof String) {
				output.add((String) item);
			}
		}
		return output;
	}

}
