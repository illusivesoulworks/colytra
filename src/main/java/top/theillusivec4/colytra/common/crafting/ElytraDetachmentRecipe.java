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

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import top.theillusivec4.colytra.common.config.ColytraConfig;
import top.theillusivec4.colytra.common.util.ColytraNbt;

public class ElytraDetachmentRecipe extends SpecialCraftingRecipe {

  public static final SpecialRecipeSerializer<ElytraDetachmentRecipe> CRAFTING_DETACH_ELYTRA = new SpecialRecipeSerializer<>(
      ElytraDetachmentRecipe::new);

  public ElytraDetachmentRecipe(Identifier id) {
    super(id);
  }

  @Override
  public boolean matches(CraftingInventory inv, World worldIn) {

    if (ColytraConfig.colytraMode != ColytraConfig.ColytraMode.NORMAL) {
      return false;
    }
    ItemStack itemstack = ItemStack.EMPTY;

    for (int i = 0; i < inv.size(); ++i) {
      ItemStack currentStack = inv.getStack(i);

      if (currentStack.isEmpty()) {
        continue;
      }

      if (!itemstack.isEmpty() || !ColytraNbt.hasUpgrade(currentStack)) {
        return false;
      }
      itemstack = currentStack;
    }
    return !itemstack.isEmpty();
  }

  @Override
  public ItemStack craft(CraftingInventory inv) {
    ItemStack itemstack = ItemStack.EMPTY;

    for (int k = 0; k < inv.size(); ++k) {
      ItemStack currentStack = inv.getStack(k);

      if (!currentStack.isEmpty()) {

        if (!itemstack.isEmpty()) {
          return ItemStack.EMPTY;
        }
        itemstack = ColytraNbt.getElytra(currentStack);
      }
    }

    if (!itemstack.isEmpty()) {
      return itemstack;
    } else {
      return ItemStack.EMPTY;
    }
  }

  @Override
  public DefaultedList<ItemStack> getRemainder(CraftingInventory inv) {
    DefaultedList<ItemStack> nonnulllist = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);

    for (int i = 0; i < nonnulllist.size(); ++i) {
      ItemStack currentStack = inv.getStack(i);

      if (!currentStack.isEmpty() && ColytraNbt.hasUpgrade(currentStack)) {
        currentStack.removeSubTag(ColytraNbt.ELYTRA_TAG);
        nonnulllist.set(i, currentStack.copy());
        break;
      }
    }
    return nonnulllist;
  }

  @Override
  public boolean fits(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CRAFTING_DETACH_ELYTRA;
  }
}
