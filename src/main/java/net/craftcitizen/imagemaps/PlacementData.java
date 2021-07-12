package net.craftcitizen.imagemaps;

import de.craftlancer.core.util.Tuple;

/**
 * Data associated with placing an image.
 */
public class PlacementData {

    private final String filename;
    private final boolean isInvisible;
    private final boolean isFixed;
    private final boolean isGlowing;
    private final Tuple<Integer, Integer> scale;

    public PlacementData(String filename, boolean isInvisible, boolean isFixed, boolean isGlowing, Tuple<Integer, Integer> scale) {
        this.filename = filename;
        this.isInvisible = isInvisible;
        this.isFixed = isFixed;
        this.isGlowing = isGlowing;
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
     * <p>
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
     * <p>
     * Only supported in 1.16 or higher!
     *
     * @return whether the placed frames will be invisible or not
     */
    public boolean isInvisible() {
        return isInvisible;
    }


    /**
     * Whether the placed item frame will be a glowing one.
     * <p>
     * Only supported in 1.17 or higher!
     *
     * @return whether the placed frames will be a glowing one
     */
    public boolean isGlowing() {
        return isGlowing;
    }

    /**
     * The <b>requested</b> size of the image. The actual size might be smaller
     * since the plugin won't modify aspect ratios.
     * <p>
     * Values of -1 stand for the default value of an unscaled map.
     *
     * @return the requested size of the image
     */
    public Tuple<Integer, Integer> getSize() {
        return scale;
    }
}
