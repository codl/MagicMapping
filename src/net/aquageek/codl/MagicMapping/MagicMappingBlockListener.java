package net.aquageek.codl.MagicMapping;

import org.bukkit.Chunk;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * MagicMapping block listener
 * @author codl
 */
public class MagicMappingBlockListener extends BlockListener {
    @SuppressWarnings("unused")
	private final MagicMapping plugin;
    private MagicMappingRenderer renderer;

    public MagicMappingBlockListener(final MagicMapping plugin, MagicMappingRenderer renderer) {
        this.plugin = plugin;
        this.renderer = renderer;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
    	Chunk chunk = event.getBlockPlaced().getChunk();
    	int chunkx = chunk.getX();
    	int chunkz = chunk.getZ();
    	renderer.enqueue(chunkx, chunkz);
    }
    
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
    	Chunk chunk = event.getBlock().getChunk();
    	int chunkx = chunk.getX();
    	int chunkz = chunk.getZ();
    	renderer.enqueue(chunkx, chunkz);
    }
    
    @Override
    public void onBlockPhysics(BlockPhysicsEvent event){
    	/*Chunk chunk = event.getBlock().getChunk();
    	int chunkx = chunk.getX();
    	int chunkz = chunk.getZ();
    	renderer.slowEnqueue(chunkx, chunkz);
    	*/	
    }
}
