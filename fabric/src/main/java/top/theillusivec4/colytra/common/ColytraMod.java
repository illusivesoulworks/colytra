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

package top.theillusivec4.colytra.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.colytra.common.crafting.ElytraAttachmentRecipe;
import top.theillusivec4.colytra.common.crafting.ElytraDetachmentRecipe;
import top.theillusivec4.colytra.common.config.AutoConfigPlugin;
import top.theillusivec4.colytra.common.util.ColytraHooks;

public class ColytraMod implements ModInitializer {

  public static final String MOD_ID = "colytra";

  private static final String ATTACH_ELYTRA = MOD_ID + ":elytra_attachment";
  private static final String DETACH_ELYTRA = MOD_ID + ":elytra_detachment";

  public static boolean isConfigLoaded = false;

  @Override
  public void onInitialize() {
    Registry.register(Registry.RECIPE_SERIALIZER, ATTACH_ELYTRA,
        ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA);
    Registry.register(Registry.RECIPE_SERIALIZER, DETACH_ELYTRA,
        ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA);
    EntityElytraEvents.CUSTOM.register(ColytraHooks::updateColytra);

    // Config
    isConfigLoaded = FabricLoader.getInstance().isModLoaded("cloth-config2");

    if (isConfigLoaded) {
      AutoConfigPlugin.init();
    }
    ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {

      if (isConfigLoaded) {
        AutoConfigPlugin.bake();
      }
    });
    ServerLifecycleEvents.END_DATA_PACK_RELOAD
        .register((minecraftServer, serverResourceManager, b) -> {

          if (isConfigLoaded) {
            AutoConfigPlugin.bake();
          }
        });
  }
}
