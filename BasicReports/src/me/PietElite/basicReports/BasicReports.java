package me.PietElite.basicReports;

import org.bukkit.plugin.java.JavaPlugin;

import me.PietElite.basicReports.commands.ReportCommand;
import me.PietElite.basicReports.commands.ReportsCommand;
import me.PietElite.basicReports.utils.data.MysqlDatabaseManager;
import me.PietElite.basicReports.utils.data.BasicReportsDatabaseManagerInterface;
import me.PietElite.basicReports.utils.data.TxtDatabaseManager;
import me.PietElite.basicReports.utils.data.FileManager;
import me.PietElite.basicReports.utils.logging.BasicReportsLogger;

public class BasicReports extends JavaPlugin {

	private FileManager fileManager;
	@SuppressWarnings("unused")
	private ReportCommand reportCommand;
	@SuppressWarnings("unused")
	private ReportsCommand reportsCommand;
	private BasicReportsDatabaseManagerInterface databaseManager;
	private BasicReportsLogger basicReportsLogger;
	
	@Override
	public void onEnable() {
		
		// Make data folder
		this.getDataFolder().mkdir();
		
		// Initialize files
		fileManager = new FileManager(this);
		
		// Initialize logger
		basicReportsLogger = BasicReportsLogger.initialize(this.getLogger(), this);
		
		// Initialize database managers
		databaseManager = initializeDatabaseManager();
		// Initialize command executors
		reportCommand = ReportCommand.initialize(this);
		reportsCommand = ReportsCommand.initialize(this);
		
	}
	
	public BasicReportsDatabaseManagerInterface initializeDatabaseManager() {
		String storageType = getFileManager().getConfigConfig().getString("storage_type");
		switch (storageType) {
		case "mysql":
			return new MysqlDatabaseManager(this);
		case "txt":
			return new TxtDatabaseManager(this);
		default:
			return new TxtDatabaseManager(this);
		}
	}

	public FileManager getFileManager() {
		return fileManager;
	}
	
	public BasicReportsDatabaseManagerInterface getDatabaseManager() {
		return databaseManager;
	}
	
	public BasicReportsLogger getBasicReportsLogger() {
		return basicReportsLogger;
	}

	public void reloadFiles() {
		fileManager = new FileManager(this);
	}
}
