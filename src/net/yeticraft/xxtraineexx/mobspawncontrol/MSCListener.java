package net.yeticraft.xxtraineexx.mobspawncontrol;

import java.util.*;

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
import org.bukkit.event.world.ChunkLoadEvent;

public class MSCListener implements Listener {

	private final MobSpawnControl plugin;
	Map<Block, MSCSpawner> activeSpawners = new HashMap<Block, MSCSpawner>();
	Map<UUID, MSCMob> activeMobs = new HashMap<UUID, MSCMob>();

	public MSCListener(MobSpawnControl plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (!plugin.pluginEnable) {
			return; // Plugin has been manually disabled
		}

		// If this didn't come from a spawner, return out.
		if (!e.getSpawnReason().toString().equalsIgnoreCase("SPAWNER")) {
			return;
		}

		// Find the spawner this monster came from.
		Block spawnedMobLoc = e.getLocation().getBlock();
		Block currentBlock;
		Block mobSpawner = null;
		UUID spawnedMobUUID = e.getEntity().getUniqueId();
		Entity spawnedMob = e.getEntity();
		Player player = null;

		// Mobs can only spawn within a 8x3x8 area
		int lowerX = spawnedMobLoc.getX() - plugin.spawnerRadiusX;
		int upperX = spawnedMobLoc.getX() + plugin.spawnerRadiusX;
		int lowerY = spawnedMobLoc.getY() - plugin.spawnerRadiusY;
		int upperY = spawnedMobLoc.getY() + plugin.spawnerRadiusY;
		int lowerZ = spawnedMobLoc.getZ() - plugin.spawnerRadiusZ;
		int upperZ = spawnedMobLoc.getZ() + plugin.spawnerRadiusZ;
		boolean keepLooping = true;



		// Searching all nearby blocks to find the spawner
		for (int y = lowerY; y <= upperY && keepLooping; y++) {
			for (int x = lowerX; x <= upperX && keepLooping; x++) {
				for (int z = lowerZ; z <= upperZ; z++) {
					currentBlock = e.getLocation().getWorld().getBlockAt(x, y, z);
					if (currentBlock.getTypeId() == 52) {
						mobSpawner = currentBlock;
						keepLooping = false;
						break;
					}
				}
			}
		}


		// If mobSpawner is still null we must have missed the spawner somehow.
		if (mobSpawner == null) {
			plugin.getLogger().info("Spawner not found for spawned creature at: " + spawnedMob.getLocation().toString());
			return;
		}

		// Checking for nearby players
		for (Player nearby : Bukkit.getServer().getOnlinePlayers()) {

			if (!nearby.getWorld().equals(mobSpawner.getWorld())) {
				continue; // Bypassing player that is not in the same world as the spawner
			}
			double nearbyDistance = nearby.getLocation().distance(mobSpawner.getLocation());
			if (nearbyDistance <= plugin.playerDistance) {
				player = nearby;
				break;
			}
		}

		// In case we didn't find a nearby Player.. we should leave.
		if (player == null) {
			if (plugin.debug) {
				plugin.getLogger().info("No Players found around the spawner. Process halted.");
			}
			return;
		}

		// Lets create a Hashset to store the mobs associated with a spawner
		Set<UUID> mobList;

		// If the spawner is NOT in the hashmap we need to add this spawner to the hashmap and add this mob to the active mobs
		if (!activeSpawners.containsKey(mobSpawner)) {
			mobList = new HashSet<UUID>();
			mobList.add(spawnedMobUUID);
			activeSpawners.put(mobSpawner, new MSCSpawner(player, mobList, mobSpawner));
			activeMobs.put(spawnedMobUUID, new MSCMob(spawnedMob, mobSpawner));

			e.setCancelled(false);
			if (plugin.debug) {
				plugin.getLogger().info("NEW Spawner: " + mobSpawner.getLocation().toString() + " Owner: [" + player.getName() + "] Mob: [" + spawnedMob.getType().getName() + "] Spawn Count: [" + mobList.size() + "]");
			}
			return;
		}

		// Looks like the mobSpawner is already in the spawnerSet. 
		mobList = activeSpawners.get(mobSpawner).getMobList();

		// Before we see if the mobList has reached it's limit, we should make sure none of the mob's have despawned.
		Iterator<UUID> it = mobList.iterator();
		int despawnedMobs = 0;
		while (it.hasNext()) {

			UUID mobUUID = it.next();
			if (activeMobs.get(mobUUID).getMobEntity().isDead()) {
				activeMobs.remove(mobUUID);
				despawnedMobs++;
				it.remove();

			}
		}
		if (plugin.debug) {
			plugin.getLogger().info("Removed [" + despawnedMobs + "] despawned Mobs in spawner: " + mobSpawner.getLocation().toString());
		}

		// Lets check to see if this set has reached its limit
		if (mobList.size() >= plugin.spawnsAllowed) {
			if (plugin.debug) {
				plugin.getLogger().info("FULL Spawner: " + mobSpawner.getLocation().toString() + " Owner: [" + player.getName() + "] Mob: [" + spawnedMob.getType().getName() + "] Spawn Count: [" + mobList.size() + "]");
			}
			e.setCancelled(true);
			return;
		}

		// Looks like the current mobSpawner is not at its maximum. Let's increment.
		mobList.add(spawnedMobUUID);
		activeSpawners.get(mobSpawner).setPlayer(player);
		activeMobs.put(spawnedMobUUID, new MSCMob(spawnedMob, mobSpawner));
		if (plugin.debug) {
			plugin.getLogger().info("EXISTING Spawner: " + mobSpawner.getLocation().toString() + " Owner: [" + player.getName() + "] Mob: [" + spawnedMob.getType().getName() + "] Spawn Count: [" + mobList.size() + "]");
		}
		e.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e) {

		if (!plugin.pluginEnable) {
			return; // Plugin has been manually disabled
		}
		UUID deadMobUUID = e.getEntity().getUniqueId();

		if (activeMobs.containsKey(deadMobUUID)) {

			// Finding the spawner this entity is attached to
			Block mobSpawner = activeMobs.get(deadMobUUID).getMobSpawner();

			// Finding the MobList associated with this spawner
			Set<UUID> mobList = activeSpawners.get(mobSpawner).getMobList();

			// Removing this mob from the mobSet, spawnList, and UUID map
			mobList.remove(deadMobUUID);
			activeMobs.remove(deadMobUUID);

			if (plugin.debug) {
				plugin.getLogger().info("MOB removed from Spawner: " + mobSpawner.getLocation().toString() + " Mob: [" + e.getEntity().getType().getName() + "] Spawn Count: [" + mobList.size() + "]");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChunkLoadEvent(ChunkLoadEvent e) {

		if (!plugin.pluginEnable) {
			return; // Plugin has been manually disabled
		}
		// Code to keep track of mobs that were in a previously unloaded chunk
		Chunk loadingChunk = e.getChunk();
		int attachedMobs = 0;

		for (Entity loadingMob : loadingChunk.getEntities()) {
			if (activeMobs.containsKey(loadingMob.getUniqueId())) {
				// Setting their new entity object in the hashmap so we can use it later.
				activeMobs.get(loadingMob.getUniqueId()).setMobEntity(loadingMob);
				attachedMobs++;
			}
		}

		if (plugin.debug && attachedMobs > 0) {
			plugin.getLogger().info(attachedMobs + " spawner attached mobs were processed in LOADING chunk: ." + loadingChunk.toString());
		}
	}
}
