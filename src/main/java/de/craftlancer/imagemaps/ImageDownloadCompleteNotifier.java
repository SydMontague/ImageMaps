/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.craftlancer.imagemaps;

import java.util.Iterator;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author gbl
 */
public class ImageDownloadCompleteNotifier extends BukkitRunnable {

    private ImageMaps plugin;
    
    public ImageDownloadCompleteNotifier(ImageMaps plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        List<ImageDownloadTask> tasks = plugin.getDownloadTasks();
        
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
