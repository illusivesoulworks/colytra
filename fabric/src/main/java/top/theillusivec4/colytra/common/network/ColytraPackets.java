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

import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.colytra.common.ColytraMod;
import top.theillusivec4.colytra.common.config.ColytraConfig;

public class ColytraPackets {

  public static final Identifier SYNC_CONFIG = new Identifier(ColytraMod.MOD_ID, "sync_config");

  public static PacketByteBuf writeConfigPacket() {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeString(ColytraConfig.colytraMode.toString());
    buf.writeString(ColytraConfig.permissionMode.toString());
    List<Item> itemsList = ColytraConfig.permissionList;
    buf.writeInt(itemsList.size());

    for (Item item : itemsList) {
      buf.writeString(Registry.ITEM.getId(item).toString());
    }
    return buf;
  }
}
