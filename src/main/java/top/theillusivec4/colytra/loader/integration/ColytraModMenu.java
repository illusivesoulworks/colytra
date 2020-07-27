package top.theillusivec4.colytra.loader.integration;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import top.theillusivec4.colytra.loader.common.ConfigDataHolder;

public class ColytraModMenu implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return screen -> AutoConfig.getConfigScreen(ConfigDataHolder.class, screen).get();
  }
}
