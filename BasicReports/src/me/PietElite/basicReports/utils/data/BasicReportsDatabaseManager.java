package me.PietElite.basicReports.utils.data;

import me.PietElite.basicReports.BasicReports;

public interface BasicReportsDatabaseManager {
	
	/**
	 * Setter for the overall plugin Main file.
	 * @param plugin
	 */
	public void setPlugin(BasicReports plugin);
	
	/**
	 * Getter for all data held for the reports.
	 * @return An object which houses all the report data
	 */
	public Object getData();
	
	/**
	 * Add a report to the database
	 * @param playerID The String version of the UUID of the player who sent the report
	 * @param reportType The report type
	 * @param message The main body of the report
	 * @param date The Java Date object for the time at which the report was sent
	 * @param location The 
	 * @param locationWorld
	 * @return
	 */
	public boolean addReport(Report report);
	
	/**
	 * Gets a Report object from the database at the first report with the given id
	 * @param id The id of the report which is to be converted to a Report
	 * @return A new Report object corresponding with the information in the database
	 */
	public Report getReport(int id);
	
	public boolean setResolved(int id, boolean isResolved);
	
	public boolean clearDatabase();
	
	public int clearReports(String condition);
	
	public boolean reNumberReportIds();
	
	public boolean hasError();
}
