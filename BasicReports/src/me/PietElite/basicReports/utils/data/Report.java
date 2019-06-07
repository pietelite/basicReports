package me.PietElite.basicReports.utils.data;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.PietElite.basicReports.BasicReports;

public class Report {
			
	private int id;
	private Player player;
	private String type;
	private String message;
	private boolean resolved;
	private Date date;
	private Location location;
	
	public Report(int id, Player player, String type, String message, boolean resolved, Date date, Location location) {
		setId(id);
		setPlayer(player);
		setType(type);
		setMessage(message);
		setResolved(resolved);
		setDate(date);
		setLocation(location);
	}
	
	public Report(int id, Player player, String type, String message, boolean resolved, Date date, String commaSeparatedBlockLocation, String worldString) {
		setId(id);
		setPlayer(player);
		setType(type);
		setMessage(message);
		setResolved(resolved);
		setDate(date);
		setLocation(commaSeparatedBlockLocation, worldString);
	}

	public Report(Player player, String type, String message, Date date, Location location) {
		setId(-1);
		setPlayer(player);
		setType(type);
		setMessage(message);
		setResolved(false);
		setDate(date);
		setLocation(location);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Location getLocation() {
		return location;
	}
	
	public String getBlockLocation() {
		return "" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public void setLocation(String commaSeparatedBlockLocation, String worldString) {
		try {
			World world = Bukkit.getWorld(worldString);
			String[] blockLocationArray = commaSeparatedBlockLocation.split(",");
			location = new Location(world, 
					Double.parseDouble(blockLocationArray[0]), 
					Double.parseDouble(blockLocationArray[1]),
					Double.parseDouble(blockLocationArray[2]));
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static String printReport(BasicReports plugin, Report report) {
		SimpleDateFormat dateFormat = plugin.getFileManager().getDateFormat();
		dateFormat.setTimeZone(plugin.getFileManager().getTimeZone());
		
		String unformattedMessage;
		if (report.isResolved()) {
			unformattedMessage = plugin.getFileManager().getMessagesConfig().getString("reports_list.resolved");
		} else {
			unformattedMessage = plugin.getFileManager().getMessagesConfig().getString("reports_list.unresolved");
		}
		
		return unformattedMessage
				.replaceAll("_ID", String.valueOf(report.getId()))
				.replaceAll("_REPORTTYPE", report.getType())
				.replaceAll("_USERNAME", report.getPlayer().getName())
				.replaceAll("_MESSAGE", report.getMessage())
				.replaceAll("_DATE", dateFormat.format(report.getDate()))
				.replaceAll("_LOCATION", String.join(",", Arrays.asList(
						String.valueOf(report.getLocation().getBlockX()),
						String.valueOf(report.getLocation().getBlockY()),
						String.valueOf(report.getLocation().getBlockZ()))))
				.replaceAll("_LOCATIONWORLD", report.getLocation().getWorld().getName());
		
	}
	
	public static class ReportType {
		
		private String name;
		
		public ReportType(String name) {
			setName(name);
		}
		
		private void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

	}
	
}
