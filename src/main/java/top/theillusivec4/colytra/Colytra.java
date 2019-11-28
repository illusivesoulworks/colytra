package top.theillusivec4.colytra;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.colytra.client.EventHandlerClient;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.EventHandlerCommon;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;
import top.theillusivec4.colytra.common.crafting.ElytraAttachmentRecipe;
import top.theillusivec4.colytra.common.crafting.ElytraDetachmentRecipe;
import top.theillusivec4.colytra.common.network.NetworkHandler;

@Mod(Colytra.MODID)
public class Colytra {

  public static final String MODID = "colytra";
  public static final Logger LOGGER = LogManager.getLogger();

  private static final String ATTACH_ELYTRA = "elytra_attachment";
  private static final String DETACH_ELYTRA = "elytra_detachment";

  public Colytra() {

    final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ColytraConfig.serverSpec);
  }

  private void setup(final FMLCommonSetupEvent evt) {

    CapabilityElytra.register();
    NetworkHandler.register();
    MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
  }

  private void clientSetup(final FMLClientSetupEvent evt) {

    MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onRecipeSerializerRegistry(
        final RegistryEvent.Register<IRecipeSerializer<?>> evt) {

      ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA.setRegistryName(ATTACH_ELYTRA);
      ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA.setRegistryName(DETACH_ELYTRA);
      evt.getRegistry()
          .registerAll(ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA,
              ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA);
    }
  }
}
