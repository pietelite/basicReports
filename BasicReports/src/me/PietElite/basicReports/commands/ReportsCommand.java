package me.PietElite.basicReports.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
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
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.util.StringUtil;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.CommandHelpMap;
import me.PietElite.basicReports.utils.General;

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
					if (player.hasPermission("BasicReports.reports.list")) {
						list.add("list");
					}
					if (player.hasPermission("BasicReports.reports.listall")) {
						list.add("listall");
					}
					if (player.hasPermission("BasicReports.reports.reload")) {
						list.add("reload");
					}
					if (player.hasPermission("BasicReports.reports.strike")) {
						list.add("strike");
					}
					if (player.hasPermission("BasicReports.reports.unstrike")) {
						list.add("unstrike");
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
				case "list":
					if (player.hasPermission("BasicReports.reports.list")) {
						list.clear();
						return list;
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
				case "strike":
					if (player.hasPermission("BasicReports.rpoerts.strike")) {
						if (args.length == 2) {
							list.clear();
							list.add("<id>");
							return StringUtil.copyPartialMatches(args[0], list, new LinkedList<String>());
						}
					}
					list.clear();
					return list;
				case "unstrike":
					if (player.hasPermission("BasicReports.reports.unstrike")) {
						if (args.length == 2) {
							list.clear();
							list.add("<id>");
							return StringUtil.copyPartialMatches(args[0], list, new LinkedList<String>());
						}
					}
					list.clear();
					return list;
				case "clear":
					if (player.hasPermission("BasicReports.reports.clear")) {
						list.clear();
						return list;
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
					
					if (player.hasPermission("BasicReports.reports.about")) {
						reportsHelpMap.put("about", "Learn more about the BasicReports");
					}
					if (player.hasPermission("BasicReports.reports.list")) {
						reportsHelpMap.put("list", "List all unchecked reports");
					}
					if (player.hasPermission("BasicReports.reports.listall")) {
						reportsHelpMap.put("listall", "List all reports");
					}
					if (player.hasPermission("BasicReports.reports.reload")) {
						reportsHelpMap.put("reload", "Reloads BasicReports files");
					}
					if (player.hasPermission("BasicReports.reports.strike")) {
						reportsHelpMap.put("strike <id>", "Mark the report as checked given its id");
					}
					if (player.hasPermission("BasicReports.reports.unstrike")) {
						reportsHelpMap.put("unstrike <id>", "Mark the report as not checked given its id");
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
					if (player.hasPermission("BasicReports.reports.about")) {
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
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return false;
					}
				case "list":
					if (player.hasPermission("BasicReports.reports.list")) {
						ResultSet reports = plugin.getDatabaseManager().getTable();
						player.sendMessage(General.chat("&8-- &dReport List &8--"));
						try {
							while (reports.next()) {
								if (reports.getInt("has_checked") == 0) {
									SimpleDateFormat dateFormat = new SimpleDateFormat(plugin.getFileManager()
											.getMessagesConfig().getString("date_format"));
									dateFormat.setTimeZone(TimeZone.getTimeZone(plugin.getFileManager().getMessagesConfig().getString("timezone")));
									String[] blockLocation = reports.getString("location").split(",");
									Location location = new Location(Bukkit.getWorld(reports.getString("location_world")), 
											Double.parseDouble(blockLocation[0]), 
											Double.parseDouble(blockLocation[1]),
											Double.parseDouble(blockLocation[2]));
									
									player.sendMessage(General.chat(plugin.getFileManager().getMessagesConfig().getString("reports_list_message")
											.replaceAll("_ID", String.valueOf(reports.getInt("id")))
											.replaceAll("_REPORTTYPE",reports.getString("report_type"))
											.replaceAll("_USERNAME", Bukkit.getPlayer(UUID.fromString(reports.getString("player_uuid"))).getName())
											.replaceAll("_MESSAGE", reports.getString("message"))
											.replaceAll("_DATE", dateFormat.format(new Date(reports.getLong("date"))))
											.replaceAll("_LOCATION", String.join(",", Arrays.asList(
													String.valueOf(location.getBlockX()),
													String.valueOf(location.getBlockY()),
													String.valueOf(location.getBlockZ()))))
											.replaceAll("_LOCATIONWORLD", location.getWorld().getName())));
									}
								}
								return true;
							} catch (SQLException e) {
								plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Listing reports did not work correctly!");
								e.printStackTrace();
								return false;
							}
					} else {
						General.sendNoPermission(plugin, player, "reports");
						return false;
					}
				case "listall":
					if (player.hasPermission("BasicReports.reports.listall")) {
						ResultSet reports = plugin.getDatabaseManager().getTable();
						player.sendMessage(General.chat("&8-- &dReport List &8- &4ALL &8--"));
						try {
							while (reports.next()) {
								SimpleDateFormat dateFormat = new SimpleDateFormat(plugin.getFileManager()
										.getMessagesConfig().getString("date_format"));
								dateFormat.setTimeZone(TimeZone.getTimeZone(plugin.getFileManager().getMessagesConfig().getString("timezone")));
								String[] blockLocation = reports.getString("location").split(",");
								Location location = new Location(Bukkit.getWorld(reports.getString("location_world")), 
										Double.parseDouble(blockLocation[0]), 
										Double.parseDouble(blockLocation[1]),
										Double.parseDouble(blockLocation[2]));
								
								player.sendMessage(General.chat(plugin.getFileManager().getMessagesConfig().getString("reports_list_message")
										.replaceAll("_ID", String.valueOf(reports.getInt("id")))
										.replaceAll("_REPORTTYPE",reports.getString("report_type"))
										.replaceAll("_USERNAME", Bukkit.getPlayer(UUID.fromString(reports.getString("player_uuid"))).getName())
										.replaceAll("_MESSAGE", reports.getString("message"))
										.replaceAll("_DATE", dateFormat.format(new Date(reports.getLong("date"))))
										.replaceAll("_LOCATION", String.join(",", Arrays.asList(
												String.valueOf(location.getBlockX()),
												String.valueOf(location.getBlockY()),
												String.valueOf(location.getBlockZ()))))
										.replaceAll("_LOCATIONWORLD", location.getWorld().getName())));
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
						player.sendMessage(General.chat(General.PLUGIN_TAG + " &aFiles reloaded."));
						return true;
					} else {
						General.sendNoPermission(plugin, player, "reports");
						return false;
					}
				case "strike":
					if (player.hasPermission("BasicReports.reports.strike")) {
						ResultSet reports = plugin.getDatabaseManager().getTable();
						try {
							boolean reportFound = false;
							boolean reportNeedsUpdate = false;
							boolean isReportLeftUnchecked = false;
							while (reports.next()) {
								// Finding the correct report and striking it
								reportNeedsUpdate = reports.getInt("has_checked") == 0;
								if (Integer.parseInt(args[1]) == reports.getInt("id")) {
									reportFound = true;
									if (reportNeedsUpdate) {
										plugin.getDatabaseManager().setChecked(reports.getInt("id"), 1);
										player.sendMessage(General.chat("&aReport id: &6" + reports.getInt("id") + " &ahas been struck."));
									} else {
										player.sendMessage(General.chat("&cThat report has already been struck!"));
									}
									continue;
								}
								
								// Checking whether this was the last one to strike
								if (reportNeedsUpdate) {
									isReportLeftUnchecked = true;
								}
							}
							if (!reportFound) {
								player.sendMessage(General.chat("&cNo valid report was found with id &6" + args[1] + "&c."));
								return false;
							}
							if (!isReportLeftUnchecked) {
								player.sendMessage(General.chat("&eThere are no more unstruck reports. \n"
										+ "You can do '/reports clear' to clear the database."));
							}
							return true;
						} catch (SQLException e) {
							plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Striking a report did not work correctly");
							e.printStackTrace();
							return false;
						} catch (NumberFormatException e) {
							player.sendMessage(General.chat("&cThis must be an integer."));
							e.printStackTrace();
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, "reports");
						return false;
					}
				case "unstrike":
					if (player.hasPermission("BasicReports.reports.unstrike")) {
						ResultSet reports = plugin.getDatabaseManager().getTable();
						try {
							while (reports.next()) {
								if (Integer.parseInt(args[1]) == reports.getInt("id")) {
									if (reports.getInt("has_checked") == 1) {
										plugin.getDatabaseManager().setChecked(reports.getInt("id"), 0);
										player.sendMessage(General.chat("&aReport id: &6" + reports.getInt("id") + " &ahas been unstruck."));
										return true;
									} else {
										player.sendMessage(General.chat("&cThat report has not yet been struck!"));
										return false;
									}
								}
							}
							player.sendMessage(General.chat("&cNo valid report was foundwith id &6" + args[1] + "&c."));
							return false;
						} catch (SQLException e) {
							plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Unstriking a report did not work correctly");
							e.printStackTrace();
							return false;
						} catch (NumberFormatException e) {
							player.sendMessage(General.chat("&cThis must be an integer."));
							e.printStackTrace();
							return false;
						}
					} else {
						General.sendNoPermission(plugin, player, command.getName());
						return true;
					}
				case "clear":
					if (player.hasPermission("BasicReports.reports.clear")) {
						plugin.getDatabaseManager().clear();
						player.sendMessage(General.chat("&aThe database has been wiped clean."));
						return true;
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
