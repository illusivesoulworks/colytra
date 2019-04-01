package top.theillusivec4.colytra;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.colytra.client.EventHandlerClient;
import top.theillusivec4.colytra.client.LayerColytra;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.EventHandlerCommon;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;
import top.theillusivec4.colytra.common.crafting.RecipesElytraAttachment;
import top.theillusivec4.colytra.common.crafting.RecipesElytraDetachment;
import top.theillusivec4.colytra.common.network.NetworkHandler;

import java.util.List;
import java.util.Map;

@Mod(Colytra.MODID)
public class Colytra {

    public static final String MODID = "colytra";

    public Colytra() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ColytraConfig.serverSpec);
    }

    private void setup(FMLCommonSetupEvent evt) {
        CapabilityElytra.register();
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        RecipeSerializers.register(RecipesElytraAttachment.CRAFTING_ATTACH_ELYTRA);
        RecipeSerializers.register(RecipesElytraDetachment.CRAFTING_DETACH_ELYTRA);
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientSetup {

        @SubscribeEvent
        public static void setupClient(FMLClientSetupEvent evt) {
            MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
        }

        @SubscribeEvent
        public static void postSetup(FMLLoadCompleteEvent evt) {
            RenderManager manager = Minecraft.getInstance().getRenderManager();
            Map<String, RenderPlayer> renderPlayerMap = manager.getSkinMap();
            for(RenderPlayer render : renderPlayerMap.values()) {
                List<LayerRenderer> list = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, render, "field_177097_h");
                list.add(new LayerColytra(render));
            }
            Render<?> render = manager.getEntityClassRenderObject(EntityArmorStand.class);
            ((RenderLivingBase<?>) render).addLayer(new LayerColytra((RenderLivingBase<?>) render));
        }
    }
}
