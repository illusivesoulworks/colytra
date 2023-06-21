package com.illusivesoulworks.colytra;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;

public class ColytraConfigInitializer implements SpectreLibInitializer {

  @Override
  public void onInitializeConfig() {
    ColytraCommonMod.init();
  }
}
