package top.theillusivec4.colytra.loader.common;

import java.util.ArrayList;
import java.util.List;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import top.theillusivec4.colytra.core.Colytra;
import top.theillusivec4.colytra.core.base.ColytraConfig.ColytraMode;
import top.theillusivec4.colytra.core.base.ColytraConfig.PermissionMode;

@Config(name = Colytra.MODID)
public class ConfigDataHolder implements ConfigData {

  @Tooltip(count = 3)
  @Comment("Sets whether the permission list is a blacklist or whitelist\n"
      + "BLACKLIST: Only specified items cannot be combined with an elytra\n"
      + "WHITELIST: Only specified items can be combined with an elytra")
  public PermissionMode permissionMode = PermissionMode.BLACKLIST;

  @Tooltip
  @Comment("List of items by registry name to be blacklisted/whitelisted based on Permission Mode")
  public List<String> permissionList = new ArrayList<>();

  @Tooltip(count = 4)
  @Comment("Sets how the elytra chestplates will behave\n"
      + "NORMAL: Elytras will exist separately from the chestplate, able to be separated later\n"
      + "UNISON: Elytras will fuse completely with the chestplate, unable to be separated\n"
      + "PERFECT: Elytras will fuse completely with the chestplate and flying will not use durability")
  public ColytraMode colytraMode = ColytraMode.NORMAL;
}
