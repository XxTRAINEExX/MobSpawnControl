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

	@Override
	public void onEnable() {
		myListener = new MSCListener(this);
		loadMainConfig();
		CommandExecutor MSCCommandExecutor = new MSCCommand(this);
		getCommand("mobspawncontrol").setExecutor(MSCCommandExecutor);
		getCommand("msc").setExecutor(MSCCommandExecutor);
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

		final Logger log = getLogger();
		log.info("Config loaded.");
		if (debug) {
			log.info("[spawnsAllowed: " + spawnsAllowed + "] ");
			log.info("[reportSize: " + reportSize + "]");
			log.info("[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info("[debug: " + String.valueOf(debug) + "]");
			log.info("[oneTimeUse: " + String.valueOf(oneTimeUse) + "]");
			log.info("[warnThreshold: " + warnThreshold + "] ");
			log.info("[alertThreshold: " + alertThreshold + "]");
			log.info("[spawnerRadiusX: " + spawnerRadiusX + "]");
			log.info("[spawnerRadiusY: " + spawnerRadiusY + "]");
			log.info("[spawnerRadiusZ: " + spawnerRadiusZ + "]");
			log.info("[playerDistance: " + playerDistance + "]");
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

		final Logger log = getLogger();
		log.info("Config saved.");
		if (debug) {
			log.info("[spawnsAllowed: " + spawnsAllowed + "] ");
			log.info("[reportSize: " + reportSize + "]");
			log.info("[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info("[debug: " + String.valueOf(debug) + "]");
			log.info("[oneTimeUse: " + String.valueOf(oneTimeUse) + "]");
			log.info("[warnThreshold: " + warnThreshold + "] ");
			log.info("[alertThreshold: " + alertThreshold + "]");
			log.info("[spawnerRadiusX: " + spawnerRadiusX + "]");
			log.info("[spawnerRadiusY: " + spawnerRadiusY + "]");
			log.info("[spawnerRadiusZ: " + spawnerRadiusZ + "]");
			log.info("[playerDistance: " + playerDistance + "]");
		}
	}
}
