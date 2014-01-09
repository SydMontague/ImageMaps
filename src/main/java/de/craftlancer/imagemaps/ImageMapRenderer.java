package de.craftlancer.imagemaps;

import java.awt.Image;
import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMapRenderer extends MapRenderer
{
    private Image image = null;
    private boolean first = true;
    
    public ImageMapRenderer(BufferedImage image, int x1, int y1)
    {
        int x2 = 128;
        int y2 = 128;
        
        if (x1 > image.getWidth() || y1 > image.getHeight())
            return;
        
        if (x1 + x2 >= image.getWidth())
            x2 = image.getWidth() - x1;
        
        if (y1 + y2 >= image.getHeight())
            y2 = image.getHeight() - y1;
        
        this.image = image.getSubimage(x1, y1, x2, y2);
    }
    
    @Override
    public void render(MapView view, MapCanvas canvas, Player player)
    {
        if (image != null && first)
        {
            canvas.drawImage(0, 0, image);
            first = false;
        }
    }
    
}
