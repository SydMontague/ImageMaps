package de.craftlancer.imagemaps;

public class PlacingCacheEntry
{
    private String image;
    private boolean fastsend;
    private double scale;
    
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
