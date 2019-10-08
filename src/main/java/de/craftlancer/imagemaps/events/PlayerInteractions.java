package de.craftlancer.imagemaps.events;

import de.craftlancer.imagemaps.services.MapService;
import de.craftlancer.imagemaps.services.impl.MapServiceImpl;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractions implements Listener {
    private MapService mapService = MapServiceImpl.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        if (!mapService.isPlacing(e.getPlayer().getUniqueId()))
            return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            e.getPlayer().sendMessage("Placing cancelled");
            mapService.removePlacing(e.getPlayer().getUniqueId());
            return;
        }

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!mapService.placeImage(e.getClickedBlock(), e.getBlockFace(), mapService.retreivePlacingCache(e.getPlayer().getUniqueId())))
            e.getPlayer().sendMessage(ChatColor.RED + "Can't place the image here!\nMake sure the area is large enough, unobstructed and without pre-existing hanging entities.");
        else
            mapService.saveMaps();

        e.setCancelled(true);
        mapService.removePlacing(e.getPlayer().getUniqueId());

    }

}
