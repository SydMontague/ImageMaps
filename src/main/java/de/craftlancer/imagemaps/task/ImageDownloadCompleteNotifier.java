/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.craftlancer.imagemaps.task;

import java.util.Iterator;
import java.util.List;

import de.craftlancer.imagemaps.ImageMaps;
import de.craftlancer.imagemaps.service.MapService;
import de.craftlancer.imagemaps.service.impl.MapServiceImpl;
import de.craftlancer.imagemaps.task.ImageDownloadTask;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author gbl
 */
public class ImageDownloadCompleteNotifier extends BukkitRunnable {

    private MapService mapService = MapServiceImpl.getInstance();

    private ImageMaps plugin;
    
    public ImageDownloadCompleteNotifier(ImageMaps plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        List<ImageDownloadTask> tasks = mapService.getDownloadTasks();
        
        Iterator<ImageDownloadTask> itr = tasks.iterator();
        while(itr.hasNext()) {
            ImageDownloadTask task = itr.next();
            
            if(task.isDone()) {
                itr.remove();
                task.getSender().sendMessage("Download " + task.getURL() + ": " + task.getResult());
            }
        }
    }
}
