package top.theillusivec4.colytra.common.config;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AutoConfigPlugin {

  private static ColytraConfigData configData;

  public static void init() {
    configData =
        AutoConfig.register(ColytraConfigData.class, JanksonConfigSerializer::new).getConfig();
  }

  public static void bake() {
    ColytraConfig.colytraMode = configData.colytraMode;
    List<Item> items = new ArrayList<>();

    for (String s : configData.permissionList) {
      Registry.ITEM.getOrEmpty(Identifier.tryParse(s)).ifPresent(items::add);
    }
    ColytraConfig.permissionList = items;
    ColytraConfig.permissionMode = configData.permissionMode;
  }

  public static Screen getConfigScreen(Screen screen) {
    return AutoConfig.getConfigScreen(ColytraConfigData.class, screen).get();
  }
}
