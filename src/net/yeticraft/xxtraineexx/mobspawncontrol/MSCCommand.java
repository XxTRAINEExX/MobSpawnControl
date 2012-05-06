package net.yeticraft.xxtraineexx.mobspawncontrol;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MSCCommand implements CommandExecutor{

	
	private final MobSpawnControl plugin;
	HashMap<Integer, Block> topSpawners = new HashMap<Integer, Block>();
	
	
	
	public MSCCommand(MobSpawnControl plugin) {
		this.plugin = plugin;
	}

	enum SubCommand {
		HELP,
		STATS,
		TP,
		RESETSTATS,
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
		
	Player player = (Player)sender;
	if (!player.hasPermission("msc.command")) {return true;}
	
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
	    		if (args.length > 1)
	    		{
	    			sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC HELP");
	    			return true;
	    		}
	    		sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " HELP: Shows this help page");
	    		sender.sendMessage(ChatColor.AQUA +  " /" + command.getName() + " STATS: Lists current spawn stats.");
	    		sender.sendMessage(ChatColor.AQUA +  " /" + command.getName() + " TP <num>: Teleport to a given spawn location.");
	    		sender.sendMessage(ChatColor.AQUA +  " /" + command.getName() + " RESETSTATS: Clears all stats, spawners, mobs.");
	    		break;
	    	case STATS:
	    		
	    		sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl Stats");
	    		sender.sendMessage(ChatColor.DARK_AQUA + "=====================");
	    		
	    		// Check permissions for STATS command
	    		if (!player.hasPermission("msc.stats")) {
	    			sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
	    			return true;
	    		}
	    		
	    		if (args.length > 1)
	    		{
	    			sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC STATS");
	    			return true;
	    		}
	    		findTopSpawners(sender);
	    		break;
	    
	    	case TP:
	    		
	    		sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl TP");
	    		sender.sendMessage(ChatColor.DARK_AQUA + "==================");
	    		
	    		// Check permissions for TP command
	    		if (!player.hasPermission("msc.tp")) {
	    			sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
	    			return true;
	    		}

	    		// Did they type too many parameters?
	    		if (args.length > 2)
	    		{
	    			sender.sendMessage(ChatColor.AQUA +  "Too manyparameters! Try /MSC TP");
	    			return true;
	    		}
	    		
	    		// Did they only type 1 parameter?
	    		if (args.length == 1)
	    		{
	    			sender.sendMessage(ChatColor.AQUA +  " /" + command.getName() + " TP <type> <num>: Teleport to a given stat location.");
					sender.sendMessage(ChatColor.AQUA +  "<num> :  Number pulled from the STATS list.");
		   		return true;
	    		}
	    		
	    		// Determing if they actually entered a number (Not a string)
	    		int spawnNumber;
	    		try{
	    			spawnNumber = Integer.parseInt(args[1]);
	    		}
	    		catch(NumberFormatException e){
	    			sender.sendMessage(ChatColor.AQUA +  "You did not enter a valid NUMBER");
	    			return true;
		    	}
	    		
	    		// Making sure they entered a valid number in the hashmap
	    		if (topSpawners.get(spawnNumber) == null){
	    			sender.sendMessage(ChatColor.AQUA +  "You did not enter a valid NUMBER from the STATS command. Rerun STATS and verify your entry.");
	    			return true;
	    		}
	    		
	    		// Teleport player to spawner
	    		
	    		player.teleport(topSpawners.get(spawnNumber).getLocation());
	    		sender.sendMessage(ChatColor.AQUA +  "Teleporting you to spawner: " + spawnNumber);
	    		break;
	      	case RESETSTATS:
	      		
	      		sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
	    		sender.sendMessage(ChatColor.DARK_AQUA + "===============");
	    		
	    		// Check permissions for STATS command
	    		if (!player.hasPermission("msc.resetstats")) {
	    			sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
	    			return true;
	    		}
	    		
	    		if (args.length > 1)
	    		{
	    			sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /MSC RESETSTATS");
	    			return true;
	    		}
	    		
	    		plugin.myListener.mobSet.clear();
	    		plugin.myListener.spawnCount.clear();
	    		plugin.myListener.spawnerSet.clear();
	    		plugin.myListener.spawnOwners.clear();
	    		topSpawners.clear();
	    		sender.sendMessage(ChatColor.AQUA + "All stats reset successfully!");
	    		plugin.log.info(plugin.prefix + "All stats cleared from the server by: " + player.getName());
				break;
	      		
	    	case UNKNOWN:
	    		sender.sendMessage(ChatColor.DARK_AQUA + "MobSpawnControl");
	    		sender.sendMessage(ChatColor.DARK_AQUA + "===============");
	    		sender.sendMessage(ChatColor.AQUA +  "Unknown command. Use /msc HELP to list available commands.");
    	}
    	
		return true;
	
	
	}
	
	public void findTopSpawners(CommandSender sender){
		
		HashMap<Block, Player> spawnOwners = plugin.myListener.spawnOwners;
		HashMap<Block, Integer> spawnCount = plugin.myListener.spawnCount;
		topSpawners.clear();
	
		// Iterating through all spawners in the hashmap
		Iterator<Block> it = spawnCount.keySet().iterator();
		int i = 1;
		while(it.hasNext() && i <= plugin.reportSize) {

			Block spawner = it.next();
			if (spawnCount.get(spawner) > (int)((double)plugin.spawnsAllowed * .75)){
				sender.sendMessage(ChatColor.AQUA + "[" + i + "] " + spawnOwners.get(spawner).getName() 
						+ " : " + ChatColor.RED + spawnCount.get(spawner) + "/" + plugin.spawnsAllowed);
				topSpawners.put(i,spawner);
				i++;
				continue;
				
	    	}
			
			if (spawnCount.get(spawner) > (int)((double)plugin.spawnsAllowed * .50)){
				sender.sendMessage(ChatColor.AQUA + "[" + i + "] " + spawnOwners.get(spawner).getName() 
						+ " : " + ChatColor.YELLOW + spawnCount.get(spawner) + "/" + plugin.spawnsAllowed);
				topSpawners.put(i,spawner);
				i++;
				continue;
	    	}
			
			sender.sendMessage(ChatColor.AQUA + "[" + i + "] " + spawnOwners.get(spawner).getName() 
					+ " : " + spawnCount.get(spawner) + "/" + plugin.spawnsAllowed);
			topSpawners.put(i,spawner);
			i++;
			
		}
		
	}
	
	
	
}
