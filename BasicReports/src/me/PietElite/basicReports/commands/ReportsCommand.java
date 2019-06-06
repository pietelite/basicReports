package me.PietElite.basicReports.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
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
	
	private ResultSet reports;
	
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
					if (player.hasPermission("BasicReports.reports.listall")) {
						list.add("listall");
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
					if (player.hasPermission("BasicReports.reports.list")) {
						if (args.length == 2) {
							list.clear();
							for (String reportType : plugin.getFileManager().getReportTypes()) {
								list.add(reportType);
							}
							return StringUtil.copyPartialMatches(args[1], list, new LinkedList<String>());
						}
					}
					list.clear();
					return list;
				case "listall":
					if (player.hasPermission("BasicReports.reports.listall")) {
						list.clear();
						return list;
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
								list.clear();
								return list;
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
						reportsHelpMap.put("list", "Lists all unchecked reports");
					}
					if (player.hasPermission("BasicReports.reports.listall")) {
						reportsHelpMap.put("listall", "Lists all reports");
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
						reportsHelpMap.put("clear", "Deletes the reports table and generates a new one");
					}
					if (player.hasPermission("BasicReports.reports.clear")) {
						reportsHelpMap.put("clear", "Deletes the reports table and generates a new one");
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
						ResultSet reports = (ResultSet) plugin.getDatabaseManager().getData();
						try {
							while (reports.next()) {
								// Finding the correct report and striking it
								if (Integer.parseInt(args[1]) == reports.getInt("id")) {
									SimpleDateFormat dateFormat = new SimpleDateFormat(plugin.getFileManager()
											.getMessagesConfig().getString("date_format"));
									dateFormat.setTimeZone(TimeZone.getTimeZone(plugin.getFileManager().getMessagesConfig().getString("timezone")));
									player.sendMessage(General.chat("&7-------" +
											"\n&3ID: &f" + args[1] +
											"\n&3Date: &f" + dateFormat.format(new Date(reports.getLong("date"))) +
											"\n&3Resolved: &f" + ((reports.getInt("has_checked") == 0) ? "false" : "true") +
											"\n&3Player: &f" + Bukkit.getPlayer(UUID.fromString(reports.getString("player_uuid"))).getName() +
											"\n&3Type: &f" + reports.getString("report_type") +
											"\n&3Message: &f" + reports.getString("message") +
											"\n&3Location: &f" + reports.getString("location") +
											"\n&3World: &f" + reports.getString("location_world")));
									return true;
								}
							}
							player.sendMessage(General.chat("&cNo valid report was found with id &6" + args[1] + "&c."));
							return false;
						} catch (SQLException e) {
							plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Resolving a report did not work correctly");
							e.printStackTrace();
							return false;
						} catch (NumberFormatException e) {
							player.sendMessage(General.chat("&6" + args[1] + " &cmust be an integer."));
							return false;
						} catch (NullPointerException e) {
							player.sendMessage(General.chat("&cThe player on record can not be identified."));
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}
				case "list":
					if (player.hasPermission("BasicReports.reports.list")) {
						if (args.length == 1) {
							reports = (ResultSet) plugin.getDatabaseManager().getData();
							player.sendMessage(General.chat("&8-- &dReport List &8- &4Unresolved &8--"));
							int rowCount = 0;
							try {
								while (reports.next()) {
									if (reports.getInt("has_checked") == 0) {
										rowCount++;
										printReport(player);
									}
								}
								if (rowCount == 0) {
									player.sendMessage(General.chat("&fThere are no reports at this time"));
								}
								return true;
							} catch (SQLException e) {
								plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Listing reports did not work correctly!");
								e.printStackTrace();
								return false;
							}
						}
						
						if (plugin.getFileManager().getReportTypes().contains(args[1])) {
							reports = (ResultSet) plugin.getDatabaseManager().getData();
							player.sendMessage(General.chat("&8-- &dReport List &8- &4" + args[1] + " &8--"));
							int rowCount = 0;
							try {
								while (reports.next()) {
									if (reports.getInt("has_checked") == 0 && reports.getString("report_type").equals(args[1])) {
										rowCount++;
										printReport(player);
									}
								}
								if (rowCount == 0) {
									player.sendMessage(General.chat("&fThere are no reports at this time"));
								}
								return true;
							} catch (SQLException e) {
								plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Listing reports did not work correctly!");
								e.printStackTrace();
								return false;
							}
						} else {
							General.sendInvalidArguments(plugin, player, command.getName());
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}
				case "listall":
					if (player.hasPermission("BasicReports.reports.listall")) {
						reports = (ResultSet) plugin.getDatabaseManager().getData();
						player.sendMessage(General.chat("&8-- &dReport List &8- &4ALL &8--"));
						int rowCount = 0;
						try {
							while (reports.next()) {
								rowCount++;
								printReport(player);
							}
							if (rowCount == 0) {
								player.sendMessage(General.chat("&fThere are no reports at this time"));
							}
							return true;
						} catch (SQLException e) {
							plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Listing all reports did not work correctly!");
							e.printStackTrace();
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, "reports");
						return false;
					}
				case "reload":
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
				case "resolve":
					if (player.hasPermission("BasicReports.reports.resolve")) {
						ResultSet reports = (ResultSet) plugin.getDatabaseManager().getData();
						try {
							boolean reportFound = false;
							boolean reportNeedsUpdate = false;
							boolean isReportLeftUnresolved = false;
							while (reports.next()) {
								// Finding the correct report and striking it
								reportNeedsUpdate = reports.getInt("has_checked") == 0;
								if (Integer.parseInt(args[1]) == reports.getInt("id")) {
									reportFound = true;
									if (reportNeedsUpdate) {
										plugin.getDatabaseManager().setResolved(reports.getInt("id"), true);
										player.sendMessage(General.chat("&aReport id: &6" + reports.getInt("id") + " &ahas been resolved."));
									} else {
										player.sendMessage(General.chat("&cThat report has already been resolved!"));
									}
									continue;
								}
								
								// Checking whether this was the last one to strike
								if (reportNeedsUpdate) {
									isReportLeftUnresolved = true;
								}
							}
							if (!reportFound) {
								player.sendMessage(General.chat("&cNo valid report was found with id &6" + args[1] + "&c."));
								return false;
							}
							if (!isReportLeftUnresolved) {
								player.sendMessage(General.chat("&eThere are no more unresolved reports."));
							}
							return true;
						} catch (SQLException e) {
							plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Resolving a report did not work correctly");
							e.printStackTrace();
							return false;
						} catch (NumberFormatException e) {
							player.sendMessage(General.chat("&6" + args[1] + " &cmust be an integer."));
							e.printStackTrace();
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, "reports");
						return false;
					}
				case "unresolve":
					if (player.hasPermission("BasicReports.reports.unresolve")) {
						ResultSet reports = (ResultSet) plugin.getDatabaseManager().getData();
						try {
							while (reports.next()) {
								if (Integer.parseInt(args[1]) == reports.getInt("id")) {
									if (reports.getInt("has_checked") == 1) {
										plugin.getDatabaseManager().setResolved(reports.getInt("id"), false);
										player.sendMessage(General.chat("&aReport id: &6" + reports.getInt("id") + " &ahas been unresolved."));
										return true;
									} else {
										player.sendMessage(General.chat("&cThat report has not yet been unresolved!"));
										return false;
									}
								}
							}
							player.sendMessage(General.chat("&cNo valid report was found with id &6" + args[1] + "&c."));
							return false;
						} catch (SQLException e) {
							plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "unresolving a report did not work correctly");
							e.printStackTrace();
							return false;
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
								removedReports = plugin.getDatabaseManager().clearReports("report_type = '" + args[2] + "'");
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
								removedReports = plugin.getDatabaseManager().clearReports("player_uuid = '" + playerId + "'");
								player.sendMessage(General.chat("&a(&6" + removedReports + "&a) reports were removed from player &6" + args[2] + "&a."));
								return true;
							}
						case "resolved":
							removedReports = plugin.getDatabaseManager().clearReports("has_checked = 1");
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
	
	public void printReport(Player player) throws SQLException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(plugin.getFileManager()
				.getMessagesConfig().getString("date_format"));
		dateFormat.setTimeZone(TimeZone.getTimeZone(plugin.getFileManager().getMessagesConfig().getString("timezone")));
		Report report = new Report(
				reports.getInt("id"),
				Bukkit.getPlayer(UUID.fromString(reports.getString("player_uuid"))),
				reports.getString("report_type"), 
				reports.getString("message"), 
				reports.getBoolean("has_checked"),
				new Date(reports.getLong("date")), 
				reports.getString("location"), 
				reports.getString("location_world"));
		
		String unformattedMessage;
		if (report.isResolved()) {
			unformattedMessage = plugin.getFileManager().getMessagesConfig().getString("reports_list.resolved");
		} else {
			unformattedMessage = plugin.getFileManager().getMessagesConfig().getString("reports_list.unresolved");
		}
		
		player.sendMessage(General.chat(unformattedMessage
				.replaceAll("_ID", String.valueOf(report.getId()))
				.replaceAll("_REPORTTYPE", report.getType())
				.replaceAll("_USERNAME", report.getPlayer().getName())
				.replaceAll("_MESSAGE", report.getMessage())
				.replaceAll("_DATE", dateFormat.format(report.getDate()))
				.replaceAll("_LOCATION", String.join(",", Arrays.asList(
						String.valueOf(report.getLocation().getBlockX()),
						String.valueOf(report.getLocation().getBlockY()),
						String.valueOf(report.getLocation().getBlockZ()))))
				.replaceAll("_LOCATIONWORLD", report.getLocation().getWorld().getName())));
	}
}
