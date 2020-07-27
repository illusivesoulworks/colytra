package top.theillusivec4.colytra.loader.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.colytra.core.Colytra;
import top.theillusivec4.colytra.core.base.ColytraConfig;
import top.theillusivec4.colytra.loader.common.ColytraMod;
import top.theillusivec4.colytra.loader.common.ConfigDataHolder;

public class NetworkPackets {

  public static final Identifier SYNC_CONFIG = new Identifier(Colytra.MODID, "sync_config");

  public static PacketByteBuf writeConfigPacket(PacketByteBuf buf) {
    ConfigDataHolder config = ColytraMod.config;
    buf.writeString(config.colytraMode.toString());
    buf.writeString(config.permissionMode.toString());
    List<String> itemsList = config.permissionList;
    buf.writeInt(itemsList.size());

    for (String item : itemsList) {
      buf.writeString(item);
    }
    return buf;
  }

  public static void readConfigPacket(ColytraConfig config, PacketByteBuf buf) {
    config.setColytraMode(ColytraConfig.ColytraMode.valueOf(buf.readString(32767)));
    config.setPermissionMode(ColytraConfig.PermissionMode.valueOf(buf.readString(32767)));
    int size = buf.readInt();
    List<Item> items = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      String id = buf.readString(32767);
      Item item = Registry.ITEM.get(new Identifier(id));

      if (item != Items.AIR) {
        items.add(item);
      }
    }
    config.setPermissionList(items);
  }
}
