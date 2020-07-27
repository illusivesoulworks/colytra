package top.theillusivec4.colytra.core.base;

import java.util.List;
import net.minecraft.item.Item;

public interface ColytraConfig {

  void setPermissionMode(PermissionMode mode);

  PermissionMode getPermissionMode();

  void setPermissionList(List<Item> items);

  List<Item> getPermissionList();

  void setColytraMode(ColytraMode mode);

  ColytraMode getColytraMode();

  enum PermissionMode {
    BLACKLIST, WHITELIST
  }

  enum ColytraMode {
    NORMAL, UNISON, PERFECT
  }
}
