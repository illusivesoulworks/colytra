package top.theillusivec4.colytra.loader.common.impl;

import java.util.List;
import net.minecraft.item.Item;
import top.theillusivec4.colytra.core.base.ColytraConfig;

public class ColytraConfigImpl implements ColytraConfig {

  private List<Item> permissionList;
  private PermissionMode permissionMode;
  private ColytraMode colytraMode;

  @Override
  public void setPermissionMode(PermissionMode mode) {
    this.permissionMode = mode;
  }

  @Override
  public PermissionMode getPermissionMode() {
    return this.permissionMode;
  }

  @Override
  public void setPermissionList(List<Item> items) {
    this.permissionList = items;
  }

  @Override
  public List<Item> getPermissionList() {
    return this.permissionList;
  }

  @Override
  public void setColytraMode(ColytraMode mode) {
    this.colytraMode = mode;
  }

  @Override
  public ColytraMode getColytraMode() {
    return this.colytraMode;
  }
}
