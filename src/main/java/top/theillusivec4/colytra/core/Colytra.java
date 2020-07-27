package top.theillusivec4.colytra.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.colytra.core.base.Accessor;
import top.theillusivec4.colytra.core.base.ColytraConfig;
import top.theillusivec4.colytra.core.base.RegistryFinder;

public class Colytra {

  public static final String MODID = "colytra";
  public static final Logger LOGGER = LogManager.getLogger();

  private static ColytraConfig config;
  private static Accessor accessor;
  private static RegistryFinder registryFinder;

  public static ColytraConfig getConfig() {
    return config;
  }

  public static void setConfig(ColytraConfig config) {
    Colytra.config = config;
  }

  public static Accessor getAccessor() {
    return accessor;
  }

  public static void setAccessor(Accessor accessor) {
    Colytra.accessor = accessor;
  }

  public static RegistryFinder getRegistryFinder() {
    return registryFinder;
  }

  public static void setRegistryFinder(RegistryFinder registryFinder) {
    Colytra.registryFinder = registryFinder;
  }
}
