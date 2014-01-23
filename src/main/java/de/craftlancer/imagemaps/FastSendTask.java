package de.craftlancer.imagemaps;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FastSendTask extends BukkitRunnable implements Listener
{
    private Map<UUID, Integer> status = new HashMap<UUID, Integer>();
    private final ImageMaps plugin;
    private final int mapsPerRun;
    
    public FastSendTask(ImageMaps plugin, int mapsPerSend)
    {
        this.plugin = plugin;
        this.mapsPerRun = mapsPerSend;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void run()
    {
        if (plugin.getFastSendList().isEmpty())
            return;
        
        for (Player p : plugin.getServer().getOnlinePlayers())
        {
            int state = getStatus(p);
            
            if (state >= plugin.getFastSendList().size())
                continue;
            
            int i = mapsPerRun;
            
            do
            {
                p.sendMap(plugin.getServer().getMap(plugin.getFastSendList().get(state)));
                state++;
            }
            while (--i > 0 && state < plugin.getFastSendList().size());
            
            status.put(p.getUniqueId(), state);
        }
    }
    
    private int getStatus(Player p)
    {
        if(!status.containsKey(p.getUniqueId()))
            status.put(p.getUniqueId(), 0);
        
        return status.get(p.getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        status.put(e.getPlayer().getUniqueId(), 0);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        status.remove(e.getPlayer().getUniqueId());
    }
}
