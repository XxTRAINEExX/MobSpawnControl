package net.yeticraft.xxtraineexx.mobspawncontrol;

import java.util.logging.Logger;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author XxTRAINEExX
 * This is the main class for the plugin. We initialize the listener, load the config, 
 * and set a few global variables.
 *
 */
public class MobSpawnControl extends JavaPlugin {

	public final Logger log = Logger.getLogger("Minecraft");
	public String prefix = "[MobSpawnControl] ";
	public FileConfiguration config;
	public MSCListener myListener;
	public int spawnsAllowed;
	public int reportSize;
	public boolean pluginEnable;
	public boolean debug;
	public boolean oneTimeUse;
	public double warnThreshold;
	public double alertThreshold;
	public int spawnerRadiusX;
	public int spawnerRadiusY;
	public int spawnerRadiusZ;
	public double playerDistance;

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

	/**
	 * Config loading method.
	 */
	public void loadMainConfig() {

		// Read the config file
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();


		// Assign all the local variables
		spawnsAllowed = config.getInt("spawnsAllowed");
		reportSize = config.getInt("reportSize");
		pluginEnable = config.getBoolean("pluginEnable");
		debug = config.getBoolean("debug");
		oneTimeUse = config.getBoolean("oneTimeUse");
		warnThreshold = config.getDouble("warnThreshold");
		alertThreshold = config.getDouble("alertThreshold");
		spawnerRadiusX = config.getInt("spawnerRadiusX");
		spawnerRadiusY = config.getInt("spawnerRadiusY");
		spawnerRadiusZ = config.getInt("spawnerRadiusZ");
		playerDistance = config.getDouble("playerDistance");

		log.info(prefix + "Config loaded.");
		if (debug) {
			log.info(prefix + "[spawnsAllowed: " + spawnsAllowed + "] ");
			log.info(prefix + "[reportSize: " + reportSize + "]");
			log.info(prefix + "[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info(prefix + "[debug: " + String.valueOf(debug) + "]");
			log.info(prefix + "[oneTimeUse: " + String.valueOf(oneTimeUse) + "]");
			log.info(prefix + "[warnThreshold: " + warnThreshold + "] ");
			log.info(prefix + "[alertThreshold: " + alertThreshold + "]");
			log.info(prefix + "[spawnerRadiusX: " + spawnerRadiusX + "]");
			log.info(prefix + "[spawnerRadiusY: " + spawnerRadiusY + "]");
			log.info(prefix + "[spawnerRadiusZ: " + spawnerRadiusZ + "]");
			log.info(prefix + "[playerDistance: " + playerDistance + "]");
		}

	}

	/**
	 * Config saving method.
	 */
	public void saveMainConfig() {

		config.set("spawnsAllowed", spawnsAllowed);
		config.set("reportSize", reportSize);
		config.set("pluginEnable", pluginEnable);
		config.set("debug", debug);
		config.set("oneTimeUse", oneTimeUse);
		config.set("warnThreshold", warnThreshold);
		config.set("alertThreshold", alertThreshold);
		config.set("spawnerRadiusX", spawnerRadiusX);
		config.set("spawnerRadiusY", spawnerRadiusY);
		config.set("spawnerRadiusZ", spawnerRadiusZ);
		config.set("playerDistance", playerDistance);

		saveConfig();
		log.info(prefix + "Config saved.");
		if (debug) {
			log.info(prefix + "[spawnsAllowed: " + spawnsAllowed + "] ");
			log.info(prefix + "[reportSize: " + reportSize + "]");
			log.info(prefix + "[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info(prefix + "[debug: " + String.valueOf(debug) + "]");
			log.info(prefix + "[oneTimeUse: " + String.valueOf(oneTimeUse) + "]");
			log.info(prefix + "[warnThreshold: " + warnThreshold + "] ");
			log.info(prefix + "[alertThreshold: " + alertThreshold + "]");
			log.info(prefix + "[spawnerRadiusX: " + spawnerRadiusX + "]");
			log.info(prefix + "[spawnerRadiusY: " + spawnerRadiusY + "]");
			log.info(prefix + "[spawnerRadiusZ: " + spawnerRadiusZ + "]");
			log.info(prefix + "[playerDistance: " + playerDistance + "]");
		}

	}
}
