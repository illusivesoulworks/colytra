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

package top.theillusivec4.colytra.loader.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import top.theillusivec4.caelus.api.event.RenderElytraCallback;
import top.theillusivec4.colytra.core.util.ElytraTag;
import top.theillusivec4.colytra.loader.network.ClientNetworkConnection;

public class ClientColytraMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ClientNetworkConnection.setup();
    RenderElytraCallback.EVENT.register(((playerEntity, renderElytraInfo) -> {
      ItemStack stack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);
      ItemStack elytraStack = ElytraTag.getElytra(stack);

      if (!elytraStack.isEmpty()) {
        renderElytraInfo.activateRender();

        if (elytraStack.hasGlint()) {
          renderElytraInfo.activateGlow();
        }
      }
    }));
  }
}
