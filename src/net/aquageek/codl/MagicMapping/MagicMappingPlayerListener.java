package net.aquageek.codl.MagicMapping;

import org.bukkit.Chunk;
import org.bukkit.Location;
//import org.bukkit.entity.Player;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author codl
 */
public class MagicMappingPlayerListener extends PlayerListener {
//    private final MagicMapping plugin;
    private MagicMappingRenderer renderer;

    public MagicMappingPlayerListener(final MagicMapping plugin, MagicMappingRenderer renderer) {
//        this.plugin = plugin;
        this.renderer = renderer;
    }
    
    @Override
    public void onPlayerMove(PlayerMoveEvent e){
    	//Player player = e.getPlayer();
    	Location l = e.getFrom();
    	int x = l.getBlockX();
    	int z = l.getBlockZ();
    	Chunk chunk = l.getWorld().getChunkAt(l.getWorld().getBlockAt(x, 5, z));
    	int chunkx = chunk.getX();
    	int chunkz = chunk.getZ();
        renderer.slowEnqueue(chunkx, chunkz);
    }
}

