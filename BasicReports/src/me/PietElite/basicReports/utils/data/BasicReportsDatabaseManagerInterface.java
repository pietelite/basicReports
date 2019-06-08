package me.PietElite.basicReports.utils.data;

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

public interface BasicReportsDatabaseManagerInterface {
	
	/**
	 * Getter for all data held for the reports.
	 * @return A hashmap where the Integer is the report's ids and Report is the report corresponding to that id.
	 * 
	 */
	@Nonnull
	public HashMap<Integer,Report> getData();
	
	/**
	 * Add a report to the database.
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
	 * Set a report as resolved or unresolved in the database.
	 * @param id the id of the report
	 * @param isResolved true to set as resolved, false to set as unresolved
	 * @return whether state was changed
	 */
	public boolean setResolved(int id, boolean isResolved);
	
	/**
	 * Completely clear the entire database.
	 * @return
	 */
	public boolean clearDatabase();
	
	/**
	 * Clear reports made by this player. All other reports are then renumbered.
	 * @param player the player who made the report
	 * @return the number of cleared reports
	 */
	public int clearReports(Player player);
	
	/**
	 * Clear reports which are either resolved or unresolved.
	 * All other reports are then renumbered.
	 * @param resolved whether the reports cleared are resolved or unresolved
	 * @return the number of cleared reports
	 */
	public int clearReports(boolean resolved);
	
	/**
	 * Clear reports which have a specific report type.
	 * @param reportType the wrapper for the type of report to clear
	 * @return the number of cleared reports
	 */
	public int clearReports(Report.ReportType reportType);
	
	/**
	 * Reestablish the id numbers to account for previously cleared reports.
	 * @return True if successful
	 */
	public boolean reNumberReportIds();
	
	/**
	 * Check whether the database has some sort of error preventing normal functions.
	 * @return True if error exists
	 */
	public boolean isError();
	
	/**
	 * Externally set whether the database has an error with its functionality.
	 */
	public void setError(boolean error);

	public String getInfoMessage();
	
}
