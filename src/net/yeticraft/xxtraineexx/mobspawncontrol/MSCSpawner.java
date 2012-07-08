package net.yeticraft.xxtraineexx.mobspawncontrol;

import java.util.Set;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author XxTRAINEExX
 * This class holds necessary information about our tracked spawner. We currently track
 * mobUUID (Entity.getUniqueID()) and the player object attached to this spawner. 
 *
 */
public class MSCSpawner {

	Player player; // Player associated with this spawner
	Set<UUID> mobList; // Set of Entity UUIDs associated with this spawner (Monsters)
	Block block;
	int temp_counter; // Counter to keep lists consistent for printing

	public MSCSpawner(Player incPlayer, Set<UUID> incMobList, Block incBlock) {
		player = incPlayer;
		mobList = incMobList;
		block = incBlock;
	}

	/**
	 * 
	 * @return Bukkit player object associated with this spawner
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * 
	 * @return Java HashSet Set<UUID> containing all Mob UUIDs associated with this spawner
	 */
	public Set<UUID> getMobList() {
		return mobList;
	}

	/**
	 * Sets the Player associated with this spawner.
	 * @param Bukkit Player object
	 * 
	 */
	public void setPlayer(Player incPlayer) {
		player = incPlayer;
	}

	/**
	 * Sets the Java HashSet Set<UUID> containing all Mob UUIDs associated with this spawner
	 * @param Java HashSet Set<UUID> containing all Mob UUIDs associated with this spawner
	 */
	public void setMobList(Set<UUID> incMobList) {
		mobList = incMobList;
	}

	/**
	 * Sets the Block where this spawner is located
	 * @param inblock The block associated with the spawner
	 */
	public void setBlock(Block inblock) {
		block = inblock;
	}

	/**
	 * Returns the block where this spawner is located
	 * @return The block where this spawner is located
	 */
	public Block getBlock() {
		return block;
	}
}
