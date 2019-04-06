package de.craftlancer.imagemaps;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMapRenderer extends MapRenderer
{
    private BufferedImage image = null;
    private boolean first = true;
    
    public ImageMapRenderer(BufferedImage image, int x1, int y1, double scale)
    {
        recalculateInput(image, x1, y1, scale);
    }
    
    public void recalculateInput(BufferedImage input, int x1, int y1, double scale)
    {
        int x2 = ImageMaps.MAP_WIDTH;
        int y2 = ImageMaps.MAP_HEIGHT;
        
        if (x1 > input.getWidth()* scale + 0.001 || y1 > input.getHeight() * scale + 0.001)
            return;
        
        if (x1 + x2 >= input.getWidth() * scale)
            x2 = (int)(input.getWidth() * scale) - x1;
        
        if (y1 + y2 >= input.getHeight() * scale)
            y2 = (int)(input.getHeight() * scale) - y1;

        this.image = input.getSubimage((int)(x1/scale), (int)(y1/scale), (int)(x2/scale), (int)(y2/scale));
        if (scale != 1.0) {
            BufferedImage resized = new BufferedImage(ImageMaps.MAP_WIDTH, ImageMaps.MAP_HEIGHT, input.getType());
            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            AffineTransformOp scaleOp =  new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            this.image = scaleOp.filter(this.image, resized);
        }
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
