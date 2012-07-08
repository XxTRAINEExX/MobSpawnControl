package net.yeticraft.xxtraineexx.mobspawncontrol;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * @author XxTRAINEExX
 * This class holds necessary information about our tracked mobs. We currently track
 * the Entity object associated with this mobs UUID and the spawner associated with
 * this mob. 
 *
 */
public class MSCMob {

	Entity mobEntity; // Mob Entity associated with this UUID
	Block mobSpawner; // mobSpawner UUID associated with this Mob

	public MSCMob(Entity incEntity, Block incMobSpawner) {
		mobEntity = incEntity;
		mobSpawner = incMobSpawner;
	}

	/**
	 * 
	 * @return Bukkit Entity object associated with this mobs UUID
	 */
	public Entity getMobEntity() {
		return mobEntity;
	}

	/**
	 * 
	 * @return Bukkit Block object representing this mobs spawner
	 */
	public Block getMobSpawner() {
		return mobSpawner;
	}

	/**
	 * Sets the Entity object associated with this classes UUID
	 * @param Bukkit Entity Object
	 */
	public void setMobEntity(Entity incEntity) {
		mobEntity = incEntity;
	}

	/**
	 * Sets the Block object referencing the spawner this mob  originated from
	 * @param Bukkit block object
	 */
	public void setMobSpawner(Block incMobSpawner) {
		mobSpawner = incMobSpawner;
	}
}
