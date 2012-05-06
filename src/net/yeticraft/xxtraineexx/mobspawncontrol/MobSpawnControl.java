package net.yeticraft.xxtraineexx.mobspawncontrol;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MobSpawnControl extends JavaPlugin{

	public final Logger log = Logger.getLogger("Minecraft");
	public String prefix = "[MobSpawnControl] ";
	public FileConfiguration config;
	public int spawnsAllowed;
	public int reportSize;
	public MSCListener myListener;
	
	
	public void onEnable() {
		
		myListener = new MSCListener(this);
		PluginDescriptionFile pdffile = this.getDescription();
		loadMainConfig();
		CommandExecutor MSCCommandExecutor = new MSCCommand(this);
		getCommand("mobspawncontrol").setExecutor(MSCCommandExecutor);
    	getCommand("msc").setExecutor(MSCCommandExecutor);  	
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
    	reportSize = config.getInt("reportSize");
    	
    	if (spawnsAllowed==0 || reportSize ==0)
    	{
    		// Config file must be empty... lets generate a new one.
    		log.info(prefix + "Configuration File not found or error found in file. Generating default.");
    		config.set("spawnsAllowed", (int)80);
    		config.set("reportSize", (int)10);
    		saveConfig();
    	}
    	else{
    		log.info(prefix + "Existing Configuration file found, loading."); 
    	}
    	
    	spawnsAllowed = config.getInt("spawnsAllowed");
    	reportSize = config.getInt("reportSize");
    	
	}

}
