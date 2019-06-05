package me.PietElite.basicReports.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.PietElite.basicReports.BasicReports;

public class CommandHelpMap extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7681678632249481026L;
	
	private BasicReports plugin;
	
	public CommandHelpMap(BasicReports plugin) {
		this.plugin = plugin;
	}

	public String toChatString() {
		List<String> outputLines = new ArrayList<String>();
		outputLines.add(plugin.getFileManager().getMessagesConfig().getString("command_help_message.first_line"));
		for (String key : this.keySet()) {
			outputLines.add(plugin.getFileManager().getMessagesConfig().getString("command_help_message.each_command_line")
					.replaceAll("_ARGUMENTS", key).replaceAll("_DESCRIPTION", this.get(key)));
		}
		return String.join("\n", outputLines);
	}
	
}
