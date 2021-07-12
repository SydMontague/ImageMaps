package net.craftcitizen.imagemaps;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("ImageMaps.Map")
public class ImageMap implements ConfigurationSerializable {

    private String filename;
    private int x;
    private int y;
    private double scale;

    public ImageMap(String filename, int x, int y, double scale) {
        this.filename = filename;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public ImageMap(Map<?, ?> map) {
        this.filename = map.get("image").toString();
        this.x = (Integer) map.get("x");
        this.y = (Integer) map.get("y");
        this.scale = (Double) map.get("scale");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("image", filename);
        map.put("x", x);
        map.put("y", y);
        map.put("scale", scale);

        return map;
    }

    public String getFilename() {
        return filename;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getScale() {
        return scale;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        long temp;
        temp = Double.doubleToLongBits(scale);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ImageMap))
            return false;
        ImageMap other = (ImageMap) obj;
        if (filename == null) {
            if (other.filename != null)
                return false;
        } else if (!filename.equals(other.filename))
            return false;
        if (Double.doubleToLongBits(scale) != Double.doubleToLongBits(other.scale))
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
