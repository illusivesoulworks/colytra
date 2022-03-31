/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Colytra, a mod made for Minecraft.
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.colytra;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.colytra.client.ColytraClientMod;
import top.theillusivec4.colytra.common.CommonEventHandler;
import top.theillusivec4.colytra.common.crafting.ElytraAttachmentRecipe;
import top.theillusivec4.colytra.common.crafting.ElytraDetachmentRecipe;
import top.theillusivec4.colytra.common.integration.CyclicClientPlugin;
import top.theillusivec4.colytra.common.integration.CyclicPlugin;
import top.theillusivec4.colytra.server.ColytraServerConfig;

@Mod(Colytra.MOD_ID)
public class Colytra {

  public static final String MOD_ID = "colytra";
  public static final Logger LOGGER = LogManager.getLogger();

  private static final String ATTACH_ELYTRA = "elytra_attachment";
  private static final String DETACH_ELYTRA = "elytra_detachment";

  public Colytra() {
    final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::config);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ColytraServerConfig.serverSpec);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new CommonEventHandler());

    if (ModList.get().isLoaded("cyclic")) {
      CyclicPlugin.setup();
    }
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    ColytraClientMod.setup();

    if (ModList.get().isLoaded("cyclic")) {
      CyclicClientPlugin.setup();
    }
  }

  private void config(final ModConfigEvent evt) {

    if (evt.getConfig().getModId().equals(MOD_ID)) {

      if (evt.getConfig().getType() == Type.SERVER) {
        ColytraServerConfig.bake();
      }
    }
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onRecipeSerializerRegistry(
        final RegistryEvent.Register<RecipeSerializer<?>> evt) {
      ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA.setRegistryName(ATTACH_ELYTRA);
      ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA.setRegistryName(DETACH_ELYTRA);
      evt.getRegistry().registerAll(ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA,
          ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA);
    }
  }
}
