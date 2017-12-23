/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.core.util;

import c4.colytra.Colytra;
import c4.colytra.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class ConfigHandler {

    private static final String CATEGORY_GENERAL = "general";
    public static Configuration cfg;
    private static String[] blacklist = new String[] {};
    private static String[] whitelist = new String[] {};
    public static ArrayList<Item> blacklisted;
    public static ArrayList<Item> whitelisted;
    public static boolean disableBauble = false;
    public static String durabilityMode = "Normal";
    public static String itemPermissionMode = "Blacklist";
    private static String[] durabilityModeList = new String[] {"Normal", "Infinite", "Chestplate"};

    public static void readConfig() {
        try {
            cfg = CommonProxy.config;
            cfg.load();
            initConfig();
        } catch (Exception e1) {
            Colytra.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initConfig() {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        if (CommonProxy.baublesLoaded) {
            Property prop = cfg.get(CATEGORY_GENERAL, "Disable Bauble", disableBauble);
            prop.setLanguageKey("Disable Bauble");
            prop.setComment("Set to true to disable the elytra bauble" + " [default: " + disableBauble + "]");
            prop.setRequiresMcRestart(true);
            disableBauble = prop.getBoolean(disableBauble);
        }
        itemPermissionMode = cfg.getString("Permission Mode", CATEGORY_GENERAL, itemPermissionMode, "The item permission mode to use for elytra chestplates (Blacklist, Whitelist)", new String[] {"Blacklist", "Whitelist"});
        whitelist = cfg.getStringList("Whitelist", CATEGORY_GENERAL, whitelist, "A list of items that can be attached with an elytra when Whitelist mode is enabled");
        blacklist = cfg.getStringList("Blacklist", CATEGORY_GENERAL, blacklist, "A list of items that cannot be attached with an elytra when Blacklist mode is enabled");
        durabilityMode = cfg.getString("Colytra Durability Mode", CATEGORY_GENERAL, durabilityMode,
                "How to handle durability for elytras attached to chestplates\n" +
                        "Normal - Elytras on chestplates will use their own durability\n" +
                        "Infinite - Elytras on chestplates will not use any durability\n" +
                        "Chestplate - Elytras on chestplates will use chestplate durability",
                        durabilityModeList);
        initLists();
    }

    private static void initLists() {

        blacklisted = new ArrayList<>();
        whitelisted = new ArrayList<>();

        if (blacklist.length > 0) {

            for (String s : blacklist) {
                Item item = Item.getByNameOrId(s);
                if (item != null) {
                    blacklisted.add(item);
                }
            }
        }

        if (whitelist.length > 0) {
            for (String s : whitelist) {
                Item item = Item.getByNameOrId(s);
                if (item != null) {
                    whitelisted.add(item);
                }
            }
        }
    }

    @Mod.EventBusSubscriber
    private static class ConfigChangeHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
            if (e.getModID().equals(Colytra.MODID)) {
                initConfig();

                if (cfg.hasChanged()) {
                    cfg.save();
                }
            }
        }
    }
}
