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
        recalculateInput(image, x1, y1);
    }
    
    public void recalculateInput(BufferedImage input, int x1, int y1)
    {
        int x2 = ImageMaps.MAP_WIDTH;
        int y2 = ImageMaps.MAP_HEIGHT;
        
        if (x1 > input.getWidth() || y1 > input.getHeight())
            return;
        
        if (x1 + x2 >= input.getWidth())
            x2 = input.getWidth() - x1;
        
        if (y1 + y2 >= input.getHeight())
            y2 = input.getHeight() - y1;
        
        this.image = input.getSubimage(x1, y1, x2, y2);
        
        first = true;
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
