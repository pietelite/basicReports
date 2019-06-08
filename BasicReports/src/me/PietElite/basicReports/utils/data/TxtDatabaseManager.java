package me.PietElite.basicReports.utils.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.data.Report.ReportType;

public class TxtDatabaseManager extends BasicReportsDatabaseManager implements BasicReportsDatabaseManagerInterface {

	public static final String FILE_NAME = "ReportsData.txt";
	public static final String ORGANIZATION = "id, username, date (unix), type, message, isresolved, location (x), location (y), location (z), location (world)";
	public static final String DELIMITER = "%";
	public static final String INFO_MESSAGE = "Note: reports containing '%' are prohobited";
	
	private File reportsDataFile;
	private BasicReports plugin;

	private BufferedReader fileReader;
	
	public TxtDatabaseManager(BasicReports plugin) {
		
		this.plugin = plugin;
		
		setReportsDataFile(new File(plugin.getFileManager().getReportDataFolderPath() + "/" + FILE_NAME));
		try {
			if (!getReportsDataFile().exists()) {
				if (!getReportsDataFile().createNewFile()) {
					setError(true);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updateLastReportId(getData().keySet());
		
	}

	@Override
	public HashMap<Integer, Report> getData() {
		HashMap<Integer, Report> output = new HashMap<Integer, Report>();
		
		try {
			fileReader = new BufferedReader(new FileReader(reportsDataFile));
			String line;
			while ((line = fileReader.readLine()) != null) {
				
				if (line.trim().length() == 0) {
					continue;
				}
				
				String[] dataArray = line.split(DELIMITER);
				output.put(Integer.valueOf(dataArray[0]), readReport(dataArray));
			
			}
		} catch (IOException e) {
			setError(true);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			setError(true);
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return output;
    }

	private Report readReport(String[] dataArray) throws NumberFormatException {
		return new Report(
				Integer.valueOf(dataArray[0]), 
				Bukkit.getPlayer(UUID.fromString(dataArray[1])), 
				dataArray[3], 
				dataArray[4], 
				Boolean.parseBoolean(dataArray[5]), 
				new Date(Long.parseLong(dataArray[2])), 
				new Location(Bukkit.getWorld(dataArray[9]), 
						Double.parseDouble(dataArray[6]), 
						Double.parseDouble(dataArray[7]), 
						Double.parseDouble(dataArray[8])));
	}

	@Override
	public boolean addReport(Report report) {
		return addReport(report, getReportsDataFile(), false);
	}
	
	private boolean addReport(Report report, File file, boolean useReportsNativeId) {
		if (report.getMessage().contains(DELIMITER)) {
			return false;
		}
		if (useReportsNativeId) {
			return addLineToFile(printStorageLine(report), file);
		} else {
			report.setId(getLastReportId() + 1);
			setLastReportId(getLastReportId() + 1);
			return addLineToFile(printStorageLine(report), file);
		}
	}
	
	private String printStorageLine(Report report) {
		return ""
				+ report.getId() + DELIMITER
				+ report.getPlayer().getUniqueId() + DELIMITER
				+ report.getDate().getTime() + DELIMITER
				+ report.getType() + DELIMITER
				+ report.getMessage() + DELIMITER
				+ report.isResolved() + DELIMITER
				+ report.getLocation().getBlockX() + DELIMITER
				+ report.getLocation().getBlockY() + DELIMITER
				+ report.getLocation().getBlockZ() + DELIMITER
				+ report.getLocation().getWorld().getName();
	}
	
	private boolean addLineToFile(String line, File file) {
		String lineWithNewLine = line + "\n";
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file, true);
			outputStream.write(lineWithNewLine.getBytes(), 0, lineWithNewLine.length());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally{
            try {
            	outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (NullPointerException e) {
            	e.printStackTrace();
            	return false;
            }
        }
		return true;
	}

	@Override
	public boolean setResolved(int id, boolean resolved) {

		try {
			
			HashMap<Integer, Report> data = getData();
			boolean changed = !(resolved == data.get(id).isResolved());
			data.get(id).setResolved(resolved);
			for (Report report : data.values()) {
				addReport(report, getReportsDataCopyFile(), true);
			}
			getReportsDataFile().delete();
			getReportsDataCopyFile().renameTo(reportsDataFile);
			return changed;
		}  catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
	}

	private File getReportsDataCopyFile() {
		File copiedDataFile = new File(plugin.getFileManager().getReportDataFolderPath() + "/" + "temp.txt");
		try {
			copiedDataFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return copiedDataFile;
	}

	@Override
	public boolean clearDatabase() {
		getReportsDataFile().delete();
		try {
			getReportsDataFile().createNewFile();
			updateLastReportId(Collections.emptySet());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int clearReports(Player player) {
		int count = 0;
		
		try {
			
			for (Report report : getData().values()) {
				if (!report.getPlayer().equals(player)) {
					addReport(report, getReportsDataCopyFile(), true);
				} else {
					count++;
				}
			}
			getReportsDataFile().delete();
			getReportsDataCopyFile().renameTo(reportsDataFile);
			reNumberReportIds();
			updateLastReportId(getData().keySet());
			return count;
		}  catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int clearReports(boolean resolved) {
		int count = 0;
		
		try {
			
			for (Report report : getData().values()) {
				if (!report.isResolved() == resolved) {
					addReport(report, getReportsDataCopyFile(), true);
				} else {
					count++;
				}
			}
			getReportsDataFile().delete();
			getReportsDataCopyFile().renameTo(reportsDataFile);
			reNumberReportIds();
			updateLastReportId(getData().keySet());
			return count;
		}  catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int clearReports(ReportType reportType) {
		int count = 0;
		
		try {
			
			for (Report report : getData().values()) {
				if (!report.getType().equals(reportType.getName())) {
					addReport(report, getReportsDataCopyFile(), true);
				} else {
					count++;
				}
			}
			getReportsDataFile().delete();
			getReportsDataCopyFile().renameTo(reportsDataFile);
			reNumberReportIds();
			updateLastReportId(getData().keySet());
			return count;
		}  catch (NullPointerException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public boolean reNumberReportIds() {
		
		int lineCount = 0;
		try {
			
			for (Report report : getData().values()) {
				lineCount++;
				report.setId(lineCount);
				addReport(report, getReportsDataCopyFile(), true);
			}
			getReportsDataFile().delete();
			getReportsDataCopyFile().renameTo(reportsDataFile);
			updateLastReportId(getData().keySet());
			return true;
		}  catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isError() {
		return super.isError();
	}
	
	@Override
	public void setError(boolean error) {
		super.setError(error);
	}

	public File getReportsDataFile() {
		return reportsDataFile;
	}

	public void setReportsDataFile(File reportsDataFile) {
		this.reportsDataFile = reportsDataFile;
	}

	@Override
	public String getInfoMessage() {
		return INFO_MESSAGE;
	}

}
