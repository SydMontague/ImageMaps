package de.craftlancer.imagemaps;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ImagePlaceEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Block block;
    private final BlockFace face;
    private final int width;
    private final int height;
    private final PlacingCacheEntry cache;
    
    private boolean cancelled;
    
    public ImagePlaceEvent(Player player, Block block, BlockFace face, int width, int height, PlacingCacheEntry cache) {
        this.player = player;
        this.block = block;
        this.face = face;
        this.width = width;
        this.height = height;
        this.cache = cache;
    }

    public Player getPlayer() {
        return player;
    }
    
    public Block getBlock() {
        return block;
    }
    
    public BlockFace getFace() {
        return face;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public PlacingCacheEntry getCacheEntry() {
        return cache;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
