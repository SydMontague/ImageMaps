package de.craftlancer.imagemaps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public class ImageMaps extends JavaPlugin implements Listener {
    public static final int MAP_WIDTH = 128;
    public static final int MAP_HEIGHT = 128;
    
    private static final String IMAGES_DIR = "images";
    
    private Map<String, PlacingCacheEntry> placing = new HashMap<>();
    private Map<Integer, ImageMap> maps = new HashMap<>();
    private Map<String, BufferedImage> images = new HashMap<>();
    private List<Integer> sendList = new ArrayList<>();
    private FastSendTask sendTask;
    private List<ImageDownloadTask> downloadTasks;
    
    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), IMAGES_DIR).exists())
            new File(getDataFolder(), IMAGES_DIR).mkdirs();
        
        int sendPerTicks = getConfig().getInt("sendPerTicks", 20);
        int mapsPerSend = getConfig().getInt("mapsPerSend", 8);
        
        loadMaps();
        getCommand("imagemap").setExecutor(new ImageMapCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
        sendTask = new FastSendTask(this, mapsPerSend);
        getServer().getPluginManager().registerEvents(sendTask, this);
        sendTask.runTaskTimer(this, sendPerTicks, sendPerTicks);
        downloadTasks=new ArrayList<>();
        new ImageDownloadCompleteNotifier(this).runTaskTimer(this, 20, 20);
    }
    
    @Override
    public void onDisable() {
        saveMaps();
        getServer().getScheduler().cancelTasks(this);
    }
    
    public List<Integer> getFastSendList() {
        return sendList;
    }
    
    public void startPlacing(Player p, String image, boolean fastsend, double scale) {
        placing.put(p.getName(), new PlacingCacheEntry(image, fastsend, scale));
    }
    
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
                getLogger().severe("Someone tried to create an image with an invalid block facing");
                return false;
        }
        
        BufferedImage image = loadImage(cache.getImage());
        
        if (image == null) {
            getLogger().severe("Someone tried to create an image with an invalid file!");
            return false;
        }
        
        Block b = block.getRelative(face);
        
        int width = (int) Math.ceil((double) image.getWidth() / (double) MAP_WIDTH * cache.getScale() - 0.0001);
        int height = (int) Math.ceil((double) image.getHeight() / (double) MAP_HEIGHT  * cache.getScale() - 0.0001);
        
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
                    setItemFrame(b.getRelative(x * xMod, -y, x * zMod), image, face, x * MAP_WIDTH, y * MAP_HEIGHT, cache);
        }
        catch (IllegalArgumentException e) {
            // God forgive me, but I actually HAVE to catch this...
            getLogger().info("Some error occured while placing the ItemFrames. This can for example happen when some existing ItemFrame/Hanging Entity is blocking.");
            getLogger().info("Unfortunatly this is caused be the way Minecraft/CraftBukkit handles the spawning of Entities.");
            return false;
        }
        
        return true;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        if (!placing.containsKey(e.getPlayer().getName()))
            return;

        if (!e.hasBlock()) {
            e.getPlayer().sendMessage("Placing cancelled");
            placing.remove(e.getPlayer().getName());
            return;
        }
        
        if (!placeImage(e.getClickedBlock(), e.getBlockFace(), placing.get(e.getPlayer().getName())))
            e.getPlayer().sendMessage(ChatColor.RED + "Can't place the image here!\nMake sure the area is large enough, unobstructed and without pre-existing hanging entities.");
        else
            saveMaps();
        e.setCancelled(true);
        placing.remove(e.getPlayer().getName());
        
    }
    
    private void setItemFrame(Block bb, BufferedImage image, BlockFace face, int x, int y, PlacingCacheEntry cache) {
        ItemFrame i = null;
        
        i = bb.getWorld().spawn(bb.getLocation(), ItemFrame.class);
        
        i.setFacingDirection(face, false);
        
        ItemStack item = getMapItem(cache.getImage(), x, y, image, cache.getScale());
        i.setItem(item);
        
        int id = ((MapMeta) item.getItemMeta()).getMapId();
        
        if (cache.isFastSend() && !sendList.contains(id)) {
            sendList.add(id);
            sendTask.addToQueue(id);
        }
        
        maps.put(id, new ImageMap(cache.getImage(), x, y, sendList.contains(id), cache.getScale()));
    }
    
    @SuppressWarnings("deprecation")
    private ItemStack getMapItem(String file, int x, int y, BufferedImage image, double scale) {
        ItemStack item = new ItemStack(Material.MAP);
        
        for (Entry<Integer, ImageMap> entry : maps.entrySet()) {
            if (entry.getValue().isSimilar(file, x, y, scale)) {
                MapMeta meta = (MapMeta) item.getItemMeta();
                meta.setMapId(entry.getKey());
                item.setItemMeta(meta);
                return item;
            }
        }

        MapView map = getServer().createMap(getServer().getWorlds().get(0));
        for (MapRenderer r : map.getRenderers())
            map.removeRenderer(r);
        
        map.addRenderer(new ImageMapRenderer(image, x, y, scale));

        MapMeta meta = ((MapMeta) item.getItemMeta());
        meta.setMapId(map.getId());
        item.setItemMeta(meta);
        
        return item;
    }
    
    public BufferedImage loadImage(String file) {
        if (images.containsKey(file))
            return images.get(file);
        
        File f = new File(getDataFolder(), IMAGES_DIR + File.separatorChar + file);
        BufferedImage image = null;
        
        if (!f.exists())
            return null;
        
        try {
            image = ImageIO.read(f);
            images.put(file, image);
        }
        catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error while trying to read image " + f.getName(), e);
        }
        
        return image;
    }
    
    @SuppressWarnings("deprecation")
    private void loadMaps() {
        File file = new File(getDataFolder(), "maps.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> warnedFilenames=new HashSet<>();
        
        for (String key : config.getKeys(false)) {
            int id = Integer.parseInt(key);
            
            MapView map = getServer().getMap(id);
            
            if(map == null)
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
                    getLogger().warning(() -> "Image file " + image + " not found, removing this map!");
                }
                continue;
            }
            
            if (fastsend)
                sendList.add(id);
            
            map.addRenderer(new ImageMapRenderer(loadImage(image), x, y, scale));
            maps.put(id, new ImageMap(image, x, y, fastsend, scale));
        }
    }
    
    private void saveMaps() {
        File file = new File(getDataFolder(), "maps.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (String key : config.getKeys(false))
            config.set(key, null);
        
        for (Entry<Integer, ImageMap> e : maps.entrySet()) {
            config.set(e.getKey() + ".image", e.getValue().getImage());
            config.set(e.getKey() + ".x", e.getValue().getX());
            config.set(e.getKey() + ".y", e.getValue().getY());
            config.set(e.getKey() + ".fastsend", e.getValue().isFastSend());
            config.set(e.getKey() + ".scale", e.getValue().getScale());
        }
        
        try {
            config.save(file);
        }
        catch (IOException e1) {
            getLogger().log(Level.SEVERE, "Failed to save maps.yml!", e1);
        }
    }
    
    @SuppressWarnings("deprecation")
    public void reloadImage(String file) {
        images.remove(file);
        BufferedImage image = loadImage(file);
        
        if(image == null) {
            getLogger().warning(() -> "Failed to reload image: " + file);
            return;
        }
        
        int width = (int) Math.ceil((double) image.getWidth() / (double) MAP_WIDTH);
        int height = (int) Math.ceil((double) image.getHeight() / (double) MAP_HEIGHT);
        
        for (Entry<Integer, ImageMap> entry : maps.entrySet()) {
            ImageMap imageMap = entry.getValue();
            if (imageMap.getImage().equals(file)) {
                int id=((MapMeta) getMapItem(file, imageMap.getX(), imageMap.getY(), image, imageMap.getScale()).getItemMeta()).getMapId();
                MapView map = getServer().getMap(id);
                for (MapRenderer renderer : map.getRenderers())
                    if (renderer instanceof ImageMapRenderer)
                        ((ImageMapRenderer) renderer).recalculateInput(image, imageMap.getX(), imageMap.getY(), imageMap.getScale());
                sendTask.addToQueue(id);
            }
        }
    }
    
    public void appendDownloadTask(ImageDownloadTask task) {
        this.downloadTasks.add(task);
    }
    
    public List<ImageDownloadTask> getDownloadTasks() {
        return this.downloadTasks;
    }
}
