package de.craftlancer.imagemaps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

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
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import de.craftlancer.imagemaps.metrics.Metrics;

public class ImageMaps extends JavaPlugin implements Listener
{
    private Map<String, String> placing = new HashMap<String, String>();
    private Map<Short, ImageMap> maps = new HashMap<Short, ImageMap>();
    private Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();
    
    @Override
    public void onEnable()
    {
        if (!new File(getDataFolder(), "images").exists())
            new File(getDataFolder(), "images").mkdirs();
        
        loadMaps();
        getCommand("imagemap").setExecutor(new ImageMapCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
        
        try
        {
            Metrics metrics = new Metrics(this);
            metrics.start();
        }
        catch (IOException e)
        {
            getLogger().severe("Failed to load Metrics!");
        }
    }
    
    @Override
    public void onDisable()
    {
        saveMaps();
    }
    
    public void startPlacing(Player p, String image)
    {
        placing.put(p.getName(), image);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e)
    {
        if (!e.hasBlock())
            return;
        
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        int xMod = 0;
        int zMod = 0;
        
        switch (e.getBlockFace())
        {
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
                return;
        }
        
        if (!placing.containsKey(e.getPlayer().getName()))
            return;
        
        BufferedImage image;
        try
        {
            image = ImageIO.read(new File(getDataFolder(), "images" + File.separatorChar + placing.get(e.getPlayer().getName())));
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            return;
        }
        
        Block b = e.getClickedBlock().getRelative(e.getBlockFace());
        
        int width = (int) Math.ceil((double) image.getWidth() / (double) 128);
        int height = (int) Math.ceil((double) image.getHeight() / (double) 128);
        
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (!e.getClickedBlock().getRelative(x * xMod, -y, x * zMod).getType().isSolid())
                {
                    e.getPlayer().sendMessage("There is not enough space for this image.");
                    return;
                }
        
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                setItemFrame(b.getRelative(x * xMod, -y, x * zMod), placing.get(e.getPlayer().getName()), e.getBlockFace(), x * 128, y * 128);
        
        placing.remove(e.getPlayer().getName());
    }
    
    @SuppressWarnings("deprecation")
    private void setItemFrame(Block bb, String image, BlockFace face, int x, int y)
    {
        BufferedImage bimage = loadImage(image);
        
        if (bimage == null)
        {
            getLogger().severe("Someone tried to create an image with an invalid file!");
            return;
        }
        
        bb.setType(Material.AIR);
        ItemFrame i = bb.getWorld().spawn(bb.getRelative(face.getOppositeFace()).getLocation(), ItemFrame.class);
        i.teleport(bb.getLocation());
        i.setFacingDirection(face, true);
        
        MapView map = getServer().createMap(bb.getWorld());
        for (MapRenderer r : map.getRenderers())
            map.removeRenderer(r);
        
        ItemStack item = new ItemStack(Material.MAP);
        item.setDurability(map.getId());
        
        i.setItem(item);
        
        map.addRenderer(new ImageMapRenderer(bimage, x, y));
        maps.put(map.getId(), new ImageMap(image, x, y));
    }
    
    @SuppressWarnings("deprecation")
    public void loadMaps()
    {
        File file = new File(getDataFolder(), "maps.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (String key : config.getKeys(false))
        {
            short id = Short.parseShort(key);
            
            MapView map = getServer().getMap(id);
            
            for (MapRenderer r : map.getRenderers())
                map.removeRenderer(r);
            
            String image = config.getString(key + ".image");
            int x = config.getInt(key + ".x");
            int y = config.getInt(key + ".y");
            
            BufferedImage bimage = loadImage(image);
            
            if (bimage == null)
            {
                getLogger().warning("Image file image not found, removing this map!");
                return;
            }
            
            map.addRenderer(new ImageMapRenderer(loadImage(image), x, y));
            maps.put(id, new ImageMap(image, x, y));
        }
    }
    
    private BufferedImage loadImage(String file)
    {
        if (images.containsKey(file))
            return images.get(file);
        
        File f = new File(getDataFolder(), "images" + File.separatorChar + file);
        BufferedImage image = null;
        
        try
        {
            image = ImageIO.read(f);
            images.put(file, image);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return image;
    }
    
    public void saveMaps()
    {
        File file = new File(getDataFolder(), "maps.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (Entry<Short, ImageMap> e : maps.entrySet())
        {
            config.set(e.getKey() + ".image", e.getValue().getImage());
            config.set(e.getKey() + ".x", e.getValue().getX());
            config.set(e.getKey() + ".y", e.getValue().getY());
        }
        
        try
        {
            config.save(file);
        }
        catch (IOException e1)
        {
            getLogger().severe("Failed to save maps.yml!");
            e1.printStackTrace();
        }
    }
}
