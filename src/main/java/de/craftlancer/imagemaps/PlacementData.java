package de.craftlancer.imagemaps;

import de.craftlancer.core.util.Tuple;

/**
 * Data associated with placing an image.
 */
public class PlacementData {
    
    private final String filename;
    private final boolean isInvisible;
    private final boolean isFixed;
    private final Tuple<Integer, Integer> scale;
    
    public PlacementData(String filename, boolean isInvisible, boolean isFixed, Tuple<Integer, Integer> scale) {
        this.filename = filename;
        this.isInvisible = isInvisible;
        this.isFixed = isFixed;
        this.scale = scale;
    }
    
    /**
     * The file name of the image to be placed
     * 
     * @return the file name of the image
     */
    public String getFilename() {
        return filename;
    }
    
    /**
     * Whether the placed item frame will have the "fixed" property set.
     * A fixed frame can't be destroyed or modified by survival players.
     * 
     * Only supported in 1.16 or higher!
     * 
     * @return whether the placed frames will be fixed or not
     */
    public boolean isFixed() {
        return isFixed;
    }
    
    /**
     * Whether the placed item frame will have the "invisible" property set.
     * An invisible frame won't be rendered, leaving only the item/map visible.
     * 
     * Only supported in 1.16 or higher!
     * 
     * @return whether the placed frames will be invisible or not
     */
    public boolean isInvisible() {
        return isInvisible;
    }
    
    /**
     * The <b>requested</b> size of the image. The actual size might be smaller
     * since the plugin won't modify aspect ratios.
     * 
     * Values of -1 stand for the default value of an unscaled map.
     * 
     * @return the requested size of the image
     */
    public Tuple<Integer, Integer> getSize() {
        return scale;
    }
}
