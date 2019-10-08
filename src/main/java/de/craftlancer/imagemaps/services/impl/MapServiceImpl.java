package de.craftlancer.imagemaps.services.impl;

import de.craftlancer.imagemaps.*;
import de.craftlancer.imagemaps.services.MapService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class MapServiceImpl implements MapService {

    private static MapServiceImpl instance;
    private static ImageMaps imageMaps;
    private Map<UUID, PlacingCacheEntry> placing = new HashMap<>();
    private Map<Integer, ImageMap> maps = new HashMap<>();
    private Map<String, BufferedImage> images = new HashMap<>();
    private List<Integer> sendList = new ArrayList<>();
    private List<ImageDownloadTask> downloadTasks = new ArrayList<>();

    private MapServiceImpl() {
        //  blank constructor to force the use of singleton pattern
    }


    public static MapServiceImpl getInstance() {
        if (instance == null) {
            instance = new MapServiceImpl();
        }
        return instance;
    }

    public static void initInstance(ImageMaps imageMaps) {
        if (instance == null) {
            instance = new MapServiceImpl();
        }
        if (imageMaps != null && MapServiceImpl.imageMaps == null) {
            MapServiceImpl.imageMaps = imageMaps;
        }
    }

    @Override
    public void startPlacing(Player p, String image, boolean fastsend, double scale) {
        placing.put(p.getUniqueId(), new PlacingCacheEntry(image, fastsend, scale));
    }

    @Override
    public boolean placeImage(Block block, BlockFace face, PlacingCacheEntry cache) {
        int xMod = 0;
        int zMod = 0;

        switch (face) {
            case EAST:
                zMod = -1;
                break;
            case WEST:
                zMod = 1;
                break;
            case SOUTH:
                xMod = 1;
                break;
            case NORTH:
                xMod = -1;
                break;
            default:
                imageMaps.getLogger().severe("Someone tried to create an image with an invalid block facing");
                return false;
        }

        BufferedImage image = loadImage(cache.getImage());

        if (image == null) {
            imageMaps.getLogger().severe("Someone tried to create an image with an invalid file!");
            return false;
        }

        Block b = block.getRelative(face);

        int width = (int) Math.ceil((double) image.getWidth() / (double) ImageMaps.MAP_WIDTH * cache.getScale() - 0.0001);
        int height = (int) Math.ceil((double) image.getHeight() / (double) ImageMaps.MAP_HEIGHT * cache.getScale() - 0.0001);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                if (!block.getRelative(x * xMod, -y, x * zMod).getType().isSolid())
                    return false;

                if (block.getRelative(x * xMod - zMod, -y, x * zMod + xMod).getType().isSolid())
                    return false;
            }

        try {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    setItemFrame(b.getRelative(x * xMod, -y, x * zMod), image, face, x * ImageMaps.MAP_WIDTH, y * ImageMaps.MAP_HEIGHT, cache);
        } catch (IllegalArgumentException e) {
            // God forgive me, but I actually HAVE to catch this...
            imageMaps.getLogger().info("Some error occured while placing the ItemFrames. This can for example happen when some existing ItemFrame/Hanging Entity is blocking.");
            imageMaps.getLogger().info("Unfortunatly this is caused be the way Minecraft/CraftBukkit handles the spawning of Entities.");
            return false;
        }

        return true;
    }


    @Override
    public void setItemFrame(Block bb, BufferedImage image, BlockFace face, int x, int y, PlacingCacheEntry cache) {
        ItemFrame i = null;

        i = bb.getWorld().spawn(bb.getLocation(), ItemFrame.class);

        i.setFacingDirection(face, false);

        ItemStack item = getMapItem(cache.getImage(), x, y, image, cache.getScale());
        i.setItem(item);

        int id = ((MapMeta) item.getItemMeta()).getMapId();

        if (cache.isFastSend() && !sendList.contains(id)) {
            sendList.add(id);
            imageMaps.getSendTask().addToQueue(id);
        }

        maps.put(id, new ImageMap(cache.getImage(), x, y, sendList.contains(id), cache.getScale()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getMapItem(String file, int x, int y, BufferedImage image, double scale) {
        ItemStack item = new ItemStack(Material.MAP);

        for (Map.Entry<Integer, ImageMap> entry : maps.entrySet()) {
            if (entry.getValue().isSimilar(file, x, y, scale)) {
                MapMeta meta = (MapMeta) item.getItemMeta();
                meta.setMapId(entry.getKey());
                item.setItemMeta(meta);
                return item;
            }
        }

        MapView map = imageMaps.getServer().createMap(imageMaps.getServer().getWorlds().get(0));
        for (MapRenderer r : map.getRenderers())
            map.removeRenderer(r);

        map.addRenderer(new ImageMapRenderer(image, x, y, scale));

        MapMeta meta = ((MapMeta) item.getItemMeta());
        meta.setMapId(map.getId());
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public BufferedImage loadImage(String file) {
        if (images.containsKey(file))
            return images.get(file);

        File f = new File(imageMaps.getDataFolder(), ImageMaps.IMAGES_DIR + File.separatorChar + file);
        BufferedImage image = null;

        if (!f.exists())
            return null;

        try {
            image = ImageIO.read(f);
            images.put(file, image);
        } catch (IOException e) {
            imageMaps.getLogger().log(Level.SEVERE, "Error while trying to read image " + f.getName(), e);
        }

        return image;
    }


    @Override
    public void loadMaps() {
        File file = new File(imageMaps.getDataFolder(), "maps.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> warnedFilenames = new HashSet<>();

        for (String key : config.getKeys(false)) {
            int id = Integer.parseInt(key);

            MapView map = imageMaps.getServer().getMap(id);

            if (map == null)
                continue;

            for (MapRenderer r : map.getRenderers())
                map.removeRenderer(r);

            String image = config.getString(key + ".image");
            int x = config.getInt(key + ".x");
            int y = config.getInt(key + ".y");
            boolean fastsend = config.getBoolean(key + ".fastsend", false);
            double scale = config.getDouble(key + ".scale", 1.0);

            BufferedImage bimage = loadImage(image);

            if (bimage == null) {
                if (!warnedFilenames.contains(image)) {
                    warnedFilenames.add(image);
                    imageMaps.getLogger().warning(() -> "Image file " + image + " not found, removing this map!");
                }
                continue;
            }

            if (fastsend)
                sendList.add(id);

            map.addRenderer(new ImageMapRenderer(loadImage(image), x, y, scale));
            maps.put(id, new ImageMap(image, x, y, fastsend, scale));
        }
    }

    @Override
    public void saveMaps() {
        File file = new File(imageMaps.getDataFolder(), "maps.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false))
            config.set(key, null);

        for (Map.Entry<Integer, ImageMap> e : maps.entrySet()) {
            config.set(e.getKey() + ".image", e.getValue().getImage());
            config.set(e.getKey() + ".x", e.getValue().getX());
            config.set(e.getKey() + ".y", e.getValue().getY());
            config.set(e.getKey() + ".fastsend", e.getValue().isFastSend());
            config.set(e.getKey() + ".scale", e.getValue().getScale());
        }

        try {
            config.save(file);
        } catch (IOException e1) {
            imageMaps.getLogger().log(Level.SEVERE, "Failed to save maps.yml!", e1);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void reloadImage(String file) {
        images.remove(file);
        BufferedImage image = loadImage(file);

        if (image == null) {
            imageMaps.getLogger().warning(() -> "Failed to reload image: " + file);
            return;
        }

        maps.values().stream().filter(a -> a.getImage().equals(file)).forEach(imageMap -> {
            int id = ((MapMeta) getMapItem(file, imageMap.getX(), imageMap.getY(), image, imageMap.getScale()).getItemMeta()).getMapId();
            MapView map = imageMaps.getServer().getMap(id);

            for (MapRenderer renderer : map.getRenderers())
                if (renderer instanceof ImageMapRenderer)
                    ((ImageMapRenderer) renderer).recalculateInput(image, imageMap.getX(), imageMap.getY(), imageMap.getScale());

            imageMaps.getSendTask().addToQueue(id);
        });
    }

    @Override
    public void appendDownloadTask(ImageDownloadTask task) {
        this.downloadTasks.add(task);
    }

    @Override
    public List<ImageDownloadTask> getDownloadTasks() {
        return this.downloadTasks;
    }

    @Override
    public boolean isPlacing(UUID uuid) {
        return placing.containsKey(uuid);
    }

    @Override
    public void removePlacing(UUID uuid) {
        placing.remove(uuid);
    }

    @Override
    public PlacingCacheEntry retreivePlacingCache(UUID uuid) {
        return placing.get(uuid);
    }

    @Override
    public List<Integer> getFastSendList() {
        return this.sendList;
    }
}
