/*
 * Copyright (C) 2017-2022 Illusive Soulworks
 *
 * Colytra is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Colytra is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Colytra. If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.colytra.common;

import com.illusivesoulworks.colytra.ColytraConstants;
import com.illusivesoulworks.colytra.common.crafting.ElytraAttachmentRecipe;
import com.illusivesoulworks.colytra.platform.Services;
import com.illusivesoulworks.spectrelib.config.SpectreConfigSpec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.tuple.Pair;

public class ColytraConfig {

  public static final SpectreConfigSpec SERVER_SPEC;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + ColytraConstants.MOD_ID + ".config.";

  static {
    final Pair<Server, SpectreConfigSpec> specPair = new SpectreConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static class Server {

    public final SpectreConfigSpec.EnumValue<PermissionMode> permissionMode;
    public final SpectreConfigSpec.ConfigValue<List<? extends String>> permissionList;
    public final SpectreConfigSpec.EnumValue<ColytraMode> colytraMode;
    public final SpectreConfigSpec.IntValue energyUsage;

    public Server(SpectreConfigSpec.Builder builder) {
      builder.push("general");

      permissionMode = builder
          .comment("Sets whether the permission list is a blacklist or whitelist")
          .translation(CONFIG_PREFIX + "permissionMode")
          .defineEnum("permissionMode", PermissionMode.BLACKLIST);

      permissionList = builder.comment(
              "List of items by registry name to be blacklisted/whitelisted based on Permission Mode")
          .translation(CONFIG_PREFIX + "permissionList")
          .defineList("permissionList", new ArrayList<>(), s -> s instanceof String);

      colytraMode = builder.comment("Sets how the elytra chestplates will behave\n"
              + "NORMAL: Elytras will exist separately from the chestplate, "
              + "able to be separated later\n"
              + "UNISON: Elytras will fuse completely with the chestplate, "
              + "unable to be separated\n"
              + "PERFECT: Elytras will fuse completely with the chestplate "
              + "and flying will not use durability").translation(CONFIG_PREFIX + "colytraMode")
          .defineEnum("colytraMode", ColytraMode.NORMAL);

      energyUsage = builder.comment(
              "How much energy per second elytra flight uses if Unison mode is active and the "
                  + "chestplate uses energy").translation(CONFIG_PREFIX + "energyUsage")
          .defineInRange("energyUsage", 1000, 0, Integer.MAX_VALUE);

      builder.pop();
    }
  }

  public static void reload() {
    ElytraAttachmentRecipe.VALID_ITEMS.clear();
    SERVER.permissionList.get().forEach(id -> {
      Item item = Services.PLATFORM.getItem(ResourceLocation.tryParse(id));

      if (item != null) {
        ElytraAttachmentRecipe.VALID_ITEMS.add(item);
      }
    });
  }

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }

  public enum ColytraMode {
    NORMAL, UNISON, PERFECT
  }
}
