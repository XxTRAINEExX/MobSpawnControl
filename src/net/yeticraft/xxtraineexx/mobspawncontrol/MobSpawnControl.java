package net.yeticraft.xxtraineexx.mobspawncontrol;

import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MobSpawnControl extends JavaPlugin{

	public final Logger log = Logger.getLogger("Minecraft");
	public String prefix = "[MobSpawnControl] ";
	public FileConfiguration config;
	public int spawnsAllowed;
	public MSCListener myListener;
	
	
	public void onEnable() {
		
		myListener = new MSCListener(this);
		PluginDescriptionFile pdffile = this.getDescription();
		loadMainConfig();
    	log.info(prefix + " " + pdffile.getVersion() + " Enabled"); 	
    	
	}
	
	public void onDisable() {
		PluginDescriptionFile pdffile = this.getDescription(); 
		log.info(prefix + " " + pdffile.getVersion() + " Disabled"); 
	}
	
	
	public void loadMainConfig(){
		// Read the config file
    	config = getConfig();
    	spawnsAllowed = config.getInt("spawnsAllowed");
    	
    	if (spawnsAllowed==0)
    	{
    		// Config file must be empty... lets generate a new one.
    		log.info(prefix + "Configuration File not found. Generating default.");
    		config.set("spawnsAllowed", (int)10);
    		saveConfig();
    	}
    	else{
    		log.info(prefix + "Existing Configuration file found, loading."); 
    	}
    	
	}

}
