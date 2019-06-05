package me.PietElite.basicReports.utils.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.PietElite.basicReports.BasicReports;
import me.PietElite.basicReports.utils.General;

public class BasicReportsLogger {
	
	// TODO: Javadocs
	private BasicReports plugin;
	private Logger defaultLogger;
	
	private String TAG_INFO;
	private String TAG_SEVERE;
	private String TAG_WARNING;
	
	private String DESCRIPTION_COLOR;
	private String MESSAGE_COLOR;
	
	private Player developer;
	
	public boolean isDevmode() {
		return plugin.getFileManager().getConfigConfig().getBoolean("debug.dev_mode");
	}
	
	public static BasicReportsLogger initialize(Logger logger, BasicReports plugin) {
		BasicReportsLogger instance = new BasicReportsLogger();
		instance.defaultLogger = logger;
		
		instance.plugin = plugin;
		
		instance.TAG_INFO = plugin.getFileManager().getConfigConfig().getString("debug.tags.info");
		instance.TAG_SEVERE = plugin.getFileManager().getConfigConfig().getString("debug.tags.severe");
		instance.TAG_WARNING = plugin.getFileManager().getConfigConfig().getString("debug.tags.warning");
		
		instance.DESCRIPTION_COLOR = plugin.getFileManager().getConfigConfig().getString("debug.description_color");
		instance.MESSAGE_COLOR = plugin.getFileManager().getConfigConfig().getString("debug.message_color");
		
		String developerName = plugin.getFileManager().getConfigConfig().getString("debug.developer");
		if (developerName == null) {
			plugin.getLogger().logp(Level.WARNING, "BasicReportsLogger", "initialize", "The developer in config.yml is invalid");

		} else {
			instance.developer = Bukkit.getPlayer(developerName);
		}
		
		
		return instance;
	}

	
	public void logpDev(Level level, String sourceClass, String sourceMethod, String msg) {
		if (isDevmode()) {
			defaultLogger.logp(level, sourceClass, sourceMethod, msg);
			
			String tag;
			switch (level.getName().toUpperCase()) {
			case "INFO":
				tag = TAG_INFO;
				break;
			case "SEVERE":
				tag = TAG_SEVERE;
				break;
			case "WARNING":
				tag = TAG_WARNING;
				break;
			default:
				tag = "&f[" + level.getName().toUpperCase() + "]";
				break;
			}
			
			if (developer != null && developer.isOnline()) {
				developer.sendMessage(General.chat(
						tag +
						" &7{" + DESCRIPTION_COLOR + sourceClass + ": " + sourceMethod + "&7} " +
						MESSAGE_COLOR + msg));
			}
		}
	}

}
