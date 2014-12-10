package net.playblack.cuboids.commands;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.blocks.BlockType;
import net.playblack.cuboids.MessageSystem;
import net.playblack.cuboids.generators.SphereGenerator;
import net.playblack.cuboids.exceptions.BlockEditLimitExceededException;
import net.playblack.cuboids.exceptions.SelectionIncompleteException;
import net.playblack.cuboids.selections.CuboidSelection;
import net.playblack.cuboids.selections.SelectionManager;
import net.playblack.mcutils.ColorManager;
import net.playblack.mcutils.Debug;
import net.playblack.mcutils.ToolBox;

/**
 * Create a sphere or ball around a center point
 *
 * @author Chris
 */
public class Csphere extends CBaseCommand {

    public Csphere() {
        super("Create a sphere: " + ColorManager.Yellow + "/csphere <radius> <block>:[data] [hollow]", 3, 4);
    }

    @Override
    public void execute(Player player, String[] command) {
        if (parseCommand(player, command)) {
            return;
        }
        boolean fill = true;
        if (command.length == 4) {
            fill = false;
        }

        // create a new template block
        BlockType material = ToolBox.parseBlock(command[2]);
        if (material == null) {
            MessageSystem.failMessage(player, "invalidBlock");
            return;
        }
        int radius = ToolBox.parseInt(command[1]);
        if (radius == -1) {
            MessageSystem.failMessage(player, "invalidRadius");
            return;
        }
        // prepare the selection
        CuboidSelection template = SelectionManager.get().getPlayerSelection(player.getName());
        if (!template.getBlockList().isEmpty()) {
            template.clearBlocks();
        }
        if (template.getOrigin() == null) {
            MessageSystem.failMessage(player, "originNotSet");
            return;
        }

        // Create the block generator
        SphereGenerator gen = new SphereGenerator(template, player.getWorld());
        gen.setHollow(fill);
        gen.setMaterial(material);
        gen.setRadius(radius);

        try {
            if (gen.execute(player, true)) {
                MessageSystem.successMessage(player, "sphereCreated");
            }
            else {
                MessageSystem.failMessage(player, "sphereNotCreated");
                MessageSystem.failMessage(player, "selectionIncomplete");
            }
        }
        catch (BlockEditLimitExceededException e) {
            Debug.logWarning(e.getMessage());
            MessageSystem.customFailMessage(player, e.getMessage());
            e.printStackTrace();
        }
        catch (SelectionIncompleteException e) {
            MessageSystem.failMessage(player, "selectionIncomplete");
        }
    }
}
