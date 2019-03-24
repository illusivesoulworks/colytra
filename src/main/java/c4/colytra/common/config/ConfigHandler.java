/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.common.config;

import c4.colytra.Colytra;
import c4.colytra.util.ColytraUtil;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

@Config(modid = Colytra.MODID)
public class ConfigHandler {

    @Name("Allow Colytra Separation")
    @Comment("True to allow separating elytras from chestplates in the crafting table, otherwise false")
    public static boolean separateColytra = false;

    @Name("Permission Mode")
    @Comment("Sets whether the Item List uses a blacklist or a whitelist")
    public static PermissionMode permissionMode = PermissionMode.BLACKLIST;

    @Name("Item List")
    @Comment("List of items by registry name to be blacklisted/whitelisted based on Permission Mode")
    public static String[] itemList = new String[]{};

    @Name("Durability Mode")
    @Comment({  "Sets how the elytra chestplates will handle elytra durability",
                "Normal: Elytras will use their own separate durability",
                "Chestplate: Elytras will use the chestplate's durability or energy",
                "Infinite: Elytras will never use durability"})
    public static DurabilityMode durabilityMode = DurabilityMode.NORMAL;

    public static Baubles baubles = new Baubles();

    public static class Baubles {

        @Name("Disable Baubles")
        @Comment("Set to true to disable the bauble Elytra")
        @RequiresMcRestart
        public boolean disableBauble = false;
    }

    public enum PermissionMode {
        BLACKLIST,
        WHITELIST
    }

    public enum DurabilityMode {
        NORMAL,
        CHESTPLATE,
        INFINITE
    }

    @Mod.EventBusSubscriber(modid = Colytra.MODID)
    private static class ConfigEventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
            if (evt.getModID().equals(Colytra.MODID)) {
                ConfigManager.sync(Colytra.MODID, Config.Type.INSTANCE);
                ColytraUtil.initConfigItemList();
            }
        }
    }
}
