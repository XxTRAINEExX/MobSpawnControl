package net.yeticraft.xxtraineexx.mobspawncontrol;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class MSCListener implements Listener{

	public static MobSpawnControl plugin;
	HashMap<Block, Set<Entity>> spawnerSet = new HashMap<Block, Set<Entity>>();
	Set<Entity> mobsLinkedToSpawner = new HashSet<Entity>();

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
		if (!e.getSpawnReason().toString().equalsIgnoreCase("SPAWNER")){return;}
		
		// Find the spawner this monster came from.
		Block spawnedMob = e.getLocation().getBlock();
		Block currentBlock = null;
		Block mobSpawner = null;
		
		
		// Mobs can only spawn within a 8x3x8 area
		int lowerX = spawnedMob.getX() - 8;
		int upperX = spawnedMob.getX() + 8;
		int lowerY = spawnedMob.getY() - 3;
		int upperY = spawnedMob.getY() + 3;
		int lowerZ = spawnedMob.getZ() - 8;
		int upperZ = spawnedMob.getZ() + 8;
		boolean finished = false;
		
		// Searching all nearby blocks to find the spawner
		for (int y = lowerY; y <= upperY || finished; y++){
			for (int x = lowerX; x <= upperX || finished; x++){
				for (int z = lowerZ; z <= upperZ; z++){
					
					currentBlock = e.getLocation().getWorld().getBlockAt(x, y, z);
					if (currentBlock.getTypeId() == 52){
						
						// Found your ass...
						mobSpawner = currentBlock;
						finished = true;
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

		// Lets create a new Hashset to store the mobs associated with a spawner
		Set<Entity> mobList = new HashSet<Entity>();
		
		// If the spawner is NOT in the hashmap we will add the monster to the new mobList and add the spawner/mobList to the spawnerSet hashmap
		if (!spawnerSet.containsKey(mobSpawner)){
			mobList.add(e.getEntity());
			mobsLinkedToSpawner.add(e.getEntity());
			spawnerSet.put(mobSpawner, mobList);
			plugin.log.info("New spawner found.  Adding to hashset / hashmap.");
			e.setCancelled(false);
			return;
		}
		
		// Looks like the mobSpawner is already in the spawnerSet. Lets check to see if this set has reached its limit
		mobList = spawnerSet.get(mobSpawner);
		if (mobList.size() > plugin.spawnsAllowed){
			plugin.log.info("Spawner maximum reached: ["+ mobList.size() + "] Stopping additional spawns");
			e.setCancelled(true);
			return;
		}
		
		// Looks like the current mobSpawner is not at its maximum. Let's increment.
		mobList.add(e.getEntity());
		mobsLinkedToSpawner.add(e.getEntity());
		plugin.log.info("Spawner currently contains [" + mobList.size() + "] monsters. Allowing spawn.");
		e.setCancelled(false);
		return;
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e) {
		
		if (mobsLinkedToSpawner.contains(e.getEntity())){
			mobsLinkedToSpawner.remove(e.getEntity());
		}
		
	}
	
	
}
