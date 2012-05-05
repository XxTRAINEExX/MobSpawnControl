package net.yeticraft.xxtraineexx.mobspawncontrol;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class MSCListener implements Listener{

	public static MobSpawnControl plugin;
	HashMap<Block, Set<Entity>> spawnerSet = new HashMap<Block, Set<Entity>>();
	HashMap<Entity, Block> mobSet = new HashMap<Entity, Block>();
	HashMap<Block, Player> spawnOwners = new HashMap<Block, Player>();
	HashMap<Block, Integer> spawnCount = new HashMap<Block, Integer>();
	

	public MSCListener(MobSpawnControl plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		MSCListener.plugin = plugin;
	}
	
	
	public void onPluginEnable (PluginEnableEvent event) {
		
		plugin.log.info(("Plugin detected: " + event.getPlugin().toString()));
		
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		
		
		// If this didn't come from a spawner, return out.
		if (!e.getSpawnReason().toString().equalsIgnoreCase("SPAWNER")){

			return;
			
		}
		
		plugin.log.info("Mob spawned FROM  a spawner.. Finding loc.");
		
		// Find the spawner this monster came from.
		Block spawnedMobLoc = e.getLocation().getBlock();
		Block currentBlock = null;
		Block mobSpawner = null;
		Entity spawnedMob = e.getEntity();
		Player player = null;
		
		// Mobs can only spawn within a 8x3x8 area
		int lowerX = spawnedMobLoc.getX() - 7;
		int upperX = spawnedMobLoc.getX() + 7;
		int lowerY = spawnedMobLoc.getY() - 2;
		int upperY = spawnedMobLoc.getY() + 2;
		int lowerZ = spawnedMobLoc.getZ() - 7;
		int upperZ = spawnedMobLoc.getZ() + 7;
		boolean keepLooping = true;
		
		plugin.log.info("lowerX: " + lowerX);
		plugin.log.info("upperX: " + upperX);
		plugin.log.info("lowerY: " + lowerY);
		plugin.log.info("upperY: " + upperY);
		plugin.log.info("lowerZ: " + lowerZ);
		plugin.log.info("upperZ: " + upperZ);
		
		
		// Searching all nearby blocks to find the spawner
		for (int y = lowerY; y <= upperY && keepLooping; y++){
			for (int x = lowerX; x <= upperX && keepLooping; x++){
				for (int z = lowerZ; z <= upperZ; z++){
					
					currentBlock = e.getLocation().getWorld().getBlockAt(x, y, z);
					if (currentBlock.getTypeId() == 52){
						
						// Found your ass...
						plugin.log.info("Found spawner at: " + currentBlock.getX() + "," + currentBlock.getY() + "," + currentBlock.getZ());
						mobSpawner = currentBlock;
						keepLooping = false;
						break;
						
					}
					
				}
			}
		}
				
		
		// If mobSpawner is still null we must have missed the spawner somehow.
		if (mobSpawner == null){
			plugin.log.info("Spawner not found. Something went wrong.");
			return;
		}
		
		// Checking for nearby players
		for (Player nearby : Bukkit.getServer().getOnlinePlayers()) {	
			double nearbyDistance = nearby.getLocation().distance(mobSpawner.getLocation());
			if (nearbyDistance <= 17){
				player = nearby;
				plugin.log.info("Nearby player [" + player.getName() + "] found [" + Math.floor(nearbyDistance) + "] blocks away.");
				break;
			}
			
		}

		// Assigning this spawner to the player.
		spawnOwners.put(mobSpawner, player);
		
		// Lets create a new Hashset to store the mobs associated with a spawner
		Set<Entity> mobList = new HashSet<Entity>();
		
		// If the spawner is NOT in the hashmap we will add the monster to the new mobList and add the spawner/mobList to the spawnerSet hashmap
		if (!spawnerSet.containsKey(mobSpawner)){
			mobList.add(spawnedMob);
			mobSet.put(spawnedMob, mobSpawner);
			spawnerSet.put(mobSpawner, mobList);
			spawnCount.put(mobSpawner, mobList.size());
			plugin.log.info("New spawner found.  Adding to hashset / hashmap." + mobSpawner.getX() + "," + mobSpawner.getY() + "," + mobSpawner.getZ());
			e.setCancelled(false);
			return;
		}
		
		// Looks like the mobSpawner is already in the spawnerSet. Lets check to see if this set has reached its limit
		mobList = spawnerSet.get(mobSpawner);
		
		if (mobList.size() >= plugin.spawnsAllowed){
			plugin.log.info("Spawner maximum reached: ["+ mobList.size() + "] Stopping additional spawns: " + mobSpawner.getX() + "," + mobSpawner.getY() + "," + mobSpawner.getZ());
			e.setCancelled(true);
			return;
		}
		
		// Looks like the current mobSpawner is not at its maximum. Let's increment.
		mobList.add(spawnedMob);
		mobSet.put(spawnedMob, mobSpawner);
		spawnCount.put(mobSpawner, mobList.size());
		plugin.log.info("Spawner currently contains [" + mobList.size() + "] monsters. Allowing spawn." + mobSpawner.getX() + "," + mobSpawner.getY() + "," + mobSpawner.getZ());
		e.setCancelled(false);
		return;
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e) {
		
		Entity spawnedMob = e.getEntity();
		
		if (mobSet.containsKey(spawnedMob)){
			
			// Finding the spawner this entity is attached to
			Block mobSpawner = mobSet.get(spawnedMob);
			
			// Finding the MobList associated with this spawner
			Set<Entity> mobList = spawnerSet.get(mobSpawner);
			
			// Removing this mob from the spawnList
			mobList.remove(spawnedMob);
			
			// Removing this mob from the mobSet
			mobSet.remove(spawnedMob);
			
			// Update the spawnCounter Hashmap
			spawnCount.put(mobSpawner, mobList.size());
			
			// Let's report a mob has been removed from a spawner. (Must have died)
			plugin.log.info("Mob removed from spawner at:" + mobSpawner.getX() + "," + mobSpawner.getY() + "," + mobSpawner.getZ());
			plugin.log.info("Spawner Mob Count at:" + mobSpawner.getX() + "," + mobSpawner.getY() + "," + mobSpawner.getZ() + " is: " + mobList.size());
			
		}
		
	}
	
	
}
