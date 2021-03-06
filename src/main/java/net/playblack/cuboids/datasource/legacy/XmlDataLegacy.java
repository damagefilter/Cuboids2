package net.playblack.cuboids.datasource.legacy;

import net.canarymod.CanaryDeserializeException;
import net.canarymod.api.world.position.Vector3D;
import net.playblack.cuboids.Config;
import net.playblack.cuboids.datasource.BaseData;
import net.playblack.cuboids.regions.Region;
import net.playblack.cuboids.regions.Region.Status;
import net.playblack.cuboids.regions.RegionManager;
import net.playblack.mcutils.Debug;
import net.playblack.mcutils.ToolBox;
import net.visualillusionsent.utils.SystemUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XmlData extends BaseData and represents the data layer for retrieving
 * Regions from xml files.
 *
 * @author Chris
 */
public class XmlDataLegacy implements BaseData {

    private final Object lock = new Object();
    /**
     * Used to serialize the XML data into a bytestream
     */
    private XMLOutputter xmlSerializer = new XMLOutputter(Format.getPrettyFormat().setExpandEmptyElements(true).setOmitDeclaration(true).setOmitEncoding(true).setLineSeparator(SystemUtils.LINE_SEP));
    private SAXBuilder regionBuilder = new SAXBuilder();
    private HashMap<String, ArrayList<Region>> loadedRegions = new HashMap<String, ArrayList<Region>>();

    public XmlDataLegacy() {
    }

    @Override
    public void saveRegion(Region node) {
        try {
            writeFile(regionToDom(node));
        }
        catch (IOException e) {
            Debug.log(e.getMessage());
        }
    }

    @Override
    public void saveAll(HashMap<String, List<Region>> treeList) {
        throw new UnsupportedOperationException("Cannot save files in legacy xml backend. Please use the new backend implementation.");
    }

    @Override
    public int loadAll() {
        RegionManager regionMan = RegionManager.get();
        loadedRegions.put("root", new ArrayList<Region>());
        File regionFolder = new File(Config.get().getBasePath() + "regions/");
        if (!regionFolder.exists()) {
            regionFolder.mkdirs();
        }
        int numRegions = 0;
        //Load all files sorted by parents.
        //Parentless regions get sorted into "root"
        for (File file : regionFolder.listFiles()) {
            if (file.getName().toLowerCase().endsWith("xml")) {
                try {
                    Document rdoc = regionBuilder.build(file);
                    Element meta = rdoc.getRootElement().getChild("meta");
                    String parentName = meta.getChildText("parent");
                    Region r = domToRegion(rdoc, false);
                    if (r != null) {
                        if (parentName == null || parentName.isEmpty()) {
                            loadedRegions.get("root").add(r);
                        }
                        else {
                            if (loadedRegions.get(parentName) == null) {
                                loadedRegions.put(parentName, new ArrayList<Region>());
                            }
                            loadedRegions.get(parentName).add(r);
                        }
                    }
                }
                catch (JDOMException e) {
                    Debug.logWarning(e.getMessage());
                }
                catch (IOException e) {
                    Debug.logWarning(e.getMessage());
                }
            }
            numRegions++;
        }

        //Sort out parents and stuff.
        for (String key : loadedRegions.keySet()) {
            //Root has no parents to sort out
            if (!key.equals("root")) {
                for (Region r : loadedRegions.get(key)) {
                    Region parent = findByName(key);
                    if (parent == null) {
                        Debug.logWarning("Cannot find parent " + key + ". Dropping regions with this parent!");
                        break;
                    }
                    r.setParent(parent);
                }
            }
        }

        //Now that we have all the parents sorted out, we can just add all nodes under "root" to the regionmanager
        for (Region root : loadedRegions.get("root")) {
            regionMan.addRoot(root);
        }
        return numRegions;
    }

    public List<Region> loadAllRaw() {
        ArrayList<Region> rootRegions = new ArrayList<Region>();
        loadedRegions.put("root", new ArrayList<Region>());
        File regionFolder = new File(Config.get().getBasePath() + "regions/");
        if (!regionFolder.exists()) {
            return rootRegions;
        }
        //Load all files sorted by parents.
        //Parentless regions get sorted into "root"
        for (File file : regionFolder.listFiles()) {
            if (file.getName().toLowerCase().endsWith("xml")) {
                try {
                    Document rdoc = regionBuilder.build(file);
                    Element meta = rdoc.getRootElement().getChild("meta");
                    String parentName = meta.getChildText("parent");
                    Region r = domToRegion(rdoc, false);
                    if (r != null) {
                        if (parentName == null || parentName.isEmpty()) {
                            loadedRegions.get("root").add(r);
                        }
                        else {
                            if (loadedRegions.get(parentName) == null) {
                                loadedRegions.put(parentName, new ArrayList<Region>());
                            }
                            loadedRegions.get(parentName).add(r);
                        }
                    }
                }
                catch (JDOMException e) {
                    Debug.logWarning(e.getMessage());
                }
                catch (IOException e) {
                    Debug.logWarning(e.getMessage());
                }
            }
        }

        //Sort out parents and stuff.
        for (String key : loadedRegions.keySet()) {
            //Root has no parents to sort out
            if (!key.equals("root")) {
                for (Region r : loadedRegions.get(key)) {
                    Region parent = findByName(key);
                    if (parent == null) {
                        Debug.logWarning("Cannot find parent " + key + ". Dropping regions with this parent!");
                        break;
                    }
                    r.setParent(parent);
                }
            }
        }

        //Now that we have all the parents sorted out, we can just add all nodes under "root" to the regionmanager
        for (Region root : loadedRegions.get("root")) {
            rootRegions.add(root);
        }
        return rootRegions;
    }

    /**
     * Get a region from the given list with the given name
     *
     * @param name
     * @return
     */
    private Region findByName(String name) {
        for (String key : loadedRegions.keySet()) {
            for (Region r : loadedRegions.get(key)) {
                if (r.getName().equals(name)) {
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public void loadRegion(String name, String world) {
        String path = Config.get().getBasePath() + "regions/" + world + "_" + name + ".xml";
        File f = new File(path);
        try {
            Document rdoc = regionBuilder.build(f);
            Region r = domToRegion(rdoc, true);
            Region old = RegionManager.get().getRegionByName(name, world);
            if (old != null) {
                RegionManager.get().removeRegion(old);
            }
            RegionManager.get().addRegion(r);
        }
        catch (JDOMException e) {
            Debug.logWarning(e.getMessage());
        }
        catch (IOException e) {
            Debug.logWarning(e.getMessage());
        }
    }

    @Override
    public void deleteRegion(Region node) {
        String path = Config.get().getBasePath() + "regions/" + node.getWorld() + "_" + node.getName() + "_" + ".xml";
        File file = new File(path);
        file.delete();
    }

    private void writeFile(Document xmlDoc) throws IOException {
        Element meta = xmlDoc.getRootElement().getChild("meta");
        FileWriter writer = new FileWriter(Config.get().getBasePath() + "regions/" + meta.getChildText("world") + "_" + meta.getChildText("name") + "_" + meta.getChildText("dimension") + ".xml");
        xmlSerializer.output(xmlDoc, writer);
        writer.close();
    }

    private Document regionToDom(Region r) {
        Document data = new Document(new Element("region"));
        Element regionElement = data.getRootElement();
        Element meta = new Element("meta");

        meta.addContent(new Element("welcome").setText(r.getWelcome()));
        meta.addContent(new Element("farewell").setText(r.getFarewell()));
        meta.addContent(new Element("name").setText(r.getName()));
        if (r.hasParent()) {
            meta.addContent(new Element("parent").setText(r.getParent().getName()));
        }

        if (r.getRestrictedCommands().size() > 0) {
            StringBuilder str = new StringBuilder();
            for (String cmd : r.getRestrictedCommands()) {
                str.append(cmd).append(",");
            }
            str.deleteCharAt(str.length() - 1);
            meta.addContent(new Element("restricted-commands").setText(str.toString()));
        }

        if (r.getRestrictedItems().size() > 0) {
            StringBuilder str = new StringBuilder();
            for (String cmd : r.getRestrictedItems()) {
                str.append(cmd).append(",");
            }
            str.deleteCharAt(str.length() - 1);
            meta.addContent(new Element("restricted-items").setText(str.toString()));
        }

        meta.addContent(new Element("priority").setText("" + r.getPriority()));
        meta.addContent(new Element("world").setText(r.getWorld()));
        meta.addContent(new Element("origin").setText(r.getOrigin().toString()));
        meta.addContent(new Element("offset").setText(r.getOffset().toString()));
        meta.addContent(new Element("players").setText(r.getPlayerList()));
        meta.addContent(new Element("groups").setText(r.getGroupList()));
        regionElement.addContent(meta);
        Element properties = new Element("properties");
        regionElement.addContent(properties);
        Map<String, Status> props = r.getAllProperties();
        for (String key : props.keySet()) {
            properties.addContent(new Element(key).setText(props.get(key).name()));
        }
        return data;
    }

    private Region domToRegion(Document doc, boolean lookupParent) {
        Region newRegion = new Region();
        Element root = doc.getRootElement();
        Element meta = root.getChild("meta");
        Element properties = root.getChild("properties");

        newRegion.setName(meta.getChildText("name"));
        newRegion.setPriority(Integer.parseInt(meta.getChildText("priority")));
        newRegion.setWorld(meta.getChildText("world"));
        newRegion.setWelcome(ToolBox.stringToNull(meta.getChildText("welcome")));
        newRegion.setFarewell(ToolBox.stringToNull(meta.getChildText("farewell")));
        newRegion.addPlayer(meta.getChildText("players"));
        newRegion.addGroup(meta.getChildText("groups"));
        try {
            newRegion.setBoundingBox(Vector3D.fromString(meta.getChildText("origin")), Vector3D.fromString(meta.getChildText("offset")), false);
        }
        catch (CanaryDeserializeException e) {
            Debug.logWarning(e.getMessage() + " - dropping region!");
            return null;
        }
        for (Element prop : properties.getChildren()) {
            newRegion.setProperty(prop.getName(), Status.fromString(prop.getText()));
        }
        if (lookupParent) {
            if (meta.getChildText("parent") != null) {
                newRegion.setParent(RegionManager.get().getPossibleParent(newRegion));
            }
        }

        if (meta.getChildText("restricted-commands") != null) {
            newRegion.addRestrictedCommand(meta.getChildText("restricted-commands"));
        }

        if (meta.getChildText("restricted-items") != null) {
            newRegion.addRestrictedItem(meta.getChildText("restricted-items"));
        }

        return newRegion;
    }
}
