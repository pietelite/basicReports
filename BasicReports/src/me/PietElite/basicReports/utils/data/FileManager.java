package me.PietElite.basicReports.utils.data;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.PietElite.basicReports.BasicReports;

public class FileManager {
	
	private BasicReports plugin;
	
	private File configFile;
	private FileConfiguration configConfig;
	
	private File messagesFile;
	private FileConfiguration messagesConfig;
	
	public static FileManager initialize(BasicReports plugin) {
		FileManager instance = new FileManager();
		
		instance.plugin = plugin;
		
		instance.configFile = new File(plugin.getDataFolder(), "config.yml");
		instance.configConfig = YamlConfiguration.loadConfiguration(instance.configFile);
		instance.initializeConfig();
		
		instance.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		instance.messagesConfig = YamlConfiguration.loadConfiguration(instance.messagesFile);
		instance.initializeMessages();
		
		return instance;
	}
	
	public void initializeConfig() {
		if (!configFile.exists()) {
			plugin.saveResource("config.yml", false);
		}
	}
	
	public void initializeMessages() {
		if (!messagesFile.exists()) {
			plugin.saveResource("messages.yml", false);
		}
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

}
