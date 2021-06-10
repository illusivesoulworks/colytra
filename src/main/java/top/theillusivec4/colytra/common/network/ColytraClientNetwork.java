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

package top.theillusivec4.colytra.common.network;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.colytra.common.ColytraMod;
import top.theillusivec4.colytra.common.config.ColytraConfig;

public class ColytraClientNetwork {

  public static void setup() {

    if (ColytraMod.isConfigLoaded) {
      ClientPlayNetworking.registerGlobalReceiver(ColytraPackets.SYNC_CONFIG,
          (client, handler, buf, responseSender) -> {
            ColytraConfig.ColytraMode mode =
                ColytraConfig.ColytraMode.valueOf(buf.readString(32767));
            ColytraConfig.PermissionMode permissionMode =
                ColytraConfig.PermissionMode.valueOf(buf.readString(32767));
            int size = buf.readInt();
            List<Item> items = new ArrayList<>();

            for (int i = 0; i < size; i++) {
              String id = buf.readString(32767);
              Registry.ITEM.getOrEmpty(Identifier.tryParse(id)).ifPresent(items::add);
            }
            client.execute(() -> {
              ColytraConfig.colytraMode = mode;
              ColytraConfig.permissionMode = permissionMode;
              ColytraConfig.permissionList = items;
            });
          });
    }
  }
}
