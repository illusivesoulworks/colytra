/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra. If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.colytra;

import com.illusivesoulworks.colytra.common.CommonEventsListener;
import com.illusivesoulworks.colytra.common.crafting.ElytraAttachmentRecipe;
import com.illusivesoulworks.colytra.common.crafting.ElytraDetachmentRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ColytraConstants.MOD_ID)
public class ColytraForgeMod {

  private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
      DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ColytraConstants.MOD_ID);
  private static final RegistryObject<RecipeSerializer<ElytraAttachmentRecipe>>
      ELYTRA_ATTACHMENT_RECIPE = RECIPE_SERIALIZERS.register(ColytraConstants.ATTACH_ELYTRA,
      () -> ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA);
  private static final RegistryObject<RecipeSerializer<ElytraDetachmentRecipe>>
      ELYTRA_DETACHMENT_RECIPE = RECIPE_SERIALIZERS.register(ColytraConstants.DETACH_ELYTRA,
      () -> ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA);

  public ColytraForgeMod() {
    ColytraCommonMod.init();
    RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ColytraForgeClientMod::setup);
    MinecraftForge.EVENT_BUS.register(new CommonEventsListener());
  }
}