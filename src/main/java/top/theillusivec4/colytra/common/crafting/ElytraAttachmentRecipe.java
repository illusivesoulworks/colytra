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

package top.theillusivec4.colytra.common.crafting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import top.theillusivec4.colytra.common.ElytraNBT;
import top.theillusivec4.colytra.server.ColytraServerConfig;

public class ElytraAttachmentRecipe extends SpecialRecipe {

  public static final SpecialRecipeSerializer<ElytraAttachmentRecipe> CRAFTING_ATTACH_ELYTRA = new SpecialRecipeSerializer<>(
      ElytraAttachmentRecipe::new);

  public ElytraAttachmentRecipe(ResourceLocation id) {
    super(id);
  }

  private static void mergeEnchantments(ItemStack source, ItemStack destination) {
    Map<Enchantment, Integer> mapSource = EnchantmentHelper.getEnchantments(source);
    Map<Enchantment, Integer> mapDestination = EnchantmentHelper.getEnchantments(destination);

    for (Enchantment enchantment : mapSource.keySet()) {

      if (enchantment == null || !enchantment.canApply(destination)) {
        continue;
      }
      int destLevel = mapDestination.getOrDefault(enchantment, 0);
      int srcLevel = mapSource.get(enchantment);
      srcLevel = destLevel == srcLevel ? srcLevel + 1 : Math.max(srcLevel, destLevel);

      for (Enchantment destEnch : mapDestination.keySet()) {

        if (enchantment != destEnch && !destEnch.isCompatibleWith(enchantment)) {
          return;
        }
      }

      if (srcLevel > enchantment.getMaxLevel()) {
        srcLevel = enchantment.getMaxLevel();
      }
      mapDestination.put(enchantment, srcLevel);
    }
    EnchantmentHelper.setEnchantments(mapDestination, destination);
    EnchantmentHelper.setEnchantments(new HashMap<>(), source);
  }

  @Override
  public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World worldIn) {
    ItemStack itemstack = ItemStack.EMPTY;
    ItemStack elytra = ItemStack.EMPTY;

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack currentStack = inv.getStackInSlot(i);

      if (currentStack.isEmpty()) {
        continue;
      }

      if (isValid(currentStack)) {

        if (!itemstack.isEmpty() || ElytraNBT.hasUpgrade(currentStack)) {
          return false;
        }
        itemstack = currentStack;
      } else {

        if (!elytra.isEmpty() || !(currentStack.getItem() instanceof ElytraItem)) {
          return false;
        }
        elytra = currentStack;
      }
    }
    return !itemstack.isEmpty() && !elytra.isEmpty();
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
    ItemStack itemstack = ItemStack.EMPTY;
    ItemStack elytra = ItemStack.EMPTY;

    for (int k = 0; k < inv.getSizeInventory(); ++k) {
      ItemStack currentStack = inv.getStackInSlot(k);

      if (currentStack.isEmpty()) {
        continue;
      }

      if (isValid(currentStack)) {

        if (!itemstack.isEmpty()) {
          return ItemStack.EMPTY;
        }
        itemstack = currentStack.copy();
        itemstack.setCount(1);
      } else {

        if (!(currentStack.getItem() instanceof ElytraItem)) {
          return ItemStack.EMPTY;
        }
        elytra = currentStack.copy();
      }
    }

    if (!itemstack.isEmpty() && !elytra.isEmpty()) {

      if (ColytraServerConfig.colytraMode != ColytraServerConfig.ColytraMode.NORMAL) {
        mergeEnchantments(elytra, itemstack);
        itemstack.setRepairCost(elytra.getRepairCost() + itemstack.getRepairCost());
        ITextComponent name = elytra.getDisplayName();
        boolean hasCustomName = elytra.hasDisplayName();
        elytra = new ItemStack(Items.ELYTRA);

        if (hasCustomName) {
          elytra.setDisplayName(name);
        }
      }
      itemstack.getOrCreateTag().put(ElytraNBT.ELYTRA_TAG, elytra.write(new CompoundNBT()));
      return itemstack;
    } else {
      return ItemStack.EMPTY;
    }
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }

  @Nonnull
  @Override
  public IRecipeSerializer<?> getSerializer() {
    return CRAFTING_ATTACH_ELYTRA;
  }

  private static boolean isValid(ItemStack stack) {
    ColytraServerConfig.PermissionMode permissionMode = ColytraServerConfig.permissionMode;
    List<Item> permissionList = ColytraServerConfig.permissionList;
    boolean isBlacklist = permissionMode == ColytraServerConfig.PermissionMode.BLACKLIST;
    return isBlacklist != permissionList.contains(stack.getItem())
        && MobEntity.getSlotForItemStack(stack) == EquipmentSlotType.CHEST && !(stack
        .getItem() instanceof ElytraItem);
  }
}
