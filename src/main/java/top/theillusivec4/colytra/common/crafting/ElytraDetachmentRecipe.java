package top.theillusivec4.colytra.common.crafting;

import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.ElytraNBT;

public class ElytraDetachmentRecipe extends SpecialRecipe {

  public static final SpecialRecipeSerializer<ElytraDetachmentRecipe> CRAFTING_DETACH_ELYTRA =
      new SpecialRecipeSerializer<>(ElytraDetachmentRecipe::new);

  public ElytraDetachmentRecipe(ResourceLocation id) {
    super(id);
  }

  @Override
  public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World worldIn) {

    if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.NORMAL) {
      return false;
    }
    ItemStack itemstack = ItemStack.EMPTY;

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack currentStack = inv.getStackInSlot(i);

      if (currentStack.isEmpty()) {
        continue;
      }

      if (!itemstack.isEmpty() || !ElytraNBT.hasUpgrade(currentStack)) {
        return false;
      }
      itemstack = currentStack;
    }
    return !itemstack.isEmpty();
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
    ItemStack itemstack = ItemStack.EMPTY;

    for (int k = 0; k < inv.getSizeInventory(); ++k) {
      ItemStack currentStack = inv.getStackInSlot(k);

      if (!currentStack.isEmpty()) {

        if (!itemstack.isEmpty()) {
          return ItemStack.EMPTY;
        }
        itemstack = ElytraNBT.getElytra(currentStack);
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
  public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
    NonNullList<ItemStack> nonnulllist =
        NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

    for (int i = 0; i < nonnulllist.size(); ++i) {
      ItemStack currentStack = inv.getStackInSlot(i);

      if (!currentStack.isEmpty() && ElytraNBT.hasUpgrade(currentStack)) {
        currentStack.removeChildTag(ElytraNBT.ELYTRA_TAG);
        nonnulllist.set(i, currentStack.copy());
        break;
      }
    }
    return nonnulllist;
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }

  @Nonnull
  @Override
  public IRecipeSerializer<?> getSerializer() {
    return CRAFTING_DETACH_ELYTRA;
  }
}
