package me.PietElite.basicReports.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
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

public class ReportsCommand implements CommandExecutor, TabCompleter {

	private BasicReports plugin;
		
	public static ReportsCommand initialize(BasicReports plugin) {
		ReportsCommand instance = new ReportsCommand();
		instance.plugin = plugin;
		plugin.getCommand("reports").setExecutor(instance);
		plugin.getCommand("reports").setTabCompleter(instance);
		return instance;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		
		Player player = (Player) sender;
		
		List<String> list = new LinkedList<String>();
		
		try {
			if (player.hasPermission("BasicReports.reports")) {
				if (args.length == 1) {
					list.clear();
					list.add("help");
					if (player.hasPermission("BasicReports.reports.about")) {
						list.add("about");
					}
					if (player.hasPermission("BasicReports.reports.info")) {
						list.add("info");
					}
					if (player.hasPermission("BasicReports.reports.list")) {
						list.add("list");
					}
					if (player.hasPermission("BasicReports.reports.reload")) {
						list.add("reload");
					}
					if (player.hasPermission("BasicReports.reports.resolve")) {
						list.add("resolve");
					}
					if (player.hasPermission("BasicReports.reports.unresolve")) {
						list.add("unresolve");
					}
					if (player.hasPermission("BasicReports.reports.clear")) {
						list.add("clear");
					}
					if (player.hasPermission("BasicReports.reports.set")) {
						list.add("set");
					}
					return StringUtil.copyPartialMatches(args[0], list, new LinkedList<String>());
				}
				
				switch (args[0]) {
				case "?":
				case "help":
					list.clear();
					return list;
				case "about":
					if (player.hasPermission("BasicReports.reports.about")) {
						list.clear();
						return list;
					}
				case "info":
					if (player.hasPermission("BasicReports.reports.resolve")) {
						if (args.length == 2 && args[1].equals("")) {
							list.clear();
							list.add("<id>");
							return list;
						}
					}
					list.clear();
					return list;
				case "list":
					if (player.hasPermission("BasicReports.reports.clear")) {
						if (args.length == 2) {
							list.clear();
							list.add("help");
							list.add("all");
							list.add("type");
							list.add("player");
							list.add("resolved");
							list.add("unresolved");
							return StringUtil.copyPartialMatches(args[1], list, new LinkedList<String>());
						}
						
						switch (args[1]) {
						case "help":
							list.clear();
							return list;
						case "all":
							list.clear();
							return list;
						case "type":
							if (args.length == 3) {
								list = plugin.getFileManager().getReportTypes();
								return StringUtil.copyPartialMatches(args[2], list, new LinkedList<String>());
							}
							list.clear();
							return list;
						case "user":
						case "player":
							if (args.length == 3) {
								return null;
							}
							list.clear();
							return list;
						case "resolved":
							list.clear();
							return list;
						case "unresolved":
							list.clear();
							return list;
						default:
							list.clear();
							return list;
						}
					}
					list.clear();
					return list;
				case "reload":
					if (player.hasPermission("BasicReports.reports.reload")) {
						list.clear();	
						return list;
					}
					list.clear();
					return list;
				case "resolve":
					if (player.hasPermission("BasicReports.reports.resolve")) {
						if (args.length == 2 && args[1].equals("")) {
							list.clear();
							list.add("<id>");
							return list;
						}
					}
					list.clear();
					return list;
				case "unresolve":
					if (player.hasPermission("BasicReports.reports.unresolve")) {
						if (args.length == 2 && args[1].equals("")) {
							list.clear();
							list.add("<id>");
							return list;
						}
					}
					list.clear();
					return list;
				case "clear": //all, type [type] , player [player], resolved
					if (player.hasPermission("BasicReports.reports.clear")) {
						if (args.length == 2) {
							list.clear();
							list.add("all");
							list.add("type");
							list.add("player");
							list.add("resolved");
							return StringUtil.copyPartialMatches(args[1], list, new LinkedList<String>());
						}
						
						switch (args[1]) {
						case "all":
							list.clear();
							return list;
						case "type":
							if (args.length == 3) {
								list = plugin.getFileManager().getReportTypes();
								return StringUtil.copyPartialMatches(args[2], list, new LinkedList<String>());
							}
							list.clear();
							return list;
						case "user":
						case "player":
							if (args.length == 3) {
								return null;
							}
							list.clear();
							return list;
						case "resolved":
							list.clear();
							return list;
						default:
							list.clear();
							return list;
						}
					}
					list.clear();
					return list;
				case "set":
					if (player.hasPermission("BasicReports.reports.set")) {
						if (args.length == 2) {
							list.clear();
							list.add("help");
							if (player.hasPermission("BasicReports.reports.set.devmode")) {
								list.add("devmode");
							}
							return StringUtil.copyPartialMatches(args[1], list, new LinkedList<String>());
						}
						
						switch (args[1]) {
						case "help":
							list.clear();
							return list;
						case "devmode":
							if (player.hasPermission("BasicReports.reports.set.devmode")) {
								if (args.length == 3) {
									list.clear();
									list.add("true");
									list.add("false");
									return StringUtil.copyPartialMatches(args[1], list, new LinkedList<String>());
								}
							}
							list.clear();
							return list;
						default:
							list.clear();
							return list;
						}
					}
				default:
					list.clear();
					return list;
				}
			} else {
				list.clear();
				return list;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			list.clear();
			return list;
		}
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
			if (player.hasPermission("BasicReports.reports")) {
				switch (args[0]) {
				case "?":
				case "help":
					CommandHelpMap reportsHelpMap = new CommandHelpMap(plugin);
					reportsHelpMap.put("help", "Displays the possible arguments for the reports command");
					
					reportsHelpMap.put("about", "Displays about information the BasicReports");
					
					if (player.hasPermission("BasicReports.reports.info")) {
						reportsHelpMap.put("info", "Checks all the information about a report, given its id");
					}
					if (player.hasPermission("BasicReports.reports.list")) {
						reportsHelpMap.put("list <filter>", "List reports under a given filter.");
					}
					if (player.hasPermission("BasicReports.reports.reload")) {
						reportsHelpMap.put("reload", "Reloads BasicReports files");
					}
					if (player.hasPermission("BasicReports.reports.resolve")) {
						reportsHelpMap.put("resolve <id>", "Marks the report as resolved, given its id");
					}
					if (player.hasPermission("BasicReports.reports.unresolve")) {
						reportsHelpMap.put("unresolve <id>", "Marks the report as not resolved, given its id");
					}
					if (player.hasPermission("BasicReports.reports.clear")) {
						reportsHelpMap.put("clear <filter>", "Deletes reports. Clearing all will delete the database and create a new one.");
					}
					if (player.hasPermission("BasicReports.reports.set")) {
						reportsHelpMap.put("set <args>", "Sets a condition. Try /reports set help");
					}
					
					player.sendMessage(General.chat(reportsHelpMap.toChatString(), player.getName(), command.getName()));
					return true;
				case "about":
					// Anyone with reports can use "about"
					InputStream inputStream = plugin.getResource("about.txt");
					InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
					BufferedReader reader = new BufferedReader(streamReader);
					try {
						for (String line; (line = reader.readLine()) != null;) {
						    player.sendMessage(General.chat(line
						    		.replaceAll("_VERSION", plugin.getDescription().getVersion().toString())));
						}
					    return true;
					} catch (IOException e) {
						plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Error while reading the about.txt file");
						e.printStackTrace();
						return false;
					}
				case "info":
					if (player.hasPermission("BasicReports.reports.info")) {
						HashMap<Integer,Report> reports = plugin.getDatabaseManager().getData();
						try {
							int id = Integer.valueOf(args[1]);
							if (reports.containsKey(id)) {
								Report report = reports.get(id);
								SimpleDateFormat dateFormat = plugin.getFileManager().getDateFormat();
								dateFormat.setTimeZone(plugin.getFileManager().getTimeZone());
								player.sendMessage(General.chat("&7-------" +
										"\n&3ID: &f" + report.getId() +
										"\n&3Date: &f" + dateFormat.format(report.getDate()) +
										"\n&3Resolved: &f" + report.isResolved() +
										"\n&3Player: &f" + report.getPlayer().getName() +
										"\n&3Type: &f" + report.getType() +
										"\n&3Message: &f" + report.getMessage() +
										"\n&3Location: &f" + report.getBlockLocation() +
										"\n&3World: &f" + report.getLocation().getWorld().getName()));
								return true;
							}
							player.sendMessage(General.chat("&cNo valid report was found with id &6" + args[1] + "&c."));
							return false;
						} catch (NumberFormatException e) {
							player.sendMessage(General.chat("&6" + args[1] + " &cmust be an integer."));
							return false;
						} catch (NullPointerException e) {
							player.sendMessage(General.chat("&cItems on record can not be identified for report with id &6" + args[1] + "&c."));
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}
				case "list":
					if (player.hasPermission("BasicReports.reports.list")) {
						
						Collection<Report> reports = plugin.getDatabaseManager().getData().values();
						
						boolean matchingReportFound = false;
						
						switch (args[1]) {
						case "help":
							CommandHelpMap reportsListHelpMap = new CommandHelpMap(plugin);
							
							reportsListHelpMap.put("help", "Displays the possible arguments for the '/reports list' command");
							reportsListHelpMap.put("all", "Displays all reports");
							reportsListHelpMap.put("player <username>", "Displays all reports left by the designated player");
							reportsListHelpMap.put("type <type>", "Displays all reports of the designated type");
							reportsListHelpMap.put("resolved", "Displays all resolved reports");
							reportsListHelpMap.put("unresolved", "Displays all unresolved reports");
							
							player.sendMessage(General.chat(reportsListHelpMap.toChatString(), player.getName(), command.getName()));
							return true;
						case "all":
							
							if (reports.isEmpty()) {
								player.sendMessage(General.chat("&eThere are no reports at this time."));
								return true;
							}
							
							player.sendMessage(General.chat("&8-- &dReport List &8- &4All &8--"));
							for (Report report : reports) {
								player.sendMessage(General.chat(Report.printReport(plugin, report)));
							}
							return true;
						case "type":
							
							if (reports.isEmpty()) {
								player.sendMessage(General.chat("&eThere are no reports at this time."));
								return true;
							}
							
							if (!plugin.getFileManager().getReportTypes().contains(args[2])) {
								General.sendInvalidArguments(plugin, player, command.getName());
								return false;
							}
							player.sendMessage(General.chat("&8-- &dReport List &8- &4" + args[2] + "&8--"));
							
							for (Report report : reports) {
								if (report.getType().equals(args[2])) {
									player.sendMessage(General.chat(Report.printReport(plugin, report)));
									matchingReportFound = true;
								}
							}
							
							if (!matchingReportFound) {
								player.sendMessage(General.chat("eNo reports were found with this filter."));
							}
							
							return true;
						case "user":
						case "player":
							
							if (reports.isEmpty()) {
								player.sendMessage(General.chat("&eThere are no reports at this time."));
								return true;
							}
							
							player.sendMessage(General.chat("&8-- &dReport List &8- &4" + args[2] + "&8--"));
							for (Report report : reports) {
								if (report.getPlayer().getName().equals(args[2])) {
									player.sendMessage(General.chat(Report.printReport(plugin, report)));
									matchingReportFound = true;
								}
							}
							
							if (!matchingReportFound) {
								player.sendMessage(General.chat("eNo reports were found with this filter."));
							}
							
							return true;
						case "resolved":
							
							if (reports.isEmpty()) {
								player.sendMessage(General.chat("&eThere are no reports at this time."));
								return true;
							}
							
							player.sendMessage(General.chat("&8-- &dReport List &8- &4Resolved &8--"));
							for (Report report : reports) {
								if (report.isResolved()) {
									player.sendMessage(General.chat(Report.printReport(plugin, report)));
									matchingReportFound = true;
								}
							}
							
							if (!matchingReportFound) {
								player.sendMessage(General.chat("eNo reports were found with this filter."));
							}
							
							return true;
						case "unresolved":
							
							if (reports.isEmpty()) {
								player.sendMessage(General.chat("&eThere are no reports at this time."));
								return true;
							}
							
							player.sendMessage(General.chat("&8-- &dReport List &8- &4Unresolved &8--"));
							for (Report report : reports) {
								if (!report.isResolved()) {
									player.sendMessage(General.chat(Report.printReport(plugin, report)));
									matchingReportFound = true;
								}
							}
							
							if (!matchingReportFound) {
								player.sendMessage(General.chat("eNo reports were found with this filter."));
							}
							
							return true;
						default:
							General.sendInvalidArguments(plugin, player, command.getName());
							return false;
						}
						
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}

				case "reload":
					player.sendMessage(General.chat("&eThe reload command is currently under development.\n"
							+ "Please restart your server to fully reload BasicReports."));
					return true;
					/*
					if (player.hasPermission("BasicReports.reports.reload")) {
						plugin.reloadFiles();
						player.sendMessage(General.chat(General.PLUGIN_TAG + "&aFiles reloaded."));
						plugin.initializeDatabaseManager();
						player.sendMessage(General.chat(General.PLUGIN_TAG + "&aDatabase reloaded."));
						return true;
					} else {
						General.sendNoPermission(plugin, player, "reports");
						return false;
					}
					*/
				case "resolve":
					if (player.hasPermission("BasicReports.reports.resolve")) {
						HashMap<Integer, Report> reports = plugin.getDatabaseManager().getData();
						try {
							int id = Integer.valueOf(args[1]);
							if (!reports.containsKey(id)) {
								player.sendMessage(General.chat("&cNo valid report was found with id &6" + args[1] + "&c."));
								return false;
							}
							if (plugin.getDatabaseManager().setResolved(id, true)) {
								player.sendMessage(General.chat("&aReport id &6" + id + " &ahas been resolved."));
								return true;
							} else {
								player.sendMessage(General.chat("&cThat report has already been resolved!"));
								return false;
							}
						} catch (NumberFormatException e) {
							player.sendMessage(General.chat("&6" + args[1] + " &cmust be an integer."));
							e.printStackTrace();
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}
				case "unresolve":
					if (player.hasPermission("BasicReports.reports.unresolve")) {
						HashMap<Integer, Report> reports = plugin.getDatabaseManager().getData();
						try {
							int id = Integer.valueOf(args[1]);
							if (!reports.containsKey(id)) {
								player.sendMessage(General.chat("&cNo valid report was found with id &6" + args[1] + "&c."));
								return false;
							}
							Report report = reports.get(id);
							if (report.isResolved()) {
								plugin.getDatabaseManager().setResolved(id, true);
								player.sendMessage(General.chat("&aReport id &6" + id + " &ahas been unresolved."));
								return true;
							} else {
								player.sendMessage(General.chat("&cThat report is already unresolved!"));
								return false;
							}
						} catch (NumberFormatException e) {
							player.sendMessage(General.chat("&6" + args[1] + " &cmust be an integer."));
							e.printStackTrace();
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return true;
					}
				case "clear":
					if (player.hasPermission("BasicReports.reports.clear")) {
						int removedReports;
						switch (args[1]) {
						case "all":
							if (args.length == 2) {
								player.sendMessage(General.chat("&eAre you sure you want to clear the report database? If yes, execute "
										+ "&e'&a/reports clear all --confirm&e' or '&a/reports clear all -c&e'."));
								return true;
							}
							switch (args[2]) {
							case "-c":
							case "--confirm":
								plugin.getDatabaseManager().clearDatabase();
								player.sendMessage(General.chat("&aThe database has been wiped clean."));
								return true;
							default:
								player.sendMessage(General.chat("&cInvalid confirmation code."));
								return false;
							}
						case "type":
							if (plugin.getFileManager().getReportTypes().contains(args[2])) {
								removedReports = plugin.getDatabaseManager().clearReports(new Report.ReportType(args[2]));
								player.sendMessage(General.chat("&a(&6" + removedReports + "&a) reports were removed with type &6" + args[2] + "&a."));
								return true;
							} else {
								General.sendInvalidArguments(plugin, player, command.getName());
								return false;
							}
						case "user":
						case "player":
							String playerId = Bukkit.getPlayer(args[2]).getUniqueId().toString();
							if (playerId == null) {
								player.sendMessage(General.chat("&cNo player was found by that username."));
								return false;
							} else {
								removedReports = plugin.getDatabaseManager().clearReports(player);
								player.sendMessage(General.chat("&a(&6" + removedReports + "&a) reports were removed from player &6" + args[2] + "&a."));
								return true;
							}
						case "resolved":
							removedReports = plugin.getDatabaseManager().clearReports(true);
							player.sendMessage(General.chat("&a(&6" + removedReports + "&a) reports resolved reports were removed."));
							return true;
						default:
							General.sendInvalidArguments(plugin, player, command.getName());
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}
				case "set":
					if (player.hasPermission("BasicReports.reports.set")) {
						switch (args[1]) {
						case "help":
							CommandHelpMap reportsSetHelpMap = new CommandHelpMap(plugin);
							reportsSetHelpMap.put("help", "Displays the possible arguments for the reports <set> command");
							
							if (player.hasPermission("BasicReports.reports.set.devmode")) {
								reportsSetHelpMap.put("set devmode <true/false>", "Enables or disables devmode");
							}
							
							player.sendMessage(General.chat(reportsSetHelpMap.toChatString(), player.getName(), command.getName()));
							return true;
						case "devmode":
							if (player.hasPermission("BasicReports.reports.set.devmode")) {
								switch (args[2]) {
								case "true":
									plugin.getFileManager().getConfigConfig().set("debug.dev_mode", true);
									player.sendMessage(General.taggedChat("&aYou just enabled developer mode"));
									return true;
								case "false":
									plugin.getFileManager().getConfigConfig().set("debug.dev_mode", false);
									player.sendMessage(General.taggedChat("&aYou just disabled developer mode"));
									return true;
								default:
									General.sendNoPermission(plugin, player, command.getName());
									return false;
								}
							} else {
								General.sendNoPermission(plugin, player, command.getName());
								return false;
							}
						default:
							General.sendInvalidArguments(plugin, player, command.getName());
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}
				default:
					General.sendInvalidArguments(plugin, player, command.getName());
					return false;
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
