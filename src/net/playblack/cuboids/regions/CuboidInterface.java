package net.playblack.cuboids.regions;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.playblack.cuboids.Config;
import net.playblack.cuboids.HMobTask;
import net.playblack.cuboids.HealThread;
import net.playblack.cuboids.MessageSystem;
import net.playblack.cuboids.blocks.CBlock;
import net.playblack.cuboids.blocks.CItem;
import net.playblack.cuboids.gameinterface.CPlayer;
import net.playblack.cuboids.selections.CuboidSelection;
import net.playblack.cuboids.selections.SelectionManager;
import net.playblack.mcutils.Vector;

/**
 * This accesses RegionManager and RegionPropertiesManager and handles 
 * actions that would and can happen when moving around in cuboids etc.
 * @author Chris
 *
 */
public class CuboidInterface {
    private RegionManager regions;
    private MessageSystem ms;
    private ScheduledExecutorService threadManager = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    HashMap<String,CItem[]> playerInventories = new HashMap<String,CItem[]>();
    
    private static CuboidInterface instance = null;
    
    /**
     * Construct a new CuboidInterface
     * @param r
     * @param log
     */
    private CuboidInterface() {
        regions = RegionManager.getInstance();
        //properties = new RegionPropertiesManager(r, log);
        //this.log = log;
        ms = MessageSystem.getInstance();
        threadManager.scheduleAtFixedRate(
                new HMobTask(), 
                20, 
                20, 
                TimeUnit.SECONDS);
    }
    
    public static CuboidInterface getInstance() {
        if(instance == null) {
            instance = new CuboidInterface();
        }
        return instance;
    }
    

    /**
     * Checks for ignore restrictions, areaMod and the given permission plus
     * if the given player is the owner of the cuboid in question. This will also
     * return false if the cuboid was null (no cuboid found).
     * @param cube
     * @param player
     * @param permission
     * @return true if permission is granted, false otherwise
     */
    private boolean permissionCheckOnToggle(CuboidE cube, CPlayer player, String permission) {
        if(cube == null) {
            ms.failMessage(player, "cuboidNotFoundOnCommand");
            return false; 
        }
        if(!player.hasPermission("cIgnoreRestrictions")) {
            if(!player.hasPermission("cAreaMod")) {
                if(!player.hasPermission(permission)) {
                    ms.failMessage(player, "permissionDenied");
                    return false;
                }
                else {
                    if(cube.playerIsOwner(player.getName())) {
                        return true;
                    }
                    ms.failMessage(player, "playerNotOwner");
                    return false;
                }
            }
        }
        return true;
    }
    
//    private boolean permissionCheck(CPlayer player, String permission) {
//        
//    }
    
    /*
     * **************************************************************************************
     * **************************************************************************************
     * AREA TOGGLES
     * **************************************************************************************
     * **************************************************************************************
     */
    
    /**
     * Toggle pvp on or off
     * @param player
     * @param cuboid
     */
    public void togglePvp(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowPvp()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cpvp")) {
            return;
        }
        
        
        if(cube.isAllowedPvp()) {
            cube.setAllowPvp(false);
            ms.successMessage(player, "pvpDisabled");
        }
        else {
            cube.setAllowPvp(true);
            ms.successMessage(player, "pvpEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Turn the blocking of spreading fire on or off
     * @param player
     * @param cuboid
     */
    public void toggleFireSpread(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowFireSpreadBlock()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cfirespread")) {
            return;
        }
        
        
        if(cube.isBlockFireSpread()) {
            cube.setBlockFireSpread(false);
            ms.successMessage(player, "blockFireSpreadDisabled");
        }
        else {
            cube.setBlockFireSpread(true);
            ms.successMessage(player, "blockFireSpreadEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle creeper secure state in this area
     * @param player
     * @param cuboid
     */
    public void toggleCreeperSecure(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowCreeperSecure()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "ccreeper")) {
            return;
        }
        
        
        if(cube.isCreeperSecure()) {
            cube.setCreeperSecure(false);
            ms.successMessage(player, "creeperSecureDisabled");
        }
        else {
            cube.setCreeperSecure(true);
            ms.successMessage(player, "creeperSecureEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle creative mode on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleFreebuild(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowFreebuild()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cfreebuild")) {
            return;
        }
        
        
        if(cube.isFreeBuild()) {
            cube.setFreeBuild(false);
            ms.successMessage(player, "creativeDisabled");
        }
        else {
            cube.setFreeBuild(true);
            ms.successMessage(player, "creativeEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle farmland protection on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleFarmland(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowFarmland()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cfarmland")) {
            return;
        }
        if(cube.isFarmland()) {
            cube.setFarmland(false);
            ms.successMessage(player, "farmlandDisabled");
        }
        else {
            cube.setFarmland(true);
            ms.successMessage(player, "farmlandEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle healing on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleHealing(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowHealing()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cheal")) {
            return;
        }
        if(cube.isHealingArea()) {
            cube.setHealing(false);
            ms.successMessage(player, "healingDisabled");
        }
        else {
            cube.setHealing(true);
            ms.successMessage(player, "healingEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle healing on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleHmobs(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowHmobs()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "chmob")) {
            return;
        }
        if(cube.ishMob()) {
            cube.sethMob(false);
            ms.successMessage(player, "hmobDisabled");
        }
        else {
            cube.sethMob(true);
            ms.successMessage(player, "hmobEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle healing on or off in this area
     * @param player
     * @param cuboid
     * @param lava set true to toggle lava control instead of water control
     */
    public void toggleFlowControl(CPlayer player, String cuboid, boolean lava) {
        if((!Config.getInstance().isAllowLavaControl()) || (!Config.getInstance().isAllowWaterControl())) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cliquids")) {
            return;
        }
        if(lava) {
            if(cube.isLavaControl()) {
                cube.setLavaControl(false);
                ms.successMessage(player, "lavaFlowControlDisabled");
            }
            else {
                cube.setLavaControl(true);
                ms.successMessage(player, "lavaFlowControlEnabled");
            }
        }
        else {
            if(cube.isWaterControl()) {
                cube.setWaterControl(false);
                ms.successMessage(player, "waterFlowControlDisabled");
            }
            else {
                cube.setWaterControl(true);
                ms.successMessage(player, "waterFlowControlEnabled");
            }
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle protection on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleProtection(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowProtection()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cprotection")) {
            return;
        }
        if(cube.isProtected()) {
            cube.setProtection(false);
            ms.successMessage(player, "protectionDisabled");
        }
        else {
            cube.setProtection(true);
            ms.successMessage(player, "protectionEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle restriction on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleRestriction(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowRestriction()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "crestriction")) {
            return;
        }
        if(cube.isRestricted()) {
            cube.setRestriction(false);
            ms.successMessage(player, "restrictionDisabled");
        }
        else {
            cube.setRestriction(true);
            ms.successMessage(player, "restrictionEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle sanctuary on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleSanctuary(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowSanctuary()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "csanctuary")) {
            return;
        }
        if(cube.isSanctuary()) {
            cube.setSanctuary(false);
            ms.successMessage(player, "sanctuaryDisabled");
        }
        else {
            cube.setSanctuary(true);
            ms.successMessage(player, "sanctuaryEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle sanctuary animal spawn on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleSanctuaryAnimalSpawn(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowSanctuarySpawnAnimals()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "csanctuary")) {
            return;
        }
        if(cube.sanctuarySpawnAnimals()) {
            cube.setSanctuarySpawnAnimals(false);
            ms.successMessage(player, "sanctuaryAnimalSpawnDisabled");
        }
        else {
            cube.setSanctuarySpawnAnimals(true);
            ms.successMessage(player, "sanctuaryAnimalSpawnEnabled");
        }
        cube.hasChanged=true;
    }
    
    /**
     * Toggle sanctuary on or off in this area
     * @param player
     * @param cuboid
     */
    public void toggleTntSecure(CPlayer player, String cuboid) {
        if(!Config.getInstance().isAllowTntSecure()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "ctnt")) {
            return;
        }
        if(cube.isTntSecure()) {
            cube.setTntSecure(false);
            ms.successMessage(player, "tntSecureDisabled");
        }
        else {
            cube.setTntSecure(true);
            ms.successMessage(player, "tntSecureEnabled");
        }
        cube.hasChanged=true;
    }
    
    /*
     * **************************************************************************************
     * **************************************************************************************
     * AREA SETS FOR WELCOME AND GOODBYE
     * **************************************************************************************
     * **************************************************************************************
     */
    
    /**
     * Set the welcome message.
     * Insert null for message to remove the welcome message
     * @param player
     * @param cuboid
     * @param message
     */
    public void setWelcome(CPlayer player, String cuboid, String message) {
        if(!Config.getInstance().isAllowWelcome()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cmessage")) {
            return;
        }
        cube.setWelcome(message);
        cube.hasChanged=true;
    }
    
    /**
     * Set the farewell message.
     * Insert null for message to remove the farewell message
     * @param player
     * @param cuboid
     * @param message
     */
    public void setFarewell(CPlayer player, String cuboid, String message) {
        if(!Config.getInstance().isAllowFarewell()) {
            ms.failMessage(player, "optionDisabled");
            return;
        }
        CuboidE cube = regions.getCuboidByName(cuboid, player.getWorld().getName()).getCuboid();
        if(!permissionCheckOnToggle(cube, player, "cmessage")) {
            return;
        }
        cube.setFarewell(message);
        cube.hasChanged=true;
    }
    
    /*
     * **************************************************************************************
     * **************************************************************************************
     * AREA OPERATION FOR PLAYERS (ADDING/REMOVING FROM AREA, TOGGLING CREATIVE/HEALING ETC)
     * **************************************************************************************
     * **************************************************************************************
     */
    
    /**
     * Add a player to a region if he's not inside already
     * @param player
     * @param location
     */
    public void addPlayerWithin(CPlayer player, Vector location) {
        ArrayList<CuboidE> nodes = regions.getCuboidsContaining(location, player.getWorld().getName());
        for(CuboidE cube : nodes) {
            if(!cube.playerIsWithin(player.getName())) {
                cube.addPlayerWithin(player.getName());
                
                if(cube.getWelcome() != null) {
                    ms.notification(player, cube.getWelcome());
                }
                if(cube.isHealingArea()) {
                    threadManager.schedule(
                            new HealThread(player, cube, threadManager, Config.getInstance().getHealPower(), Config.getInstance().getHealDelay()), 
                            0, 
                            TimeUnit.SECONDS);
                }
                
                if(cube.isFreeBuild() && (!player.isInCreativeMode())) {
                    playerInventories.put(player.getName(), player.getInventory());
                    player.clearInventory();
                    player.setCreative(1);
                }
            }
        }
    }
    
    /**
     * Remove a player from this region.
     * TODO: For a method that keeps being called on every footstep, this must be WAY more lightweight,
     * think about some better logic here! The slightest performance gain will help already
     * @param player
     * @param vFrom
     * @param vTo
     */
    public void removePlayerWithin(CPlayer player, Vector vFrom, Vector vTo) {
        CuboidNode cubeFrom = regions.getActiveCuboid(vFrom, player.getWorld().getName());
        CuboidNode cubeTo = regions.getActiveCuboid(vTo, player.getWorld().getName());
        if(cubeFrom != null) {
            if(cubeTo != null) {
                ArrayList<CuboidE> cubesFrom = regions.getCuboidsContaining(vFrom, player.getWorld().getName());
                ArrayList<CuboidE> cubesTo = regions.getCuboidsContaining(vTo, player.getWorld().getName());
                for(CuboidE from : cubesFrom) {
                    for(CuboidE to : cubesTo) {
                        if(!from.isWithin(vTo)) {
                            if(from.getFarewell() != null) {
                                ms.notification(player, from.getFarewell());
                            }
                            if((from.isFreeBuild()) && (!to.isFreeBuild())) {
                                if(player.isInCreativeMode()) {
                                    player.setCreative(0);
                                    player.setInventory(playerInventories.get(player.getName()));
                                    playerInventories.remove(player.getName());
                                }
                            }
                            from.removePlayerWithin(player.getName());
                        }
                    }
                }
            }
            else {
                ArrayList<CuboidE> cubesFrom = regions.getCuboidsContaining(vFrom, player.getWorld().getName());
                for(CuboidE from : cubesFrom) {
                    if(from.getFarewell() != null) {
                        ms.notification(player, from.getFarewell());
                    }
                    if(from.isFreeBuild()) {
                        player.setCreative(0);
                        player.setInventory((playerInventories.get(player.getName())));
                        playerInventories.remove(player.getName());
                    }
                    from.removePlayerWithin(player.getName());
                }
            }
        }
    }
    
    /*
     * **************************************************************************************
     * **************************************************************************************
     * INTERFACE FOR AREA LOOKUP + CHECKS FOR WHATEVER PERMISSION
     * **************************************************************************************
     * **************************************************************************************
     */
    
    /**
     * Check if a player can modify a block at the given position.
     * @param player
     * @param position
     * @return
     */
    public boolean canModifyBlock(CPlayer player, Vector position) {
        if(player.hasPermission("cIgnoreRestrictions")) {
            return true;
        }
        CuboidE cube = regions.getActiveCuboid(position, player.getWorld().getName()).getCuboid();
        if(cube.getName().equals("__GLOBAL__")) {
            return cube.isProtected();
        }
        if(cube.isProtected()) {
            if(cube.playerIsAllowed(player.getName(), player.getGroups()) 
                    || player.hasPermission("cAreaMod")) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }
    
    /**
     * Can modify block based upon a given cuboid
     * @param player
     * @param cube
     * @return
     */
    private boolean canModifyBlock(CPlayer player, CuboidE cube) {
        if(player.hasPermission("cIgnoreRestrictions")) {
            return true;
        }
        if(cube.getName().equals("__GLOBAL__")) {
            return cube.isProtected();
        }
        if(cube.isProtected()) {
            if(cube.playerIsAllowed(player.getName(), player.getGroups()) 
                    || player.hasPermission("cAreaMod")) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }
    
    /**
     * Check if a player can enter the cuboid at his "going-to" position
     * @param player
     * @param block
     * @return
     */
    public boolean canEnter(CPlayer player, Vector block) {
        if(player.hasPermission("cIgnoreRestrictions")) {
            return true;
        }       
        CuboidE cube = regions.getActiveCuboid(block, player.getWorld().getName()).getCuboid();
        if(cube != null) {
            if(cube.isRestricted()) {
                if(!cube.playerIsAllowed(player.getName(), player.getGroups())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Check if a block can flow (water or lava only)
     * @param block
     * @param v
     * @param world
     * @return
     */
    public boolean canFlow(CBlock block, Vector v, String world) {
        CuboidE cube = regions.getActiveCuboid(v, world).getCuboid();
        //LAVA
        if(block.getType() == 10 || block.getType() == 11) {
            return cube.isLavaControl();
        }
        //WATER
        if(block.getType() == 8 || block.getType() == 9) {
            return cube.isWaterControl();
        }
        else {
            return false;
        }
    }
    
    /**
     * Check if a player can use a bucket at the given location
     * @param player
     * @param v
     * @param block
     * @return true if a player can use a bucket
     */
    public boolean canPlaceBucket(CPlayer player, Vector v, CBlock block) {
        if(player.hasPermission("cIgnoreRestrictions")) {
            return true;
        }
        
        CuboidE cube = regions.getActiveCuboid(v, player.getWorld().getName()).getCuboid();
        if(cube.getName().equals("__GLOBAL__")) {
            return (cube.isLavaControl() || cube.isWaterControl());
        }
        if(block.getType() == 10 || block.getType() == 11) {
            if(cube.isLavaControl()
                    && (cube.playerIsAllowed(player.getName(), player.getGroups()) || player.hasPermission("cAreaMod"))) {
                return true;
            }
            else {
                return false;
            }
        }
        else if(block.getType() == 8 || block.getType() == 9) {
            if(cube.isWaterControl()
                    && (cube.playerIsAllowed(player.getName(), player.getGroups()) || player.hasPermission("cAreaMod"))) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return !canModifyBlock(player, cube);
        }
    }
    
    /**
     * Check if a player can start a fire at the given position
     * @param player
     * @param v
     * @return
     */
    public boolean canStartFire(CPlayer player, Vector v) {
        if(player.hasPermission("cIgnoreRestrictions")) {
            return true;
        }
        CuboidE cube = regions.getActiveCuboid(v, player.getWorld().getName()).getCuboid();
        if(cube.getName().equals("__GLOBAL__")) {
            return (cube.isProtected() && cube.isBlockFireSpread());
        }
        if(cube.isProtected() && cube.isBlockFireSpread()) {
            if(cube.playerIsOwner(player.getName())) {
                return true;
            }
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * Check if that command is on the restricted commands list
     * @param player
     * @param command
     * @return
     */
    public boolean commandIsRestricted(CPlayer player, String command) {
        if(player.hasPermission("cIgnoreRestrictions")) {
            return true;
        }
        CuboidE cube = regions.getActiveCuboid(player.getPosition(), player.getWorld().getName()).getCuboid();
        return cube.commandIsRestricted(command);
    }
    
    /**
     * Check if a cuboid exists
     * @param name
     * @param world
     * @return
     */
    public boolean cuboidExist(String name, String world) {
        return regions.cuboidExists(name, world);
    }
    
    /**
     * Add a cuboid to the tree
     * @param cube
     * @return
     */
    public boolean addCuboid(CuboidE cube) {
        if(Config.getInstance().isAutoParent()) {
            //Override default with parent settings and make parent stuff
                CuboidNode parent = regions.getPossibleParent(cube);
                if(parent != null) {
                    cube.setParent(parent.getCuboid().getName());
                    cube.setPriority(parent.getCuboid().getPriority()+1);
                    cube.overwriteProperties(parent.getCuboid());
                    return regions.addCuboid(cube);
                }
        }
        return regions.addCuboid(cube);
    }
    
    /**
     * Remove a cuboid from tree
     * @param player
     * @param cubeName
     * @return
     */
    public boolean removeCuboid(CPlayer player, String cubeName) {
        CuboidE cube = regions.getCuboidByName(player.getWorld().getName(), cubeName).getCuboid();
        
        if(cube != null) {
            if(cube.playerIsOwner(player.getName()) || player.hasPermission("cAreaMod") 
                    || player.hasPermission("/cIgnoreRestrictions") || player.hasPermission("/cdelete")) {
                    String response = regions.removeCuboid(cube, false);
                    
                    if(response.equalsIgnoreCase("NOT_REMOVED_HAS_CHILDS")) {
                        ms.failMessage(player, "cuboidNotRemovedHasChilds");
                        return false;
                    }
                    
                    if(response.equalsIgnoreCase("REMOVED")) {
                        ms.successMessage(player, "cuboidRemoved");
                        return true;
                    }
                    
                    if(response.equalsIgnoreCase("NOT_REMOVED_NOT_FOUND")) {
                        ms.failMessage(player, "cuboidNotRemovedNotFound");
                        return false;
                    }
                    if(response.equalsIgnoreCase("NOT_REMOVED")) {
                        ms.failMessage(player, "cuboidNotRemovedError");
                        return false;
                    }
                    else {
                        ms.customFailMessage(player, "Unexpected Exception! Sorry! Try again.");
                        return false;
                    }
                
            }
            else {
                ms.failMessage(player, "permissionDenied");
                return false;
            }

        } //cuboid not null
        else {
            ms.failMessage(player, "cuboidNotRemovedNotFound");
            return false;
        }
    }
    
    /**
     * Remove a player or group from the cuboid with given name (index 1)
     * @param player
     * @param command
     * @return
     */
    public boolean disallowEntity(CPlayer player, String[] command) {
        CuboidE cube = regions.getCuboidByName(player.getWorld().getName(), command[1]).getCuboid();
        if(cube != null) {
            if(cube.playerIsOwner(player.getName()) || player.hasPermission("cAreaMod")|| player.hasPermission("cIgnoreRestrictions")) {
                if(!player.hasPermission("callow")) {
                    if(!player.hasPermission("/cIgnoreRestrictions")) {
                        ms.failMessage(player, "permissionDenied");
                        return false;
                    }
                }
                for(int i = 2; i < command.length; i++) {
                    if(command[i].startsWith("g:")) {
                        cube.removeGroup(command[i]);
                    }
                    else {
                        cube.removePlayer(command[i]);
                    }
                }
                
                if(regions.updateCuboidNode(cube)) {
                    ms.successMessage(player, "cuboidUpdated");
                    return true;
                }
                else {
                    ms.failMessage(player, "cuboidNotUpdated");
                    return false;
                }
            }
            else {
                ms.failMessage(player, "cuboidNotUpdated");
                return false;
            }
        }
        else {
            ms.failMessage(player, "cuboidNotUpdated");
            return false;
        }
    }
    
    /**
     * Allow an entity into the area given with index 1 of command array
     * @param player
     * @param command
     * @return
     */
    public boolean allowEntity(CPlayer player, String[] command) {
        CuboidE cube = regions.getCuboidByName(player.getWorld().getName(), command[1]).getCuboid();
        if(cube != null) {
            if(cube.playerIsOwner(player.getName()) || player.hasPermission("cAreaMod") || player.hasPermission("cIgnoreRestrictions")) {
                
                if(!player.hasPermission("callow")) {
                    if(!player.hasPermission("cIgnoreRestrictions")) {
                        ms.failMessage(player, "permissionDenied");
                        return false;
                    }
                }
                
                for(int i = 3; i < command.length; i++) {
                    if(command[i].startsWith("g:")) {
                        cube.addGroup(command[i]);
                    }
                    else {
                        cube.addPlayer(command[i]);
                    }
                }
                regions.updateCuboidNode(cube);
                ms.successMessage(player, "cuboidUpdated");
                return true;
            }
            else {
                ms.failMessage(player, "playerNotOwner");
                return false;
            }
        }
        else {
            ms.failMessage(player, "noCuboidFoundOnCommand");
            return false;
        }
    }
    
    public boolean resize(CPlayer player, String cuboidName) {
        CuboidE cube = regions.getCuboidByName(cuboidName, player.getWorld().getName()).getCuboid();
        if(cube != null) {
            if(cube.playerIsOwner(player.getName()) || player.hasPermission("/cAreaMod") || player.hasPermission("/cIgnoreRestrictions")) {
                if(!player.hasPermission("/cIgnoreRestrictions")) {
                    if(!player.hasPermission("/cmove")) {
                        if(!player.hasPermission("/cAreaMod")) {
                            ms.failMessage(player, "permissionDenied");
                            return false;
                        }
                    }
                }
                CuboidSelection selection = SelectionManager.getInstance().getPlayerSelection(player.getName());
                if(!selection.isComplete()) {
                    ms.failMessage(player, "selectionIncomplete");
                }
                cube.setPoints(selection.getOrigin(), selection.getOffset());
                regions.updateCuboidNode(cube);
                ms.successMessage(player, "cuboidMoved");
                return true;
            }
            else {
                ms.failMessage(player, "playerNotOwner");
                return false;
            }
        }
        else {
            ms.failMessage(player, "cuboidNotFoundOnCommand");
            return false;
        }
    }
}
