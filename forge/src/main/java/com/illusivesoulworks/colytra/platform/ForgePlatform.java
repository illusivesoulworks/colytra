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

package com.illusivesoulworks.colytra.platform;

import com.illusivesoulworks.colytra.common.ColytraConfig;
import com.illusivesoulworks.colytra.platform.services.IPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgePlatform implements IPlatform {

  @Override
  public Enchantment getEnchantment(ResourceLocation resourceLocation) {
    return ForgeRegistries.ENCHANTMENTS.getValue(resourceLocation);
  }

  @Override
  public Item getItem(ResourceLocation resourceLocation) {
    return ForgeRegistries.ITEMS.getValue(resourceLocation);
  }

  @Override
  public void extractEnergy(ItemStack stack) {
    LazyOptional<IEnergyStorage> energyStorage = stack.getCapability(CapabilityEnergy.ENERGY);
    energyStorage.ifPresent(
        energy -> energy.extractEnergy(ColytraConfig.SERVER.energyUsage.get(), false));
  }

  @Override
  public boolean hasEnergy(ItemStack stack) {
    return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
  }

  @Override
  public boolean canExtractEnergy(ItemStack stack) {
    LazyOptional<IEnergyStorage> energyStorage = stack.getCapability(CapabilityEnergy.ENERGY);
    return energyStorage.map(energy -> energy.canExtract() &&
        energy.getEnergyStored() > ColytraConfig.SERVER.energyUsage.get()).orElse(false);
  }
}
