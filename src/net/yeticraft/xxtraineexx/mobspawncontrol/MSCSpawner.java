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

	private String player; // Player associated with this spawner
	private Set<UUID> mobList; // Set of Entity UUIDs associated with this spawner (Monsters)
	private Block block;
	public int temp_counter; // Counter to keep lists consistent for printing

	public MSCSpawner(String incPlayer, Set<UUID> incMobList, Block incBlock) {
		player = incPlayer;
		mobList = incMobList;
		block = incBlock;
	}

	/**
	 * @return Name of the player associated with this spawner
	 */
	public String getPlayerName() {
		return player;
	}

	/**
	 * @return A {@link Set}&lt;{@link UUID}&gt; containing all Mob UUIDs associated with this spawner
	 */
	public Set<UUID> getMobList() {
		return mobList;
	}

	/**
	 * Sets the player associated with this spawner.
	 * @param incPlayer Player name
	 * 
	 */
	public void setPlayerName(String incPlayer) {
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
