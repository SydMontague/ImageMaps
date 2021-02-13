package net.craftcitizen.imagemaps;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMapRenderer extends MapRenderer {
    private BufferedImage image = null;
    private boolean first = true;
    
    private final int x;
    private final int y;
    private final double scale;
    
    public ImageMapRenderer(BufferedImage image, int x, int y, double scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        recalculateInput(image);
    }
    
    public void recalculateInput(BufferedImage input) {
        first = true;
        
        if (x * ImageMaps.MAP_WIDTH > Math.round(input.getWidth() * scale) || y * ImageMaps.MAP_HEIGHT > Math.round(input.getHeight() * scale))
            return;
        
        int x1 = (int) Math.round(x * ImageMaps.MAP_WIDTH / scale);
        int y1 = (int) Math.round(y * ImageMaps.MAP_HEIGHT / scale);
        
        int x2 = (int) Math.round(Math.min(input.getWidth(), ((x + 1) * ImageMaps.MAP_WIDTH / scale)));
        int y2 = (int) Math.round(Math.min(input.getHeight(), ((y + 1) * ImageMaps.MAP_HEIGHT / scale)));
        
        if(x2 - x1 <= 0 || y2 - y1 <= 0)
            return;
        
        this.image = input.getSubimage(x1, y1, x2 - x1, y2 - y1);
        
        if (scale != 1D) {
            BufferedImage resized = new BufferedImage(ImageMaps.MAP_WIDTH, ImageMaps.MAP_HEIGHT, input.getType());
            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            this.image = scaleOp.filter(this.image, resized);
        }
    }
    
    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (image != null && first) {
            canvas.drawImage(0, 0, image);
            first = false;
        }
    }
}
