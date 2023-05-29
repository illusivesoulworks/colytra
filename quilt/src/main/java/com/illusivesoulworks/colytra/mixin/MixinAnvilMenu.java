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

package com.illusivesoulworks.colytra.mixin;

import com.illusivesoulworks.colytra.common.CommonEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu extends ItemCombinerMenu {

  @Shadow
  private String itemName;
  @Shadow
  private int repairItemCountCost;
  @Shadow
  @Final
  private DataSlot cost;

  public MixinAnvilMenu(@Nullable MenuType<?> menuType, int i, Inventory inventory,
                        ContainerLevelAccess containerLevelAccess) {
    super(menuType, i, inventory, containerLevelAccess);
  }

  @Inject(at = @At("HEAD"), method = "createResult", cancellable = true)
  private void colytra$createResult(CallbackInfo ci) {
    ItemStack input = this.inputSlots.getItem(0);
    ItemStack ingredient = this.inputSlots.getItem(1);
    Triple<ItemStack, Integer, Integer> result =
        CommonEvents.repairColytra(input, ingredient, this.itemName);

    if (!result.getLeft().isEmpty()) {
      this.repairItemCountCost = result.getMiddle();
      this.cost.set(result.getRight());
      this.resultSlots.setItem(0, result.getLeft());
      this.broadcastChanges();
      ci.cancel();
    }
  }
}
