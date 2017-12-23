/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.proxy;

import c4.colytra.Colytra;
import c4.colytra.common.CommonEventHandler;
import c4.colytra.common.crafting.recipe.RecipeElytraBauble;
import c4.colytra.common.items.ItemElytraBauble;
import c4.colytra.core.util.ConfigHandler;
import c4.colytra.network.NetworkHandler;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@Mod.EventBusSubscriber
public class CommonProxy {

    public static Configuration config;
    public static ItemElytraBauble elytraBauble;
    public static boolean baublesLoaded = false;
    public static boolean quarkLoaded = false;

    public void preInit(FMLPreInitializationEvent e) {

        if (Loader.isModLoaded("baubles")) {
            baublesLoaded = true;
        }

        if (Loader.isModLoaded("quark")) {
            quarkLoaded = true;
        }

        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "colytra.cfg"));
        ConfigHandler.readConfig();
    }

    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        NetworkHandler.init();
    }

    public void postInit(FMLPostInitializationEvent e) {

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {

        if (baublesLoaded && !ConfigHandler.disableBauble) {
            elytraBauble = new ItemElytraBauble();
            e.getRegistry().register(elytraBauble);
        }
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> e) {

        if (baublesLoaded && !ConfigHandler.disableBauble) {
            e.getRegistry().register((new RecipeElytraBauble(new ResourceLocation(Colytra.MODID, "elytraToBauble"), Items.ELYTRA, new ItemStack(CommonProxy.elytraBauble, 1))).setRegistryName("elytra_to_bauble_recipe"));
            e.getRegistry().register((new RecipeElytraBauble(new ResourceLocation(Colytra.MODID, "baubleToElytra"), CommonProxy.elytraBauble, new ItemStack(Items.ELYTRA, 1))).setRegistryName("bauble_to_elytra_recipe"));
        }
    }
}
