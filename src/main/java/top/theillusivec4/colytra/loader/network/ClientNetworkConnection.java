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

package top.theillusivec4.colytra.loader.network;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import top.theillusivec4.colytra.core.Colytra;
import top.theillusivec4.colytra.core.base.ColytraConfig;
import top.theillusivec4.colytra.loader.common.impl.ColytraConfigImpl;

public class ClientNetworkConnection {

  public static void setup() {
    ClientSidePacketRegistry.INSTANCE
        .register(NetworkPackets.SYNC_CONFIG, (((packetContext, packetByteBuf) -> {
          final ColytraConfig config = new ColytraConfigImpl();
          NetworkPackets.readConfigPacket(config, packetByteBuf);
          packetContext.getTaskQueue().execute(() -> Colytra.setConfig(config));
        })));
  }
}
