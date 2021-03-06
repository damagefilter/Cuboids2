package net.playblack.cuboids.loaders.cuboidf;

import net.playblack.cuboids.loaders.CuboidShell;
import net.playblack.cuboids.loaders.Loader;
import net.playblack.mcutils.Debug;
import net.visualillusionsent.utils.PropertiesFile;

import java.io.File;
import java.util.ArrayList;

public class CuboidFLoader implements Loader {

    @Override
    public java.util.List<CuboidShell> load() {
        ArrayList<CuboidShell> shells = new ArrayList<CuboidShell>(20);
        try {
            File cuboidFPath = new File("plugins/config/CuboidPlugin/areas/");
            if (!cuboidFPath.exists()) {
                // Does not exists, return empty shell list
                return shells;
            }
            if (cuboidFPath.listFiles().length > 0) {
                File test = new File("plugins/cuboids2/backups_cuboidF/");
                if (!test.exists()) {
                    test.mkdirs();
                }
            }
            for (File files : new File("plugins/config/CuboidPlugin/areas/").listFiles()) {
                if (files.getName().toLowerCase().endsWith(".areaf")) {
                    PropertiesFile file = new PropertiesFile("plugins/config/CuboidPlugin/areas/" + files.getName());
                    shells.add(new CuboidFShell(file));
                    File b = new File("plugins/cuboids2/backups_cuboidF/" + files.getName());
                    files.renameTo(b);
                }
            }
        }
        catch (Exception e) {
            Debug.logWarning("Exception while loading CuboidF: " + e.getMessage());
        }
        return shells;
    }

    @Override
    public String getImplementationVersion() {
        return "CuboidF (by SirPsp)";
    }

}
