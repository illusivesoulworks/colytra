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
