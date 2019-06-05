package me.PietElite.basicReports.utils.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.PietElite.basicReports.BasicReports;

public class DatabaseManager {

	private static int lastReportID;
	
	private BasicReports plugin;
	
	private String mysqlHost;
	private int mysqlPort;
	private String mysqlDatabase;
	private String mysqlUsername;
	private String mysqlPassword;
	private String dataTableName;
	private Statement statement;
	private Connection connection;
	
	public static DatabaseManager initialize(BasicReports plugin) {
		DatabaseManager instance = new DatabaseManager();
		instance.plugin = plugin;
		lastReportID = 0;
		
		instance.mysqlHost = plugin.getFileManager().getConfigConfig().getString("my_sql_data.address");
		instance.mysqlPort = plugin.getFileManager().getConfigConfig().getInt("my_sql_data.port");
		instance.mysqlDatabase = plugin.getFileManager().getConfigConfig().getString("my_sql_data.database");
		instance.mysqlUsername = plugin.getFileManager().getConfigConfig().getString("my_sql_data.username");
		instance.mysqlPassword = plugin.getFileManager().getConfigConfig().getString("my_sql_data.password");
		instance.dataTableName = plugin.getFileManager().getConfigConfig().getString("my_sql_data.table_name");
		
		instance.generateTable();
		
		// Update last id number
		ResultSet reports = instance.getTable();
		try {
			while (reports.next()) {
				lastReportID = reports.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.SEVERE, "DatabaseManager", "initialize", "Error in trying to find the latest report id number");
		} catch (NullPointerException e) {
		}
		return instance;
	}
	
	public ResultSet getTable() {
		makeStatement();
		try {
			return statement.executeQuery("SELECT * FROM " + dataTableName);
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.SEVERE, "DatabaseManager", "getTable", "Table getter failed!");
			return null;
		}
		
	}

	private void generateTable() {
		makeStatement();
		try {
			statement.execute("CREATE TABLE IF NOT EXISTS " + dataTableName 
					+ " ("
					+ "id INT(7),"
					+ "has_checked INT(1),"
					+ "player_uuid VARCHAR(63),"
					+ "report_type VARCHAR(63),"
					+ "message VARCHAR(63),"
					+ "date BIGINT(15),"
					+ "location VARCHAR(63),"
					+ "location_world VARCHAR(63)"
					+ ");");
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.SEVERE, "DatabaseManager", "generateTable", "Table generation failed!");
		}
		
	}

	public boolean add(String playerID, String reportType, String message, Date date, String location, String locationWorld) {
		makeStatement();

		String sqlCommand = "INSERT INTO " + dataTableName 
				+ " (id, has_checked, player_uuid, report_type, message, date, location, location_world) VALUES "
				+ "(" + (lastReportID + 1) + ", "
				+ "0,"
				+ "'" + playerID + "', "
				+ "'" + reportType + "', "
				+ "'" + message.replaceAll("'", "''") + "', "
				+ date.getTime() + ", "
				+ "'" + location + "', "
				+ "'" + locationWorld + "');";
		try {
			statement.executeUpdate(sqlCommand);
			lastReportID++;
			plugin.getBasicReportsLogger().logpDev(Level.INFO, "DatabaseManager", "add", 
					Bukkit.getPlayer(UUID.fromString(playerID)).getName() + " just submitted a " + reportType + " report: " + message);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.WARNING, "DatabaseManager", "add", "Report addition failed! Command: " + sqlCommand);
			return false;
		}
	}
	
	public void setChecked(int id, int isChecked) {
		makeStatement();
		try {
			statement.executeUpdate("UPDATE " + dataTableName + " SET has_checked = " + isChecked + " WHERE " + "id = " + id + ";");
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, "DatabaseManager", "setChecked", "An error occured when striking a report");
			e.printStackTrace();
		}
	}

	public void clear() {
		makeStatement();
		try {
			statement.execute("DROP TABLE IF EXISTS " + dataTableName);
			initialize(plugin);
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, "DatabaseManager", "clear", "An error occured while clearing the database");
			e.printStackTrace();
		}
		
	}

	private void makeStatement() {
		
		try {
			openConnection();
			this.statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void openConnection() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed()) {
			return;
		}
		
		synchronized (this) {
			if (connection != null && !connection.isClosed()) {
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + this.mysqlHost + ":" + this.mysqlPort + "/" + this.mysqlDatabase, this.mysqlUsername, this.mysqlPassword);
		}
	}
	
}
