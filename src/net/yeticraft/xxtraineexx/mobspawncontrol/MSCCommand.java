package net.yeticraft.xxtraineexx.mobspawncontrol;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * @author XxTRAINEExX
 * This class holds all of the command structure for the plugin. There is also a reporting
 * method that probably doesn't belong here... but I'm tired so it's going here.
 *
 */
public class MSCCommand implements CommandExecutor {

	private final MobSpawnControl plugin;
	ArrayList<Block> topSpawners;

	public MSCCommand(MobSpawnControl plugin) {
		this.plugin = plugin;
		topSpawners = new ArrayList<Block>(plugin.reportSize);
	}

	enum SubCommand {

		HELP,
		STATS,
		TP,
		RESETSTATS,
		DEBUG,
		RELOAD,
		TOGGLE,
		UNKNOWN;

		private static SubCommand toSubCommand(String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (Exception ex) {
				return UNKNOWN;
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("msc.command")) {
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
			sender.sendMessage(ChatColor.DARK_AQUA + "===============");
			sender.sendMessage(ChatColor.AQUA + "Try /" + command.getName() + " HELP");
			return true;
		}
		if (args.length > 2) {
			sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
			sender.sendMessage(ChatColor.DARK_AQUA + "===============");
			sender.sendMessage(ChatColor.AQUA + "Looks like you typed too many parameters.");
			return true;
		}


		switch (SubCommand.toSubCommand(args[0].toUpperCase())) {
			case HELP:
				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl Help");
				sender.sendMessage(ChatColor.DARK_AQUA + "====================");
				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC HELP");
					return true;
				}

				sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " HELP: Shows this help page");
				if (sender.hasPermission("msc.stats")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " STATS: Lists current spawn stats.");
				}
				if (sender.hasPermission("msc.tp")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " TP <num>: Teleport to a given spawn location.");
				}
				if (sender.hasPermission("msc.resetstats")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " RESETSTATS: Clears all stats, spawners, mobs.");
				}
				if (sender.hasPermission("msc.debug")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " DEBUG: Enables DEBUG mode on the console.");
				}
				if (sender.hasPermission("msc.reload")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " RELOAD: Reloads config from disk.");
				}
				if (sender.hasPermission("msc.toggle")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " TOGGLE: Enables/Disables the plugin.");
				}
				break;
			case STATS:

				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl Stats");
				sender.sendMessage(ChatColor.DARK_AQUA + "=====================");

				// Check permissions for STATS command
				if (!sender.hasPermission("msc.stats")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC STATS");
					return true;
				}
				findTopSpawners(sender);
				break;

			case TP:

				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl TP");
				sender.sendMessage(ChatColor.DARK_AQUA + "==================");

				// Not going to allow TP for the console
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_AQUA + "How do you expect to teleport from a console?");
					return true;
				}

				// Check permissions for TP command
				if (!sender.hasPermission("msc.tp")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				// Did they type too many parameters?
				if (args.length > 2) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC TP");
					return true;
				}

				// Did they only type 1 parameter?
				if (args.length == 1) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " TP <type> <num>: Teleport to a given stat location.");
					sender.sendMessage(ChatColor.AQUA + "<num> :  Number pulled from the STATS list.");
					return true;
				}

				// Determing if they actually entered a number (Not a string)
				int spawnNumber;
				try {
					spawnNumber = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.AQUA + "You did not enter a valid NUMBER");
					return true;
				}

				// Making sure they entered a valid number in the hashmap
				if ((spawnNumber >= topSpawners.size())
						|| (spawnNumber < 0)) {
					sender.sendMessage(ChatColor.AQUA + "You did not enter a valid NUMBER from the STATS command. Rerun STATS and verify your entry.");
					return true;
				}
				if (topSpawners.get(spawnNumber) == null) {
					sender.sendMessage(ChatColor.AQUA + "You did not enter a valid NUMBER from the STATS command. Rerun STATS and verify your entry.");
					return true;
				}

				// Teleport player to spawner
				Player player = (Player) sender; // Cast already checked near beginning of command handler
				player.teleport(topSpawners.get(spawnNumber).getLocation());
				sender.sendMessage(ChatColor.AQUA + "Teleporting you to spawner #" + spawnNumber);
				if (plugin.debug) {
					plugin.log.info(plugin.prefix + sender.getName() + " teleported to spawner at: [" + topSpawners.get(spawnNumber).getLocation().getBlockX()
							+ "," + topSpawners.get(spawnNumber).getLocation().getBlockY() + "," + topSpawners.get(spawnNumber).getLocation().getBlockZ() + "]");
				}
				break;
			case RESETSTATS:

				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("msc.resetstats")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC RESETSTATS");
					return true;
				}

				plugin.myListener.activeMobs.clear();
				plugin.myListener.activeSpawners.clear();
				topSpawners.clear();
				sender.sendMessage(ChatColor.AQUA + "All stats reset successfully!");
				plugin.log.info(plugin.prefix + "All stats cleared from the server by " + sender.getName());
				break;

			case DEBUG:

				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("msc.debug")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC DEBUG");
					return true;
				}

				if (plugin.debug) {
					plugin.debug = false;
					sender.sendMessage(ChatColor.AQUA + "Debugging Disabled!");
					plugin.log.info(plugin.prefix + "Debugging disabled by " + sender.getName());
				} else {
					plugin.debug = true;
					sender.sendMessage(ChatColor.AQUA + "Debugging Enabled!");
					plugin.log.info(plugin.prefix + "Debugging enabled by " + sender.getName());
				}
				plugin.saveMainConfig();
				break;
			case RELOAD:

				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("msc.reload")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC RELOAD");
					return true;
				}

				plugin.reloadConfig();
				plugin.loadMainConfig();
				if (plugin.debug) {
					plugin.log.info(plugin.prefix + "Config reloaded from disk.");
				}
				sender.sendMessage(ChatColor.AQUA + "Config reloaded from disk.");

				break;

			case TOGGLE:

				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("msc.toggle")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC TOGGLE");
					return true;
				}

				if (plugin.pluginEnable) {
					plugin.pluginEnable = false;
					sender.sendMessage(ChatColor.AQUA + "Plugin Disabled!");
					plugin.log.info(plugin.prefix + "Plugin disabled by " + sender.getName());
				} else {
					plugin.pluginEnable = true;
					sender.sendMessage(ChatColor.AQUA + "Plugin Enabled!");
					plugin.log.info(plugin.prefix + "Plugin enabled by " + sender.getName());
				}
				plugin.saveMainConfig();
				break;
			case UNKNOWN:
				sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");
				sender.sendMessage(ChatColor.AQUA + "Unknown command. Use /msc HELP to list available commands.");
		}

		return true;
	}

	/**
	 * This method executes the spawn report and sends it to the sender.
	 * 
	 */
	public void findTopSpawners(CommandSender sender) {
		HashMap<Block, MSCSpawner> activeSpawners = plugin.myListener.activeSpawners;
		topSpawners.clear();

		// Iterating through all spawners in the hashmap
		Iterator<MSCSpawner> it = activeSpawners.values().iterator();

		// Temporary list to store the top ten spawners into
		LinkedList<MSCSpawner> templist = new LinkedList<MSCSpawner>();


		while (it.hasNext()) {
			MSCSpawner cur_spawner = it.next();
			cur_spawner.temp_counter = cur_spawner.getMobList().size();
			for (int i = 0; i < plugin.reportSize; i++) {
				// If we hit the end of the list before hitting the the top n items, add this one
				if (i >= templist.size()) {
					templist.add(cur_spawner);
					break;
				}
				// If our current item has more mobs than the current index, then we need to insert it
				if (cur_spawner.temp_counter > templist.get(i).temp_counter) {
					templist.add(i, cur_spawner);
					break;
				}
			}
		}

		//At this point, we have at least the configured number of spawners in our linked list...
		for (int i = 0; (i < plugin.reportSize) && (i < templist.size()); i++) {
			MSCSpawner cur_spawner = templist.get(i);

			topSpawners.add(i, cur_spawner.getBlock());

			if (cur_spawner.temp_counter >= (int) ((double) plugin.spawnsAllowed * plugin.alertThreshold)) {
				sender.sendMessage(ChatColor.AQUA + "[" + i + "] " + cur_spawner.getPlayer().getName()
						+ " : " + ChatColor.RED + cur_spawner.temp_counter + "/" + plugin.spawnsAllowed);
			} else if (cur_spawner.temp_counter >= (int) ((double) plugin.spawnsAllowed * plugin.warnThreshold)) {
				sender.sendMessage(ChatColor.AQUA + "[" + i + "] " + cur_spawner.getPlayer().getName()
						+ " : " + ChatColor.YELLOW + cur_spawner.temp_counter + "/" + plugin.spawnsAllowed);
			} else {
				sender.sendMessage(ChatColor.AQUA + "[" + i + "] " + cur_spawner.getPlayer().getName()
						+ " : " + cur_spawner.temp_counter + "/" + plugin.spawnsAllowed);
			}
		}
	}
}
