package top.theillusivec4.colytra.common.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;

public class ColytraConfig {

  public static ColytraConfig.PermissionMode permissionMode = PermissionMode.BLACKLIST;
  public static List<Item> permissionList = new ArrayList<>();
  public static ColytraMode colytraMode = ColytraMode.NORMAL;

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }

  public enum ColytraMode {
    NORMAL, UNISON, PERFECT
  }
}
