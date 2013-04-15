package net.playblack.cuboids.impl.canarymod;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.plugin.Plugin;
import net.playblack.cuboids.Bootstrapper;
import net.playblack.cuboids.Config.Implementation;
import net.playblack.cuboids.impl.canarymod.commands.CmodCommands;
import net.playblack.cuboids.impl.canarymod.commands.MiscCommands;
import net.playblack.cuboids.impl.canarymod.commands.SelectionEditingCommands;
import net.playblack.cuboids.impl.canarymod.commands.WorldEditingCommands;
import net.playblack.mcutils.Debug;

public class Cuboids2 extends Plugin {

    @Override
    public boolean enable() {
        Debug.overrideLogger(getLogman());
        new Bootstrapper(new CanaryServer(), null, Implementation.CANARY);
        try {
            Canary.commands().registerCommands(new CmodCommands(), this, false);
            Canary.commands().registerCommands(new MiscCommands(), this, false);
            Canary.commands().registerCommands(new SelectionEditingCommands(), this, false);
            Canary.commands().registerCommands(new WorldEditingCommands(), this, false);
        }
        catch(CommandDependencyException e) {
            Debug.logStack(e);
            return false;
        }
        Canary.hooks().registerListener(new BlockListener(), this);
        Canary.hooks().registerListener(new MiscListener(), this);
        Canary.hooks().registerListener(new PlayerListener(), this);
        return true;
    }

    @Override
    public void disable() {
        Canary.commands().unregisterCommands(this);
    }

}
