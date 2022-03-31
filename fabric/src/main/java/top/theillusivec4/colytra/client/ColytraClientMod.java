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

package top.theillusivec4.colytra.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.entity.EquipmentSlot;
import top.theillusivec4.colytra.common.network.ColytraClientNetwork;
import top.theillusivec4.colytra.common.util.ColytraNbt;

public class ColytraClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ColytraClientNetwork.setup();
    LivingEntityFeatureRendererRegistrationCallback.EVENT
        .register((entityType, entityRenderer, registrationHelper, context) -> registrationHelper
            .register(new ColytraFeatureRenderer<>(entityRenderer, context.getModelLoader())));
    LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(
        player -> !ColytraNbt.hasUpgrade(player.getEquippedStack(EquipmentSlot.CHEST)));
  }
}
