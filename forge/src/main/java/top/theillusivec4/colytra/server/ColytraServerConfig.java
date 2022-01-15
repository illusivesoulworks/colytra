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

package top.theillusivec4.colytra.server;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.colytra.Colytra;

public class ColytraServerConfig {

  public static final ForgeConfigSpec serverSpec;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + Colytra.MOD_ID + ".config.";

  static {
    final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Server::new);
    serverSpec = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static PermissionMode permissionMode;
  public static List<Item> permissionList;
  public static ColytraMode colytraMode;
  public static int energyUsage;

  public static void bake() {
    permissionMode = SERVER.permissionMode.get();
    colytraMode = SERVER.colytraMode.get();
    energyUsage = SERVER.energyUsage.get();

    List<? extends String> configItems = SERVER.permissionList.get();
    List<Item> items = new ArrayList<>();
    configItems.forEach(id -> {
      Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));

      if (item != null) {
        items.add(item);
      }
    });
    permissionList = items;
  }

  public static class Server {

    public final ForgeConfigSpec.EnumValue<PermissionMode> permissionMode;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> permissionList;
    public final ForgeConfigSpec.EnumValue<ColytraMode> colytraMode;
    public final ForgeConfigSpec.IntValue energyUsage;

    public Server(ForgeConfigSpec.Builder builder) {

      builder.push("server");

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
    }
  }

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }

  public enum ColytraMode {
    NORMAL, UNISON, PERFECT
  }
}
