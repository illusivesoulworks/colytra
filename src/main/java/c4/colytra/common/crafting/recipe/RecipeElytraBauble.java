/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.common.crafting.recipe;

import c4.colytra.proxy.CommonProxy;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import vazkii.quark.vanity.feature.DyableElytra;

import javax.annotation.Nonnull;

public class RecipeElytraBauble extends ShapelessOreRecipe {

    public RecipeElytraBauble(ResourceLocation resourceLocation, Item input, ItemStack output) {
        super(resourceLocation, NonNullList.from(Ingredient.EMPTY, Ingredient.fromItem(input)), output);
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1){

        int damage = 0;
        NBTTagList enchants = new NBTTagList();
        ItemStack elytra = ItemStack.EMPTY;

        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack slot = var1.getStackInSlot(x);

            if (!slot.isEmpty())
            {
                if (slot.getItem() instanceof ItemElytra) {
                    damage = slot.getItemDamage();
                    enchants = slot.getEnchantmentTagList();
                    elytra = slot.copy();
                }
            }
        }

        ItemStack result = output.copy();
        result.setItemDamage(damage);
        if (!enchants.hasNoTags()) {
            result.setTagInfo("ench", enchants);
        }
        if (CommonProxy.quarkLoaded) {
            copyElytraColor(elytra, result);
        }

        return result;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Optional.Method(modid = "quark")
    private static void copyElytraColor(ItemStack input, ItemStack output) {
        if (input.hasTagCompound() && input.getTagCompound().hasKey(DyableElytra.TAG_ELYTRA_DYE)) {
            if (!output.hasTagCompound()) {
                output.setTagCompound(new NBTTagCompound());
            }
            output.getTagCompound().setInteger(DyableElytra.TAG_ELYTRA_DYE, input.getTagCompound().getInteger(DyableElytra.TAG_ELYTRA_DYE));
        }
    }
}
