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

import javax.annotation.Nonnull;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import top.theillusivec4.colytra.common.ElytraTag;
import top.theillusivec4.colytra.server.ColytraServerConfig;

public class ElytraDetachmentRecipe extends CustomRecipe {

  public static final SimpleRecipeSerializer<ElytraDetachmentRecipe> CRAFTING_DETACH_ELYTRA = new SimpleRecipeSerializer<>(
      ElytraDetachmentRecipe::new);

  public ElytraDetachmentRecipe(ResourceLocation id) {
    super(id);
  }

  @Override
  public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level worldIn) {

    if (ColytraServerConfig.colytraMode != ColytraServerConfig.ColytraMode.NORMAL) {
      return false;
    }
    ItemStack itemstack = ItemStack.EMPTY;

    for (int i = 0; i < inv.getContainerSize(); ++i) {
      ItemStack currentStack = inv.getItem(i);

      if (currentStack.isEmpty()) {
        continue;
      }

      if (!itemstack.isEmpty() || !ElytraTag.hasUpgrade(currentStack)) {
        return false;
      }
      itemstack = currentStack;
    }
    return !itemstack.isEmpty();
  }

  @Nonnull
  @Override
  public ItemStack assemble(@Nonnull CraftingContainer inv) {
    ItemStack itemstack = ItemStack.EMPTY;

    for (int k = 0; k < inv.getContainerSize(); ++k) {
      ItemStack currentStack = inv.getItem(k);

      if (!currentStack.isEmpty()) {

        if (!itemstack.isEmpty()) {
          return ItemStack.EMPTY;
        }
        itemstack = ElytraTag.getElytra(currentStack);
      }
    }

    if (!itemstack.isEmpty()) {
      return itemstack;
    } else {
      return ItemStack.EMPTY;
    }
  }

  @Nonnull
  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
    NonNullList<ItemStack> nonnulllist = NonNullList
        .withSize(inv.getContainerSize(), ItemStack.EMPTY);

    for (int i = 0; i < nonnulllist.size(); ++i) {
      ItemStack currentStack = inv.getItem(i);

      if (!currentStack.isEmpty() && ElytraTag.hasUpgrade(currentStack)) {
        currentStack.removeTagKey(ElytraTag.ELYTRA_TAG);
        nonnulllist.set(i, currentStack.copy());
        break;
      }
    }
    return nonnulllist;
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Nonnull
  @Override
  public RecipeSerializer<?> getSerializer() {
    return CRAFTING_DETACH_ELYTRA;
  }
}
