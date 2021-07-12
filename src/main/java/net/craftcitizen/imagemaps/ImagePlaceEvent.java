package net.craftcitizen.imagemaps;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an image is attempted to be placed.
 */
public class ImagePlaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Block block;
    private final BlockFace widthDirection;
    private final BlockFace heightDirection;
    private final int width;
    private final int height;
    private final PlacementData cache;

    private boolean cancelled;

    public ImagePlaceEvent(Player player, Block block, BlockFace widthDirection, BlockFace heightDirection, int width, int height, PlacementData cache) {
        this.player = player;
        this.block = block;
        this.widthDirection = widthDirection;
        this.heightDirection = heightDirection;
        this.width = width;
        this.height = height;
        this.cache = cache;
    }

    /**
     * The player attempting to place the image
     *
     * @return the player attempting to place the image
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * The initial block the image is placed against.
     *
     * @return the initial block the image is placed against
     */
    public Block getBlock() {
        return block;
    }

    /**
     * The direction in which maps are placed in the height direction of the image.
     *
     * @return the height direction of the map placement
     */
    public BlockFace getHeightDirection() {
        return heightDirection;
    }

    /**
     * The direction in which maps are placed in the width direction of the image.
     *
     * @return the width direction of the map placement
     */
    public BlockFace getWidthDirection() {
        return widthDirection;
    }

    /**
     * The width of the image in maps
     *
     * @return the width of the image in maps
     */
    public int getWidth() {
        return width;
    }

    /**
     * The height of the image in maps
     *
     * @return the height of the image in maps
     */
    public int getHeight() {
        return height;
    }

    /**
     * The placement data used to place the image
     *
     * @return the placement data
     */
    public PlacementData getCacheEntry() {
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
