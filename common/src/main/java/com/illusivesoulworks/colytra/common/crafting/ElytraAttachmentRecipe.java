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

package com.illusivesoulworks.colytra.common.crafting;

import com.illusivesoulworks.colytra.common.ColytraConfig;
import com.illusivesoulworks.colytra.common.ElytraTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class ElytraAttachmentRecipe extends CustomRecipe {

  public static final List<Item> VALID_ITEMS = new ArrayList<>();
  public static final RecipeSerializer<ElytraAttachmentRecipe> CRAFTING_ATTACH_ELYTRA =
      new SimpleCraftingRecipeSerializer<>(ElytraAttachmentRecipe::new);

  public ElytraAttachmentRecipe(ResourceLocation id, CraftingBookCategory category) {
    super(id, category);
  }

  private static void mergeEnchantments(ItemStack source, ItemStack destination) {
    Map<Enchantment, Integer> mapSource = EnchantmentHelper.getEnchantments(source);
    Map<Enchantment, Integer> mapDestination = EnchantmentHelper.getEnchantments(destination);

    for (Enchantment enchantment : mapSource.keySet()) {

      if (enchantment == null || !enchantment.canEnchant(destination)) {
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
  public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level worldIn) {
    ItemStack itemstack = ItemStack.EMPTY;
    ItemStack elytra = ItemStack.EMPTY;

    for (int i = 0; i < inv.getContainerSize(); ++i) {
      ItemStack currentStack = inv.getItem(i);

      if (currentStack.isEmpty()) {
        continue;
      }

      if (isValid(currentStack)) {

        if (!itemstack.isEmpty() || ElytraTag.hasUpgrade(currentStack)) {
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
  public ItemStack assemble(@Nonnull CraftingContainer inv,
                            @Nonnull RegistryAccess registryAccess) {
    ItemStack itemstack = ItemStack.EMPTY;
    ItemStack elytra = ItemStack.EMPTY;

    for (int k = 0; k < inv.getContainerSize(); ++k) {
      ItemStack currentStack = inv.getItem(k);

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

      if (ColytraConfig.SERVER.fusionType.get() != ColytraConfig.FusionType.NORMAL) {
        mergeEnchantments(elytra, itemstack);
        itemstack.setRepairCost(elytra.getBaseRepairCost() + itemstack.getBaseRepairCost());
        Component name = elytra.getHoverName();
        boolean hasCustomName = elytra.hasCustomHoverName();
        elytra = new ItemStack(Items.ELYTRA);

        if (hasCustomName) {
          elytra.setHoverName(name);
        }
      }
      itemstack.getOrCreateTag().put(ElytraTag.ELYTRA_TAG, elytra.save(new CompoundTag()));
      return itemstack;
    } else {
      return ItemStack.EMPTY;
    }
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Nonnull
  @Override
  public RecipeSerializer<?> getSerializer() {
    return CRAFTING_ATTACH_ELYTRA;
  }

  private static boolean isValid(ItemStack stack) {
    ColytraConfig.ListType listType = ColytraConfig.SERVER.itemListType.get();
    boolean isBlacklist = listType == ColytraConfig.ListType.DENY;
    return isBlacklist != VALID_ITEMS.contains(stack.getItem()) &&
        Mob.getEquipmentSlotForItem(stack) == EquipmentSlot.CHEST &&
        !(stack.getItem() instanceof ElytraItem);
  }
}
