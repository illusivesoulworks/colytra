package top.theillusivec4.colytra.common.crafting;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

public class ElytraAttachmentRecipe extends SpecialRecipe {

  public static final SpecialRecipeSerializer<ElytraAttachmentRecipe> CRAFTING_ATTACH_ELYTRA =
      new SpecialRecipeSerializer<>(ElytraAttachmentRecipe::new);

  public ElytraAttachmentRecipe(ResourceLocation id) {

    super(id);
  }

  private static void mergeEnchantments(ItemStack source, ItemStack destination) {

    Map<Enchantment, Integer> mapSource = EnchantmentHelper.getEnchantments(source);
    Map<Enchantment, Integer> mapDestination = EnchantmentHelper.getEnchantments(destination);

    for (Enchantment srcEnch : mapSource.keySet()) {

      if (srcEnch == null) {
        continue;
      }

      if (!srcEnch.canApply(destination)) {
        return;
      }

      int destLevel = mapDestination.getOrDefault(srcEnch, 0);
      int srcLevel = mapSource.get(srcEnch);
      srcLevel = destLevel == srcLevel ? srcLevel + 1 : Math.max(srcLevel, destLevel);

      for (Enchantment destEnch : mapDestination.keySet()) {

        if (srcEnch != destEnch && !destEnch.isCompatibleWith(srcEnch)) {
          return;
        }
      }

      if (srcLevel > srcEnch.getMaxLevel()) {
        srcLevel = srcEnch.getMaxLevel();
      }
      mapDestination.put(srcEnch, srcLevel);
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

      if (CapabilityElytra.getCapability(currentStack).isPresent()) {

        if (!itemstack.isEmpty()) {
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

      LazyOptional<CapabilityElytra.IElytra> capability =
          CapabilityElytra.getCapability(currentStack);

      if (capability.isPresent()) {

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
      final ItemStack elytraStack = elytra;

      if (ColytraConfig.getColytraMode() != ColytraConfig.ColytraMode.NORMAL) {
        mergeEnchantments(elytraStack, itemstack);
        itemstack.setRepairCost(elytraStack.getRepairCost() + itemstack.getRepairCost());
      }
      CapabilityElytra.getCapability(itemstack)
          .ifPresent(elytraHolder -> elytraHolder.setElytra(elytraStack));
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
}
