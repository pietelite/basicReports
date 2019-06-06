package me.PietElite.basicReports.utils.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import me.PietElite.basicReports.BasicReports;

public class MysqlDatabaseManager implements BasicReportsDatabaseManager {

	private static final String TAG = "MysqlDatabaseManager";
	
	private static int lastReportId;
	
	private BasicReports plugin;
	
	private String mysqlHost;
	private int mysqlPort;
	private String mysqlDatabase;
	private String mysqlUsername;
	private String mysqlPassword;
	private String dataTableName;
	private Statement statement;
	private Connection connection;
	private boolean error;
	
	public MysqlDatabaseManager(BasicReports plugin) {
		setPlugin(plugin);
		
		lastReportId = 0;
		
		mysqlHost = plugin.getFileManager().getConfigConfig().getString("my_sql_data.address");
		mysqlPort = plugin.getFileManager().getConfigConfig().getInt("my_sql_data.port");
		mysqlDatabase = plugin.getFileManager().getConfigConfig().getString("my_sql_data.database");
		mysqlUsername = plugin.getFileManager().getConfigConfig().getString("my_sql_data.username");
		mysqlPassword = plugin.getFileManager().getConfigConfig().getString("my_sql_data.password");
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
		ResultSet reports = (ResultSet) getData();
		lastReportId = 0;
		try {
			while (reports.next()) {
				lastReportId = reports.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.SEVERE, TAG, "initialize", "Error in trying to find the latest report id number");
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
	public Object getData() {
		makeStatement();
		try {
			return statement.executeQuery("SELECT * FROM " + dataTableName);
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
	public int clearReports(String condition) {
		makeStatement();
		try {
			int clearedReports = 0;
			clearedReports = statement.executeUpdate("DELETE FROM " + dataTableName + " WHERE " + condition + ";");
			reNumberReportIds();
			updateLastReportId();
			return clearedReports;
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, TAG, "clearReports", "An error occured while clearing specific reports from the database "
					+ "with condition " + condition);
			e.printStackTrace();

		}
		return 0;
	}

	@Override
	public boolean reNumberReportIds() {
		plugin.getLogger().logp(Level.INFO, TAG, "reNumberIds", "run");
		makeStatement();
		ResultSet reports = (ResultSet) getData();
		try {
			HashMap<Integer, Integer> reportReNumberMap = new HashMap<Integer, Integer>();
			
			int count = 0;
			while (reports.next()) {
				count++;
				reportReNumberMap.put(count, reports.getInt("id"));
			}
			
			for (Integer key : reportReNumberMap.keySet()) {
				plugin.getLogger().logp(Level.INFO, TAG, "reNumberIds", 
						"report with id: " + reportReNumberMap.get(key) + " is being changed to " + key);
				statement.executeUpdate("UPDATE " + dataTableName + " SET id = " + key + " WHERE id = " + reportReNumberMap.get(key) + ";");
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
	public Report getReport(int id) {
		makeStatement();
		try {
			ResultSet results = statement.executeQuery("SELECT FROM " + dataTableName + " WHERE id = " + id + ";");
			Report report;
			if (results.next()) {
				report = new Report(
						id,
						Bukkit.getPlayer(UUID.fromString(results.getString("player_uuid"))), 
						results.getString("report_type"), 
						results.getString("message"), 
						(results.getInt("has_checked") == 0) ? false : true, 
						new Date(results.getLong("date")), 
						results.getString("location"), 
						results.getString("location_world"));
				return report;
			} else {
				return null;
			}
		} catch (SQLException e) {
			plugin.getLogger().logp(Level.WARNING, TAG, "getReport", "An error occured while getting a report from the database. id: " + id);
			e.printStackTrace();
			return null;
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
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + this.mysqlHost + ":" + this.mysqlPort + "/" + this.mysqlDatabase, this.mysqlUsername, this.mysqlPassword);
		}
	}

}
