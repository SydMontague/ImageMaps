package net.craftcitizen.imagemaps;

import de.craftlancer.core.LambdaRunnable;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageMapRenderer extends MapRenderer {
    private ImageMaps plugin;

    private BufferedImage image = null;
    private boolean first = true;

    private final int x;
    private final int y;
    private final double scale;

    public ImageMapRenderer(ImageMaps plugin, BufferedImage image, int x, int y, double scale) {
        this.plugin = plugin;
        this.x = x;
        this.y = y;
        this.scale = scale;
        recalculateInput(image);
    }

    public void recalculateInput(BufferedImage input) {
        if (x * ImageMaps.MAP_WIDTH > Math.round(input.getWidth() * scale) || y * ImageMaps.MAP_HEIGHT > Math.round(input.getHeight() * scale))
            return;

        int x1 = (int) Math.floor(x * ImageMaps.MAP_WIDTH / scale);
        int y1 = (int) Math.floor(y * ImageMaps.MAP_HEIGHT / scale);

        int x2 = (int) Math.ceil(Math.min(input.getWidth(), ((x + 1) * ImageMaps.MAP_WIDTH / scale)));
        int y2 = (int) Math.ceil(Math.min(input.getHeight(), ((y + 1) * ImageMaps.MAP_HEIGHT / scale)));

        if (x2 - x1 <= 0 || y2 - y1 <= 0)
            return;

        this.image = input.getSubimage(x1, y1, x2 - x1, y2 - y1);

        if (scale != 1D) {
            BufferedImage resized = new BufferedImage(ImageMaps.MAP_WIDTH, ImageMaps.MAP_HEIGHT, input.getType() == 0 ? image.getType() : input.getType());
            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            this.image = scaleOp.filter(this.image, resized);
        }

        first = true;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (image != null && first) {
            new LambdaRunnable(() -> {
                @SuppressWarnings("deprecation")
                byte[] imageData = MapPalette.imageToBytes(image);

                new LambdaRunnable(() -> {
                    for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
                        for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                            canvas.setPixel(x2, y2, imageData[y2 * image.getWidth(null) + x2]);
                        }
                    }
                }).runTaskLater(plugin, System.nanoTime() % 20);
                // spread out pseudo randomly in a very naive way

            }).runTaskAsynchronously(plugin);
            first = false;
        }
    }

}
