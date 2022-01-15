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

package top.theillusivec4.colytra.common.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import top.theillusivec4.colytra.common.ColytraMod;
import top.theillusivec4.colytra.common.config.ColytraConfig;

public class ColytraNbt {

  public static final String ELYTRA_TAG = ColytraMod.MOD_ID + ":ElytraUpgrade";

  public static boolean hasUpgrade(ItemStack stack) {
    return stack.getSubNbt(ELYTRA_TAG) != null;
  }

  public static ItemStack getElytra(ItemStack stack) {
    NbtCompound tag = stack.getSubNbt(ELYTRA_TAG);
    return tag != null ? ItemStack.fromNbt(tag) : ItemStack.EMPTY;
  }

  public static void setElytra(ItemStack chestStack, ItemStack elytraStack) {
    chestStack.getOrCreateNbt().put(ELYTRA_TAG, elytraStack.writeNbt(new NbtCompound()));
  }

  public static void damageElytra(LivingEntity livingEntity, ItemStack chestStack,
                                  ItemStack elytraStack, int amount) {
    ColytraConfig.ColytraMode mode = ColytraConfig.colytraMode;

    if (mode == ColytraConfig.ColytraMode.NORMAL) {
      elytraStack.damage(amount, livingEntity,
          damager -> damager.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
    } else if (mode == ColytraConfig.ColytraMode.UNISON) {
      chestStack.damage(amount, livingEntity,
          damager -> damager.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
    }
    setElytra(chestStack, elytraStack);
  }

  public static boolean isUseable(ItemStack chestStack, ItemStack elytraStack) {

    if (elytraStack.isEmpty()) {
      return false;
    }
    ColytraConfig.ColytraMode colytraMode = ColytraConfig.colytraMode;

    if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
      return elytraStack.getItem() instanceof ElytraItem && ElytraItem.isUsable(elytraStack);
    } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {
      return !chestStack.isDamageable() || (chestStack.getDamage() < chestStack.getMaxDamage() - 1);
    }
    return true;
  }
}
