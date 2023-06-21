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

    public final SpectreConfigSpec.EnumValue<ListType> itemListType;
    public final SpectreConfigSpec.ConfigValue<List<? extends String>> itemsList;
    public final SpectreConfigSpec.EnumValue<FusionType> fusionType;
    public final SpectreConfigSpec.IntValue energyCost;

    public Server(SpectreConfigSpec.Builder builder) {
      itemListType = builder
          .comment("Determines if itemsList contains allowed items or denied items.")
          .translation(CONFIG_PREFIX + "itemListType")
          .defineEnum("itemListType", ListType.DENY);

      itemsList = builder.comment(
              "The items for crafting with elytras.")
          .translation(CONFIG_PREFIX + "itemsList")
          .defineList("itemsList", new ArrayList<>(),
              s -> s instanceof String str && ResourceLocation.isValidResourceLocation(str));

      fusionType = builder.comment("Determines how the combined elytra chestplates will behave.\n"
              + "NORMAL: Elytras will exist separately from the chestplate, "
              + "able to be separated later.\n"
              + "UNISON: Elytras will fuse completely with the chestplate, "
              + "unable to be separated.\n"
              + "PERFECT: Elytras will fuse completely with the chestplate "
              + "and flying will not use durability.").translation(CONFIG_PREFIX + "fusionType")
          .defineEnum("fusionType", FusionType.NORMAL);

      energyCost = builder.comment(
              "The energy cost per second of fall flying if fusionType is UNISON and the "
                  + "chestplate uses energy.").translation(CONFIG_PREFIX + "energyCost")
          .defineInRange("energyCost", 1000, 0, Integer.MAX_VALUE);
    }
  }

  public static void reload() {
    ElytraAttachmentRecipe.VALID_ITEMS.clear();
    SERVER.itemsList.get().forEach(id -> {
      Item item = Services.PLATFORM.getItem(ResourceLocation.tryParse(id));

      if (item != null) {
        ElytraAttachmentRecipe.VALID_ITEMS.add(item);
      }
    });
  }

  public enum ListType {
    DENY, ALLOW
  }

  public enum FusionType {
    NORMAL, UNISON, PERFECT
  }
}
