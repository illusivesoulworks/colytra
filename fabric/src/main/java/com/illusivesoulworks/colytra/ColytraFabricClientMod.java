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

import com.illusivesoulworks.colytra.client.ClientEvents;
import com.illusivesoulworks.colytra.client.ColytraLayer;
import com.illusivesoulworks.colytra.common.ElytraTag;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.world.entity.EquipmentSlot;

public class ColytraFabricClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    LivingEntityFeatureRendererRegistrationCallback.EVENT
        .register((entityType, entityRenderer, registrationHelper, context) -> registrationHelper
            .register(new ColytraLayer<>(entityRenderer, context.getModelSet())));
    LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(
        player -> !ElytraTag.hasUpgrade(player.getItemBySlot(EquipmentSlot.CHEST)));
    ItemTooltipCallback.EVENT.register(
        (stack, context, lines) -> ClientEvents.addColytraTooltip(stack, lines));
  }
}
