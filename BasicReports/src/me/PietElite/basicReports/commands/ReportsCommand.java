package me.PietElite.basicReports.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.General;
import me.PietElite.basicReports.utils.data.FileManager;

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
		
		if (args.length == 1) {
			return StringUtil.copyPartialMatches(args[0], new LinkedList<String>(), new LinkedList<String>());
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
				switch (args[0]) {
				case "help":
					//give a list of commands
					return true;
				case "list":
					ResultSet reports = plugin.getDatabaseManager().getTable();
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
				case "listall":
					reports = plugin.getDatabaseManager().getTable();
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
				case "reload":
					plugin.reloadFiles();
					player.sendMessage("BasicReports files were reloaded.");
					return true;
				case "strike":
					reports = plugin.getDatabaseManager().getTable();
					try {
						while (reports.next()) {
							if (Integer.parseInt(args[1]) == reports.getInt("id")) {
								if (reports.getInt("has_checked") == 0) {
									plugin.getDatabaseManager().setChecked(reports.getInt("id"), 1);
									player.sendMessage(General.chat("&aReport id: &6" + reports.getInt("id") + " &ahas been struck."));
									return true;
								} else {
									player.sendMessage(General.chat("&cThat report has already been checked!"));
									return false;
								}
							}
						}
						player.sendMessage(General.chat("&cNo valid report was found with that id."));
						return false;
					} catch (SQLException e) {
						plugin.getLogger().logp(Level.WARNING, "ReportsCommand", "onCommand", "Striking a report did not work correctly");
						e.printStackTrace();
						return false;
					} catch (NumberFormatException e) {
						player.sendMessage(General.chat("&cThis must be an integer."));
						e.printStackTrace();
						return false;
					}
				case "clear":
					plugin.getDatabaseManager().clear();
					player.sendMessage(General.chat("&aThe database has been wiped clean."));
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
