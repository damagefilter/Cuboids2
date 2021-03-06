package net.playblack.cuboids.generators;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Vector3D;
import net.playblack.cuboids.SessionManager;
import net.playblack.cuboids.exceptions.BlockEditLimitExceededException;
import net.playblack.cuboids.exceptions.SelectionIncompleteException;
import net.playblack.cuboids.history.HistoryObject;
import net.playblack.cuboids.selections.CuboidSelection;

/**
 * Generate walls along the line of a cuboid selection
 *
 * @author Chris
 */
public class SphereGenerator extends BaseGen {

    private boolean fill;
    private int radius;
    private BlockType material;

    /**
     * The selection you pass along here will be written into the world!
     *
     * @param selection
     * @param world
     */
    public SphereGenerator(CuboidSelection selection, World world) {
        super(selection, world);
    }

    /**
     * Set the material of the sphere
     *
     * @param block
     */
    public void setMaterial(BlockType block) {
        this.material = block;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Set fill true to make a filled sphere, false to make it hollow(rly...)
     */
    public void setHollow(boolean sleepy) {
        fill = sleepy;
    }

    private void createSphere() {
        Vector3D center = selection.getOrigin();
        if (selection.getOffset() == null) {
            // A little work around to evaluate true
            // when modifyWorld is issued as it needs the selection to be
            // complete
            selection.setOffset(center);
        }
        int Xmin = center.getBlockX() - radius;
        int Xmax = center.getBlockX() + radius;
        int Ymin = center.getBlockY() - radius;
        int Ymax = center.getBlockY() + radius;
        int Zmin = center.getBlockZ() - radius;
        int Zmax = center.getBlockZ() + radius;

        synchronized (lock) {
            for (int x = Xmin; x <= Xmax; x++) {
                for (int y = Ymin; y <= Ymax; y++) {
                    for (int z = Zmin; z <= Zmax; z++) {

                        double diff = Math.sqrt(Math.pow(x - center.getX(), 2.0D) + Math.pow(y - center.getY(), 2.0D) + Math.pow(z - center.getZ(), 2.0D));
                        if (diff < radius + 0.5 && (fill || (!fill && diff > radius - 0.5))) {
                            selection.setBlock(new Vector3D(x, y, z), material);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean execute(Player player, boolean newHistory) throws BlockEditLimitExceededException, SelectionIncompleteException {
        selection.clearBlocks();
        createSphere();
        CuboidSelection world = scanWorld(true, false);

        if (newHistory) {
            SessionManager.get().getPlayerHistory(player.getName()).remember(new HistoryObject(world, selection));
        }
        return modifyWorld(false);
    }
}
