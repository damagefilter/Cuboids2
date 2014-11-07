package net.playblack.cuboids.selections;

import net.canarymod.api.world.blocks.BlockType;
import net.playblack.mcutils.Vector;

import java.util.LinkedHashMap;

/**
 * Interface for a generic selection
 *
 * @author Chris
 */
public interface ISelection {

    /**
     * Get the world for this selection
     *
     * @return
     */
    public String getWorld();

    /**
     * Set the world for this selection
     *
     * @param world
     */
    public void setWorld(String world);

    /**
     * Empty the block list
     */
    public void clearBlocks();

    /**
     * Set a block in this selection
     *
     * @param v
     * @param b
     */
    public void setBlock(Vector v, BlockType b);

    /**
     * Get a block from the specified coordinate storage in this selection. May
     * return null
     *
     * @param v
     * @return
     */
    public BlockType getBlock(Vector v);

    /**
     * Get the whole block list from this selection
     *
     * @return
     */
    public LinkedHashMap<Vector, BlockType> getBlockList();

    /**
     * Override the whole block list from this selection
     *
     * @param newList
     */
    public void setBlockList(LinkedHashMap<Vector, BlockType> newList);

    /**
     * Get the size of this selection, that is: number of stored blocks
     *
     * @return
     */
    public long getSize();

    /**
     * Get the boundary size. That is: the volume of this selection (number of
     * blocks in the world that are encompassed by this selection)
     *
     * @return
     */
    public long getBoundarySize();
}
