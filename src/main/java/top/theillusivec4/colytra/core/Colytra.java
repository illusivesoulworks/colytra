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
