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
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.caelus.api.RenderCapeEvent;

public class ColytraForgeClientMod {

  public static void setup() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(ColytraForgeClientMod::onAddLayers);
    MinecraftForge.EVENT_BUS.addListener(ColytraForgeClientMod::onItemTooltip);
    MinecraftForge.EVENT_BUS.addListener(ColytraForgeClientMod::onRenderCape);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void onAddLayers(final EntityRenderersEvent.AddLayers evt) {

    for (String skin : evt.getSkins()) {
      LivingEntityRenderer renderer = evt.getSkin(skin);

      if (renderer != null) {
        renderer.addLayer(new ColytraLayer<>(renderer, evt.getEntityModels()));
      }
    }
    LivingEntityRenderer renderer = evt.getRenderer(EntityType.ARMOR_STAND);

    if (renderer != null) {
      renderer.addLayer(new ColytraLayer<>(renderer, evt.getEntityModels()));
    }
  }

  private static void onItemTooltip(final ItemTooltipEvent evt) {
    ClientEvents.addColytraTooltip(evt.getItemStack(), evt.getToolTip());
  }

  private static void onRenderCape(final RenderCapeEvent evt) {

    if (!ElytraTag.getElytra(evt.getEntity().getItemBySlot(EquipmentSlot.CHEST)).isEmpty()) {
      evt.setCanceled(true);
    }
  }
}
