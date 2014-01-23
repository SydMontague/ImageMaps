package de.craftlancer.imagemaps;

public class ImageMap
{
    private String image;
    private int x;
    private int y;
    private boolean fastsend;
    
    public ImageMap(String image, int x, int y, boolean fastsend)
    {
        this.image = image;
        this.x = x;
        this.y = y;
        this.fastsend = fastsend;
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
    
    public boolean isSimilar(String file, int x2, int y2)
    {
        if (!getImage().equalsIgnoreCase(file))
            return false;
        if (getX() != x2)
            return false;
        if (getY() != y2)
            return false;
        
        return true;
    }
}
