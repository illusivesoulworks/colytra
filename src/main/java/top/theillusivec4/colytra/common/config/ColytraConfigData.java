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

package top.theillusivec4.colytra.common.config;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import top.theillusivec4.colytra.common.ColytraMod;

@Config(name = ColytraMod.MOD_ID)
public class ColytraConfigData implements ConfigData {

  @ConfigEntry.Gui.Tooltip(count = 3)
  @Comment("""
      Sets whether the permission list is a blacklist or whitelist
      BLACKLIST: Only specified items cannot be combined with an elytra
      WHITELIST: Only specified items can be combined with an elytra""")
  public ColytraConfig.PermissionMode permissionMode = ColytraConfig.PermissionMode.BLACKLIST;

  @ConfigEntry.Gui.Tooltip
  @Comment("List of items by registry name to be blacklisted/whitelisted based on Permission Mode")
  public List<String> permissionList = new ArrayList<>();

  @ConfigEntry.Gui.Tooltip(count = 4)
  @Comment("""
      Sets how the elytra chestplates will behave
      NORMAL: Elytras will exist separately from the chestplate, able to be separated later
      UNISON: Elytras will fuse completely with the chestplate, unable to be separated
      PERFECT: Elytras will fuse completely with the chestplate and flying will not use durability""")
  public ColytraConfig.ColytraMode colytraMode = ColytraConfig.ColytraMode.NORMAL;
}
