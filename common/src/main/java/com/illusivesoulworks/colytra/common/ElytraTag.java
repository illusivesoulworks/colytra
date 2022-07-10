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
import com.illusivesoulworks.colytra.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

public class ElytraTag {

  public static final String ELYTRA_TAG = ColytraConstants.MOD_ID + ":ElytraUpgrade";

  public static boolean hasUpgrade(ItemStack stack) {
    return stack.getTagElement(ELYTRA_TAG) != null;
  }

  public static ItemStack getElytra(ItemStack stack) {
    CompoundTag tag = stack.getTagElement(ELYTRA_TAG);
    return tag != null ? ItemStack.of(tag) : ItemStack.EMPTY;
  }

  public static void setElytra(ItemStack chestStack, ItemStack elytraStack) {
    chestStack.getOrCreateTag().put(ELYTRA_TAG, elytraStack.save(new CompoundTag()));
  }

  public static void damageElytra(LivingEntity livingEntity, ItemStack chestStack,
                                  ItemStack elytraStack, int amount) {
    ColytraConfig.ColytraMode colytraMode = ColytraConfig.SERVER.colytraMode.get();

    if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
      elytraStack.hurtAndBreak(amount, livingEntity,
          damager -> damager.broadcastBreakEvent(EquipmentSlot.CHEST));
    } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {

      if (Services.PLATFORM.hasEnergy(chestStack)) {
        Services.PLATFORM.extractEnergy(chestStack);
      } else {
        chestStack.hurtAndBreak(amount, livingEntity,
            damager -> damager.broadcastBreakEvent(EquipmentSlot.CHEST));
      }
    }
    setElytra(chestStack, elytraStack);
  }

  public static boolean isUseable(ItemStack chestStack, ItemStack elytraStack) {

    if (elytraStack.isEmpty()) {
      return false;
    }
    ColytraConfig.ColytraMode colytraMode = ColytraConfig.SERVER.colytraMode.get();

    if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
      return elytraStack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(elytraStack);
    } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {

      if (Services.PLATFORM.hasEnergy(chestStack)) {
        return Services.PLATFORM.canExtractEnergy(chestStack);
      } else {
        return !chestStack.isDamageableItem() || (chestStack.getDamageValue()
            < chestStack.getMaxDamage() - 1);
      }
    }
    return true;
  }
}
