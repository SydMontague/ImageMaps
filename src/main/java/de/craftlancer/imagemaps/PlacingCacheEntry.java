package de.craftlancer.imagemaps;

public class PlacingCacheEntry
{
    private final String image;
    private final boolean fastsend;
    private final double scale;
    
    public PlacingCacheEntry(String image, boolean fastsend, double scale)
    {
        this.image = image;
        this.fastsend = fastsend;
        this.scale = scale;
    }
    
    public String getImage()
    {
        return image;
    }
    
    public boolean isFastSend()
    {
        return fastsend;
    }
    
    public double getScale() {
        return scale;
    }
}
