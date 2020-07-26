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

package top.theillusivec4.colytra.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import top.theillusivec4.colytra.Colytra;
import top.theillusivec4.colytra.server.ColytraServerConfig;

public class ElytraNBT {

  public static final String ELYTRA_TAG = Colytra.MODID + ":ElytraUpgrade";

  public static boolean hasUpgrade(ItemStack stack) {
    return stack.getChildTag(ELYTRA_TAG) != null;
  }

  public static ItemStack getElytra(ItemStack stack) {
    CompoundNBT tag = stack.getChildTag(ELYTRA_TAG);
    return tag != null ? ItemStack.read(tag) : ItemStack.EMPTY;
  }

  public static void setElytra(ItemStack chestStack, ItemStack elytraStack) {
    chestStack.getOrCreateTag().put(ELYTRA_TAG, elytraStack.write(new CompoundNBT()));
  }

  public static void damageElytra(LivingEntity livingEntity, ItemStack chestStack,
      ItemStack elytraStack, int amount) {
    ColytraServerConfig.ColytraMode colytraMode = ColytraServerConfig.colytraMode;

    if (colytraMode == ColytraServerConfig.ColytraMode.NORMAL) {
      elytraStack.damageItem(amount, livingEntity,
          damager -> damager.sendBreakAnimation(EquipmentSlotType.CHEST));
    } else if (colytraMode == ColytraServerConfig.ColytraMode.UNISON) {
      LazyOptional<IEnergyStorage> energyStorage = chestStack
          .getCapability(CapabilityEnergy.ENERGY);

      energyStorage
          .ifPresent(energy -> energy.extractEnergy(ColytraServerConfig.energyUsage, false));

      if (!energyStorage.isPresent()) {
        chestStack.damageItem(amount, livingEntity,
            damager -> damager.sendBreakAnimation(EquipmentSlotType.CHEST));
      }
    }
    setElytra(chestStack, elytraStack);
  }

  public static boolean isUseable(ItemStack chestStack, ItemStack elytraStack) {

    if (elytraStack.isEmpty()) {
      return false;
    }
    ColytraServerConfig.ColytraMode colytraMode = ColytraServerConfig.colytraMode;

    if (colytraMode == ColytraServerConfig.ColytraMode.NORMAL) {
      return elytraStack.getItem() instanceof ElytraItem && ElytraItem.isUsable(elytraStack);
    } else if (colytraMode == ColytraServerConfig.ColytraMode.UNISON) {
      LazyOptional<IEnergyStorage> energyStorage = chestStack
          .getCapability(CapabilityEnergy.ENERGY);

      if (energyStorage.isPresent()) {
        return energyStorage.map(energy -> energy.canExtract()
            && energy.getEnergyStored() > ColytraServerConfig.energyUsage).orElse(false);
      } else {
        return !chestStack.isDamageable() || (chestStack.getDamage()
            < chestStack.getMaxDamage() - 1);
      }
    }
    return true;
  }
}
