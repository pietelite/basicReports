package me.PietElite.basicReports.utils.logging;

import java.util.LinkedList;
import java.util.List;
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
	
	private String MESSAGE_COLOR;
	
	private List<Player> developers;
	
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
		
		instance.MESSAGE_COLOR = plugin.getFileManager().getConfigConfig().getString("debug.message_color");
		
		instance.developers = new LinkedList<Player>();
		List<?> developerNames = plugin.getFileManager().getConfigConfig().getList("debug.developers");
		if (developerNames == null) {
			plugin.getLogger().logp(Level.WARNING, "BasicReportsLogger", "initialize", "The developer in config.yml is invalid");

		} else {
			for (Object developerObject : developerNames) {
				if (!(developerObject instanceof String)) {
					plugin.getLogger().logp(Level.WARNING, "BasicReportsLogger", "initialize", 
							"One or more of the developers in config.yml is not in the form of a string");
					continue;
				}
				instance.developers.add(Bukkit.getPlayer((String) developerObject));
			}
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
			for (Player developer : developers) {
				if (developer != null && developer.isOnline()) {
					developer.sendMessage(General.chat(
							General.PLUGIN_TAG + tag + MESSAGE_COLOR + msg));
				}
			}
		}
	}
}
