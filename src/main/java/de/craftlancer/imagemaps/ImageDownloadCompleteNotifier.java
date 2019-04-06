/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.craftlancer.imagemaps;

import java.util.List;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author gbl
 */
public class ImageDownloadCompleteNotifier extends BukkitRunnable implements Listener {

    private ImageMaps plugin;
    
    public ImageDownloadCompleteNotifier(ImageMaps plugin) {
        this.plugin=plugin;
    }
    
    @Override
    public void run() {
        List<ImageDownloadTask> tasks = plugin.getDownloadTasks();
        for (ImageDownloadTask task: tasks) {
            if (task.isDone()) {
                tasks.remove(task);
                try {
                    task.getSender().sendMessage("Download "+task.getURL()+": "+task.getResult());
                } catch (Exception e) {
                    // ignore it, the sender might have logged out
                }
                return; // one message per tick, and no ConcurrentModificationExceptions
            }
        }
    }
}
