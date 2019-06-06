package me.PietElite.basicReports.utils.data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.PietElite.basicReports.BasicReports;

public class FileManager {
	
	private BasicReports plugin;
	
	private File configFile;
	private FileConfiguration configConfig;
	
	private File messagesFile;
	private FileConfiguration messagesConfig;
	
	private void initialize() {
		
		configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			plugin.saveResource("config.yml", false);
		}
		configConfig = YamlConfiguration.loadConfiguration(configFile);
		
		messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			plugin.saveResource("messages.yml", false);
		}
		messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
		
	}
	
	public FileManager(BasicReports plugin) {
		setPlugin(plugin);
		this.initialize();
	}
	
	private void setPlugin(BasicReports plugin) {
		this.plugin = plugin;
	}

	public File getConfigFile() {
		return configFile;
	}
	
	public FileConfiguration getConfigConfig() {
		return configConfig;
	}
	
	public File getMessagesFile() {
		return messagesFile;
	}
	
	public FileConfiguration getMessagesConfig() {
		return messagesConfig;
	}
	
	public List<String> getReportTypes() {
		List<?> reportTypes = getConfigConfig().getList("report_types");
		List<String> output = new LinkedList<String>();
		
		for (Object item : reportTypes) {
			
			if (item instanceof String) {
				output.add((String) item);
			}
		}
		return output;
	}

}
