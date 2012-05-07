package net.yeticraft.xxtraineexx.mobspawncontrol;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class MSCListener implements Listener{

	public static MobSpawnControl plugin;
	HashMap<Block, Set<UUID>> spawnerSet = new HashMap<Block, Set<UUID>>(); // Stores the spawner block and all mob UUIDs associated with this spawner
	HashMap<UUID, Block> mobSet = new HashMap<UUID, Block>(); // Stores mob UUID and spawner associated with that mob
	HashMap<Block, Player> spawnOwners = new HashMap<Block, Player>(); // Stores spawner and player associated with that spawner

	// The following hashmap only exists to track despawned mobs. There is no method within bukkit to identify a despawned mob.
	// To fix this, we will call the entity object associated with the UUID and check isDead(). If the mob is despawned it should
	// return TRUE.
	HashMap<UUID, Entity> mobUUIDMap = new HashMap<UUID, Entity>(); // Stores UUID of each mob and their matching entity object.


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
		
		// Find the spawner this monster came from.
		Block spawnedMobLoc = e.getLocation().getBlock();
		Block currentBlock = null;
		Block mobSpawner = null;
		UUID spawnedMobUUID = e.getEntity().getUniqueId();
		Player player = null;
		
		// Using the following map to keep track of mobs and their respective UUID / chunks. This is necessary due to chunk unloads
		// which destroy the entity object but retain the UUID.
		mobUUIDMap.put(spawnedMobUUID, e.getEntity());
		
		// Mobs can only spawn within a 8x3x8 area
		int lowerX = spawnedMobLoc.getX() - 7;
		int upperX = spawnedMobLoc.getX() + 7;
		int lowerY = spawnedMobLoc.getY() - 2;
		int upperY = spawnedMobLoc.getY() + 2;
		int lowerZ = spawnedMobLoc.getZ() - 7;
		int upperZ = spawnedMobLoc.getZ() + 7;
		boolean keepLooping = true;
		
		// Searching all nearby blocks to find the spawner
		for (int y = lowerY; y <= upperY && keepLooping; y++){
			for (int x = lowerX; x <= upperX && keepLooping; x++){
				for (int z = lowerZ; z <= upperZ; z++){
					
					currentBlock = e.getLocation().getWorld().getBlockAt(x, y, z);
					if (currentBlock.getTypeId() == 52){
						mobSpawner = currentBlock;
						keepLooping = false;
						break;
					}
					
				}
			}
		}
				
		
		// If mobSpawner is still null we must have missed the spawner somehow.
		if (mobSpawner == null){
			plugin.log.info(plugin.prefix + "Spawner not found for spawned creature at: " + e.getEntity().getLocation().toString());
			return;
		}
		
		// Checking for nearby players
		for (Player nearby : Bukkit.getServer().getOnlinePlayers()) {	
			double nearbyDistance = nearby.getLocation().distance(mobSpawner.getLocation());
			if (nearbyDistance <= 17){
				player = nearby;
				break;
			}
			
		}

		// Assigning this spawner to the player.
		spawnOwners.put(mobSpawner, player);
			
		// Lets create a new Hashset to store the mobs associated with a spawner
		Set<UUID> mobList = new HashSet<UUID>();
		
		// If the spawner is NOT in the hashmap we will add the monster to the new mobList and add the spawner/mobList to the spawnerSet hashmap
		if (!spawnerSet.containsKey(mobSpawner)){
			mobList.add(spawnedMobUUID);
			mobSet.put(spawnedMobUUID, mobSpawner);
			spawnerSet.put(mobSpawner, mobList);
			e.setCancelled(false);
			if (plugin.debug){ plugin.log.info(plugin.prefix + "NEW Spawner: " + mobSpawner.getLocation().toString() + " Owner: [" + player.getName() + "] Mob: [" + e.getEntity().getType().getName() + "] Spawn Count: [" + mobList.size() + "]");}
			return;
		}
		
		// Looks like the mobSpawner is already in the spawnerSet. 
		mobList = spawnerSet.get(mobSpawner);
		
		// Before we see if the mobList has reached it's limit, we should make sure none of the mob's have despawned.
		Iterator<UUID> it = mobList.iterator();
		int despawnedMobs = 0;
		while(it.hasNext()) {

			UUID mobUUID = it.next();
			if (mobUUIDMap.get(mobUUID).isDead()){
				mobList.remove(mobUUID);
				despawnedMobs++;
			}
		}
		if (plugin.debug){ plugin.log.info(plugin.prefix + "Removed [" + despawnedMobs + "] despawned Mobs in spawner: " + mobSpawner.getLocation().toString());}
		
		// Lets check to see if this set has reached its limit
		if (mobList.size() >= plugin.spawnsAllowed){
			plugin.log.info("Spawner maximum reached: " + player.getName() + " [" + mobList.size() + "] "  + mobSpawner.getLocation().toString());
			if (plugin.debug){ plugin.log.info(plugin.prefix + "FULL Spawner: " + mobSpawner.getLocation().toString() + " Owner: [" + player.getName() + "] Mob: [" + e.getEntity().getType().getName() + "] Spawn Count: [" + mobList.size() + "]");}
			e.setCancelled(true);
			return;
		}
		
		// Looks like the current mobSpawner is not at its maximum. Let's increment.
		mobList.add(spawnedMobUUID);
		mobSet.put(spawnedMobUUID, mobSpawner);
		if (plugin.debug){ plugin.log.info(plugin.prefix + "EXISTING Spawner: " + mobSpawner.getLocation().toString() + " Owner: [" + player.getName() + "] Mob: [" + e.getEntity().getType().getName() + "] Spawn Count: [" + mobList.size() + "]");}
		e.setCancelled(false);
		return;
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e) {
		
		UUID deadMobUUID = e.getEntity().getUniqueId();
		
		if (mobSet.containsKey(deadMobUUID)){
			
			// Finding the spawner this entity is attached to
			Block mobSpawner = mobSet.get(deadMobUUID);
			
			// Finding the MobList associated with this spawner
			Set<UUID> mobList = spawnerSet.get(mobSpawner);
			
			// Removing this mob from the mobSet, spawnList, and UUID map
			mobList.remove(deadMobUUID);
			mobSet.remove(deadMobUUID);
			mobUUIDMap.remove(deadMobUUID);
			
			if (plugin.debug){ plugin.log.info(plugin.prefix + "MOB removed from Spawner: " + mobSpawner.getLocation().toString() + " Mob: [" + e.getEntity().getType().getName() + "] Spawn Count: [" + mobList.size() + "]");}
				
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChunkUnloadEvent(ChunkUnloadEvent e) {
		
		// Code to keep track of mobs in a chunk that is about to be unloaded
		Chunk unloadingChunk = e.getChunk();
		int attachedMobs = 0;
		
		for (Entity unloadingMob : unloadingChunk.getEntities()) {	
			
			if (mobUUIDMap.containsKey(unloadingMob.getUniqueId())){
			
				// Setting their Entity object to NULL so we know they've been popped by the server
				mobUUIDMap.put(unloadingMob.getUniqueId(), null);
				attachedMobs++;
				
			}
			
		}
		
		if (plugin.debug && attachedMobs > 0){ plugin.log.info(plugin.prefix + attachedMobs + " spawner attached mobs were processed in UN-LOADING chunk: ." + unloadingChunk.toString());}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChunkLoadEvent(ChunkLoadEvent e) {
		
		// Code to keep track of mobs that were in a previously unloaded chunk
		Chunk loadingChunk = e.getChunk();
				
		int attachedMobs = 0;
				
		for (Entity unloadingMob : loadingChunk.getEntities()) {	
					
			if (mobUUIDMap.containsKey(unloadingMob.getUniqueId())){
					
				// Setting their new entity object in the hashmap so we can use it later.
				mobUUIDMap.put(unloadingMob.getUniqueId(), unloadingMob);
				attachedMobs++;
						
			}
					
		}
					
		if (plugin.debug && attachedMobs > 0){ plugin.log.info(plugin.prefix + attachedMobs + " spawner attached mobs were processed in LOADING chunk: ." + loadingChunk.toString());}
		
		
	}
	
}
