package net.yeticraft.xxtraineexx.mobspawncontrol;


import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;

public class MSCSpawner {

	public static Player player; // Player associated with this spawner
	public static Set<UUID> mobList; // Set of Entity UUIDs associated with this spawner (Monsters)
	
	public MSCSpawner(Player incPlayer, Set<UUID> incMobList){
		
		player=incPlayer;
		mobList=incMobList;
		
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Set<UUID> getMobList(){
		return mobList;
	}
	
	
		
	public void setPlayer(Player incPlayer){
		
		player = incPlayer;
		
	}
	
	public void setMobList(Set<UUID> incMobList){
		mobList = incMobList;
	}
	
	
}
