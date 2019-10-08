package de.craftlancer.imagemaps.services;

import de.craftlancer.imagemaps.ImageDownloadTask;
import de.craftlancer.imagemaps.PlacingCacheEntry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

public interface MapService {

    void startPlacing(Player p, String image, boolean fastsend, double scale);

    boolean placeImage(Block block, BlockFace face, PlacingCacheEntry cache);

    void setItemFrame(Block bb, BufferedImage image, BlockFace face, int x, int y, PlacingCacheEntry cache);

    @SuppressWarnings("deprecation")
    ItemStack getMapItem(String file, int x, int y, BufferedImage image, double scale);

    BufferedImage loadImage(String file);

    void loadMaps();

    void saveMaps();

    @SuppressWarnings("deprecation")
    void reloadImage(String file);

    void appendDownloadTask(ImageDownloadTask task);

    List<ImageDownloadTask> getDownloadTasks();

    boolean isPlacing(UUID uuid);


    void removePlacing(UUID uuid);

    PlacingCacheEntry retreivePlacingCache(UUID uuid);

    List<Integer> getFastSendList();
}
