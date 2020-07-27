package top.theillusivec4.colytra.loader.common;

import java.util.ArrayList;
import java.util.List;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import top.theillusivec4.colytra.core.Colytra;
import top.theillusivec4.colytra.core.base.ColytraConfig.ColytraMode;
import top.theillusivec4.colytra.core.base.ColytraConfig.PermissionMode;

@Config(name = Colytra.MODID)
public class ConfigDataHolder implements ConfigData {

  public PermissionMode permissionMode = PermissionMode.BLACKLIST;
  public List<String> permissionList = new ArrayList<>();
  public ColytraMode colytraMode = ColytraMode.NORMAL;
}
