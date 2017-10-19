/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.proxy;

import c4.colytra.client.ClientEventHandler;
import c4.colytra.client.renderer.entity.layers.LayerColytra;
import c4.colytra.core.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    public void init(FMLInitializationEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        ClientUtil.init();
        addColytraRenderLayer();
    }

    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {

        if (CommonProxy.baublesLoaded) {
            ModelLoader.setCustomModelResourceLocation(CommonProxy.elytraBauble, 0, new ModelResourceLocation(CommonProxy.elytraBauble.getRegistryName(), "inventory"));
        }
    }

    private static void addColytraRenderLayer() {
        Minecraft mc = Minecraft.getMinecraft();
        RenderManager manager = mc.getRenderManager();
        Map<String, RenderPlayer> skinMap = manager.getSkinMap();
        skinMap.get("default").addLayer(new LayerColytra(skinMap.get("default")));
        skinMap.get("slim").addLayer(new LayerColytra(skinMap.get("slim")));
        Render<?> render = manager.getEntityClassRenderObject(EntityArmorStand.class);
        ((RenderLivingBase<?>) render).addLayer(new LayerColytra((RenderLivingBase<?>) render));
    }
}
