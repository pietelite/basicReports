package me.PietElite.basicReports;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.PietElite.basicReports.commands.ReportCommand;
import me.PietElite.basicReports.commands.ReportsCommand;
import me.PietElite.basicReports.utils.data.DatabaseManager;
import me.PietElite.basicReports.utils.data.FileManager;
import me.PietElite.basicReports.utils.logging.BasicReportsLogger;

public class BasicReports extends JavaPlugin {

	private FileManager fileManager;
	private ReportCommand reportCommand;
	private ReportsCommand reportsCommand;
	private DatabaseManager databaseManager;
	private BasicReportsLogger basicReportsLogger;
	
	@Override
	public void onEnable() {
		
		// Make data folder
		this.getDataFolder().mkdirs();
		
		// Initialize files
		fileManager = FileManager.initialize(this);
		
		// Initialize logger
		basicReportsLogger = BasicReportsLogger.initialize(this.getLogger(), this);
		
		// Initialize database manager
		databaseManager = DatabaseManager.initialize(this);
		// Initialize command executors
		reportCommand = ReportCommand.initialize(this);
		reportsCommand = ReportsCommand.initialize(this);
		
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	public BasicReportsLogger getBasicReportsLogger() {
		return basicReportsLogger;
	}

	public void reloadFiles() {
		fileManager = FileManager.initialize(this);
	}
}
