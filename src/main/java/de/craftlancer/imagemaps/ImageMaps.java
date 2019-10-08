package de.craftlancer.imagemaps;

import de.craftlancer.imagemaps.command.ImageMapCommand;
import de.craftlancer.imagemaps.event.PlayerInteractions;
import de.craftlancer.imagemaps.service.MapService;
import de.craftlancer.imagemaps.service.impl.MapServiceImpl;
import de.craftlancer.imagemaps.task.FastSendTask;
import de.craftlancer.imagemaps.task.ImageDownloadCompleteNotifier;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class ImageMaps extends JavaPlugin {
    public static final int MAP_WIDTH = 128;
    public static final int MAP_HEIGHT = 128;
    public static final String IMAGES_DIR = "images";
    private MapService mapService = MapServiceImpl.getInstance();
    private FastSendTask sendTask;


    @Override
    public void onEnable() {
        MapServiceImpl.initInstance(this);
        if (!new File(getDataFolder(), IMAGES_DIR).exists()) {
            boolean result = new File(getDataFolder(), IMAGES_DIR).mkdirs();
            if (!result) {
                getLogger().log(Level.WARNING, "The folder has not been created. Please verify that the application has the right to do that!");
            }
        }

        int sendPerTicks = getConfig().getInt("sendPerTicks", 20);
        int mapsPerSend = getConfig().getInt("mapsPerSend", 8);

        mapService.loadMaps();
        getCommand("imagemap").setExecutor(new ImageMapCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerInteractions(), this);
        sendTask = new FastSendTask(this, mapsPerSend);
        getServer().getPluginManager().registerEvents(sendTask, this);
        sendTask.runTaskTimer(this, sendPerTicks, sendPerTicks);
        new ImageDownloadCompleteNotifier(this).runTaskTimer(this, 20, 20);
    }

    @Override
    public void onDisable() {
        mapService.saveMaps();
        getServer().getScheduler().cancelTasks(this);
    }

    public FastSendTask getSendTask() {
        return sendTask;
    }

}
