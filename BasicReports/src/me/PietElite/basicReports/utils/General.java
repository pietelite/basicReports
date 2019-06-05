package me.PietElite.basicReports.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.PietElite.basicReports.BasicReports;

public class General {
	
	public static final String PLUGIN_TAG = "&7[&bBasicReports&7]&f ";
	
	// {USERNAME} - Player's username
	// {COMMAND} - Command name
	
	public static String chat(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static String taggedChat(String message) {
		return ChatColor.translateAlternateColorCodes('&', PLUGIN_TAG + message);
	}
	
	public static String chat(String message, String username, String commandName) {
		if (username != null) {
			message = message.replaceAll("_USERNAME", username);
		}
		
		if (commandName != null) {
			message = message.replaceAll("_COMMAND", commandName);
		}
		
		return chat(message);
	}
	
	public static void sendNoPermission(BasicReports plugin, Player player, String commandName) {
		player.sendMessage(chat(plugin.getFileManager().getMessagesConfig().getString("messages.no_permission"), player.getName(), commandName));
	}
	
	public static void sendInvalidArguments(BasicReports plugin, Player player, String commandName) {
		player.sendMessage(chat(plugin.getFileManager().getMessagesConfig().getString("messages.invalid_arguments"), player.getName(), commandName));
	}

	public static void sendInvalidBoolean(BasicReports plugin, Player player, String commandName) {
		player.sendMessage(chat(plugin.getFileManager().getMessagesConfig().getString("messages.invalid_boolean"), player.getName(), commandName));
	}
	
}
