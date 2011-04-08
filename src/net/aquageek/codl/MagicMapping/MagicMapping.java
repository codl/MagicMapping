package net.aquageek.codl.MagicMapping;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.Date;
import java.util.Properties;

//import javax.imageio.ImageIO;
//import java.util.HashMap;
//import org.bukkit.entity.Player;
//import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.Event;
//import org.bukkit.plugin.PluginDescriptionFile;
//import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;


/**
 * MagicMapping for Bukkit
 *
 * @author codl
 */
public class MagicMapping extends JavaPlugin {
	private MagicMappingRenderer renderer = new MagicMappingRenderer(this);
    private final MagicMappingPlayerListener playerListener = new MagicMappingPlayerListener(this, renderer);
    private final MagicMappingBlockListener blockListener = new MagicMappingBlockListener(this, renderer);
    public World world;
    public String output = null;

    public void onEnable() {
    	
        PluginManager pm = getServer().getPluginManager();

        this.world = this.getServer().getWorlds().get(0);
		try {
			Properties props = new Properties();
			
			props.load(new FileReader("server.properties"));
			output = props.getProperty("magicmapping-output");
			
		} catch (IOException e) {
			System.out.println("MagicMapping : Could not read server.properties");
		}
		if (output == null) {
			output = ".";
			System.out.println("MagicMapping : Could not find magicmapping-output in server.properties, all images will be written to the current directory");
			System.out.println("MagicMapping : You should set magicmapping-output to the location of the web folder in the archive");
		}
		try {
			FileWriter recent = new FileWriter(output+File.separator+"recent", false);
			recent.write("0 0 0\n");
			recent.close();
			new File(output+File.separator+"chunks").mkdirs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        new Thread(renderer).start();
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Monitor, this);
        //pm.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Event.Priority.Monitor, this); //Lava & water flow
        //pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Event.Priority.Monitor, this);
        
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Monitor, this);
    	System.out.println("MagicMapping on");
    	/*
    	for (int x=-5; x < 5; x++) {
        	for (int z=-5; z < 5; z++) {
        		renderer.enqueue(x, z);
        	}
    	}
    	*/
    }
    public void onDisable() {

    }
 /*   public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    } */
}

