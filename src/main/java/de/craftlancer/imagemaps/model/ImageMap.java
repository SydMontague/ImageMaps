package de.craftlancer.imagemaps.model;

public class ImageMap
{
    private String image;
    private int x;
    private int y;
    private boolean fastsend;
    private double scale;
    
    public ImageMap(String image, int x, int y, boolean fastsend, double scale)
    {
        this.image = image;
        this.x = x;
        this.y = y;
        this.fastsend = fastsend;
        this.scale = scale;
    }
    
    public String getImage()
    {
        return image;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public boolean isFastSend()
    {
        return fastsend;
    }
    
    public double getScale() 
    {
        return scale;
    }
    
    public boolean isSimilar(String file, int x2, int y2, double d)
    {
        if (!getImage().equalsIgnoreCase(file))
            return false;
        if (getX() != x2)
            return false;
        if (getY() != y2)
            return false;
        
        double diff = d - getScale();
        return (diff > -0.0001 && diff < 0.0001);
    }
}
