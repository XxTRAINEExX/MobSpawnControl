package net.yeticraft.xxtraineexx.mobspawncontrol;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class MSCMob {

	public static Entity mobEntity; // Mob Entity associated with this UUID
	public static Block mobSpawner; // mobSpawner UUID associated with this Mob
	
	public MSCMob(Entity incEntity, Block incMobSpawner){
		
		mobEntity = incEntity;
		mobSpawner = incMobSpawner;
		
	}
	
	public Entity getMobEntity(){
		return mobEntity;
	}
	
	public Block getMobSpawner(){
		return mobSpawner;
	}
		
	public void setMobEntity(Entity incEntity){
		
		mobEntity = incEntity;
		
	}
	
	public void setMobSpawner(Block incMobSpawner){
		mobSpawner = incMobSpawner;

	}
	
	
}
