package me.PietElite.basicReports.utils.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.data.Report.ReportType;

public class H2DatabaseManager implements BasicReportsDatabaseManager {

	private static final String TAG = "H2DatabaseManager";
	
	private static int lastReportId;
	
	private BasicReports plugin;

	private String dataTableName;
	private Statement statement;
	private Connection connection;
	private boolean error;
	
	public H2DatabaseManager(BasicReports plugin) {
		setPlugin(plugin);
		
		lastReportId = 0;

		dataTableName = plugin.getFileManager().getConfigConfig().getString("my_sql_data.table_name");
		
		error = !checkConnection();
		if (error) {
			plugin.getLogger().logp(Level.SEVERE, "MysqlDatabaseManager", "constructor", 
					"You have an error in your database manager. Something went wrong!");
		} else {
			generateTable();
			updateLastReportId();
		}
		
	}

	private void updateLastReportId() {
		HashMap<Integer, Report> data = getData();
		if (data.isEmpty()) {
			lastReportId = 0;
		} else {
			lastReportId = Collections.max(getData().keySet());
		}
	}

	private void generateTable(){
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
			plugin.getLogger().logp(Level.SEVERE, TAG, "generateTable", "Table generation failed!");
		}
		
	}

	@Override
	public HashMap<Integer,Report> getData() {
		makeStatement();
		try {
			ResultSet results = statement.executeQuery("SELECT * FROM " + dataTableName);
			HashMap<Integer,Report> data = new HashMap<Integer,Report>();
			while (results.next()) {
				int id = results.getInt("id");
				data.put(id,new Report(
						id,
						Bukkit.getPlayer(UUID.fromString(results.getString("player_uuid"))), 
						results.getString("report_type"), 
						results.getString("message"), 
						(results.getInt("has_checked") == 0) ? false : true, 
						new Date(results.getLong("date")), 
						results.getString("location"), 
						results.getString("location_world")));
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.SEVERE, TAG, "getTable", "Table getter failed!");
			return null;
		}
	}

	@Override
	public boolean setResolved(int id, boolean isResolved) {
		makeStatement();
		try {
			statement.executeUpdate("UPDATE " + dataTableName + " SET has_checked = " + ((isResolved) ? 1 : 0) + " WHERE " + "id = " + id + ";");
			return true;
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, TAG, "setChecked", "An error occured when striking a report");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean clearDatabase() {
		makeStatement();
		try {
			statement.execute("DROP TABLE IF EXISTS " + dataTableName);
			generateTable();
			updateLastReportId();
			return true;
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, TAG, "clearDatabase", "An error occured while clearing the database");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int clearReports(Player player) {
		int clearedReports = clearReports("player_uuid = '" + player.getUniqueId().toString() + "'");
		plugin.getBasicReportsLogger().logpDev(Level.INFO, "MysqlDatabaseManager", "clearReports", 
				"Cleared " + clearedReports + " reports with from player " + player.getName() + ".");
		return clearedReports;
	}

	@Override
	public int clearReports(boolean resolved) {
		int clearedReports = clearReports("has_checked = " + ((resolved) ? 1 : 0));
		plugin.getBasicReportsLogger().logpDev(Level.INFO, "MysqlDatabaseManager", "clearReports", 
				"Cleared " + clearedReports + " " + ((resolved) ? "" : "un") + "resolved reports.");
		return clearedReports;
	}

	@Override
	public int clearReports(ReportType reportType) {
		int clearedReports = clearReports("report_type = '" + reportType.getName() + "'");
		plugin.getBasicReportsLogger().logpDev(Level.INFO, "MysqlDatabaseManager", "clearReports", 
				"Cleared " + clearedReports + " reports under the category " + reportType.getName() + ".");
		return clearedReports;
	}
	
	/**
	 * Helper function for all other clearReports methods
	 * @param sqlCondition
	 * @return number of cleared reports
	 */
	private int clearReports(String sqlCondition) {
		makeStatement();
		try {
			int clearedReports = statement.executeUpdate("DELETE FROM " + dataTableName + " WHERE " + sqlCondition + ";");
			reNumberReportIds();
			updateLastReportId();
			return clearedReports;
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, TAG, "clearReports", "An error occured while clearing specific reports from the database "
					+ "with condition " + sqlCondition);
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public boolean reNumberReportIds() {
		makeStatement();
		try {			
			int count = 0;
			for (int id : getData().keySet()) {
				count++;
				if (id != count) {
					plugin.getBasicReportsLogger().logpDev(Level.INFO, TAG, "reNumberIds", 
							"report with id " + id + " is being updated to " + count);
					statement.executeUpdate("UPDATE " + dataTableName + " SET id = " + count + " WHERE id = " + id + ";");
				}
			}
			updateLastReportId();
			return true;
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, TAG, "clear", "An error occured while renumbering the database");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean addReport(Report report) {
		makeStatement();

		String sqlCommand = "INSERT INTO " + dataTableName 
				+ " (id, has_checked, player_uuid, report_type, message, date, location, location_world) VALUES "
				+ "(" + (lastReportId + 1) + ", "
				+ "0,"
				+ "'" + report.getPlayer().getUniqueId().toString() + "', "
				+ "'" + report.getType() + "', "
				+ "'" + report.getMessage().replaceAll("'", "''") + "', "
				+ report.getDate().getTime() + ", "
				+ "'" + report.getLocation().toVector() + "', "
				+ "'" + report.getLocation().getWorld().getName() + "');";
		try {
			statement.executeUpdate(sqlCommand);
			lastReportId++;
			plugin.getBasicReportsLogger().logpDev(Level.INFO, "DatabaseManager", "add", 
					report.getPlayer().getName() + " just submitted a " + report.getType() + " report: " + report.getMessage());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.WARNING, TAG, "add", "Report addition failed! Command: " + sqlCommand);
			return false;
		}
	}
	
	@Override
	public void setPlugin(BasicReports plugin) {
		this.plugin = plugin;
	}

	private boolean checkConnection() {
		
		try {
			openConnection();
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (CommunicationsException e) {
			plugin.getLogger().logp(Level.SEVERE, TAG, "makeStatement", "A connection couldn't be made with the sql information you supplied. "
					+ "Check to make sure your information is correct.");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean hasError() {
		return error;
	}

	private void makeStatement() {
		
		try {
			openConnection();
			this.statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (CommunicationsException e) {
			plugin.getLogger().logp(Level.SEVERE, TAG, "makeStatement", "A connection couldn't be made with the sql information you supplied. "
					+ "Check to make sure your information is correct.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void openConnection() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed()) {
			return;
		}
		synchronized (this) {
			if (connection != null && !connection.isClosed()) {
				return;
			}
			Class.forName("org.h2.Driver"); 
			connection = DriverManager.getConnection("jdbc:h2://" + plugin.getDataFolder().getPath());
		}
	}
}
