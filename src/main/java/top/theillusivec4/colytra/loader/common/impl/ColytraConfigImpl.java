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
