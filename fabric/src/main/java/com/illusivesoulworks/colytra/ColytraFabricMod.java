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

import com.illusivesoulworks.colytra.common.CommonEvents;
import com.illusivesoulworks.colytra.common.crafting.ElytraAttachmentRecipe;
import com.illusivesoulworks.colytra.common.crafting.ElytraDetachmentRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ColytraFabricMod implements ModInitializer {

  @Override
  public void onInitialize() {
    Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
        ColytraConstants.MOD_ID + ":" + ColytraConstants.ATTACH_ELYTRA,
        ElytraAttachmentRecipe.CRAFTING_ATTACH_ELYTRA);
    Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
        ColytraConstants.MOD_ID + ":" + ColytraConstants.DETACH_ELYTRA,
        ElytraDetachmentRecipe.CRAFTING_DETACH_ELYTRA);
    EntityElytraEvents.CUSTOM.register(CommonEvents::updateColytra);
  }
}
