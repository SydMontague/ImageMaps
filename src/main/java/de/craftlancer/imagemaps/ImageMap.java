package de.craftlancer.imagemaps;

public class ImageMap
{
    private String image;
    private int x;
    private int y;
    
    public ImageMap(String image, int x, int y)
    {
        this.image = image;
        this.x = x;
        this.y = y;
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
}
