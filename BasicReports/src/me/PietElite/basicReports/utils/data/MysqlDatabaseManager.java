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
import org.bukkit.entity.Player;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.data.Report.ReportType;

public class MysqlDatabaseManager extends BasicReportsDatabaseManager implements BasicReportsDatabaseManagerInterface {

	private static final String TAG = "MysqlDatabaseManager";
		
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
		
		setLastReportId(0);
		
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
			updateLastReportId(getData().keySet());
		}
		
	}

	private void generateTable(){
		makeStatement();
		try {
			statement.execute("CREATE TABLE IF NOT EXISTS " + dataTableName 
					+ " ("
					+ "id INT(7),"
					+ "is_resolved INT(1),"
					+ "player_uuid VARCHAR(63),"
					+ "report_type VARCHAR(63),"
					+ "message VARCHAR(127),"
					+ "date BIGINT(15),"
					+ "location_block VARCHAR(63),"
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
						(results.getInt("is_resolved") == 0) ? false : true, 
						new Date(results.getLong("date")), 
						results.getString("location_block"), 
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
			statement.executeUpdate("UPDATE " + dataTableName + " SET is_resolved = " + ((isResolved) ? 1 : 0) + " WHERE " + "id = " + id + ";");
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
			updateLastReportId(getData().keySet());
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
		int clearedReports = clearReports("is_resolved = " + ((resolved) ? 1 : 0));
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
			updateLastReportId(getData().keySet());
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
			updateLastReportId(getData().keySet());
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
				+ " (id, is_resolved, player_uuid, report_type, message, date, location_block, location_world) VALUES "
				+ "(" + (getLastReportId() + 1) + ", "
				+ "0,"
				+ "'" + report.getPlayer().getUniqueId().toString() + "', "
				+ "'" + report.getType() + "', "
				+ "'" + report.getMessage().replaceAll("'", "''") + "', "
				+ report.getDate().getTime() + ", "
				+ "'" + report.getLocation().toVector() + "', "
				+ "'" + report.getLocation().getWorld().getName() + "');";
		try {
			statement.executeUpdate(sqlCommand);
			setLastReportId(getLastReportId() + 1);
			plugin.getBasicReportsLogger().logpDev(Level.INFO, "DatabaseManager", "add", 
					report.getPlayer().getName() + " just submitted a " + report.getType() + " report: " + report.getMessage());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().logp(Level.WARNING, TAG, "add", "Report addition failed! Command: " + sqlCommand);
			return false;
		}
	}

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
	public boolean isError() {
		return error;
	}
	
	@Override
	public void setError(boolean error) {
		super.setError(error);
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

	@Override
	public String getInfoMessage() {
		return null;
	}

}
