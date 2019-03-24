package c4.colytra.common.crafting.recipe;

import c4.colytra.Colytra;
import c4.colytra.common.config.ConfigHandler;
import c4.colytra.proxy.CommonProxy;
import c4.colytra.util.ColytraUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.quark.vanity.feature.DyableElytra;

import javax.annotation.Nonnull;

public class RecipeSeparateColytra extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public RecipeSeparateColytra() {
        this.setRegistryName(Colytra.MODID, "separate_colytra");
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {

        if (!ConfigHandler.separateColytra) {
            return false;
        }
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);

            if (!itemstack1.isEmpty()) {

                if (!itemstack.isEmpty()) {
                    return false;
                }
                EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(itemstack1);

                if (slot == EntityEquipmentSlot.CHEST) {

                    if (!ColytraUtil.hasElytraUpgrade(itemstack1)) {
                        return false;
                    }
                    itemstack = itemstack1;
                }
            }
        }
        return !itemstack.isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1){
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < var1.getSizeInventory(); ++i) {
            ItemStack itemstack1 = var1.getStackInSlot(i);

            if (!itemstack1.isEmpty()) {

                if (!itemstack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(itemstack1);

                if (slot == EntityEquipmentSlot.CHEST) {

                    if (!ColytraUtil.hasElytraUpgrade(itemstack1)) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack1;
                }
            }
        }

        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack output = new ItemStack(Items.ELYTRA);
        NBTTagCompound compound = itemstack.getSubCompound("Elytra Upgrade");
        output.setItemDamage(output.getMaxDamage() - compound.getInteger("Durability"));

        if (CommonProxy.quarkLoaded) {
            copyElytraColor(output, itemstack);
        }
        return output;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);

            if (!itemstack1.isEmpty()) {

                if (!itemstack.isEmpty()) {
                    return ret;
                }
                EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(itemstack1);

                if (slot == EntityEquipmentSlot.CHEST) {

                    if (!ColytraUtil.hasElytraUpgrade(itemstack1)) {
                        return ret;
                    }
                    itemstack1.removeSubCompound("Elytra Upgrade");
                    ret.set(i, itemstack1.copy());
                }
            }
        }
        return ret;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Optional.Method(modid = "quark")
    private static void copyElytraColor(ItemStack output, ItemStack elytra) {

        if (elytra.hasTagCompound() && elytra.getTagCompound().hasKey(DyableElytra.TAG_ELYTRA_DYE)) {

            if (!output.hasTagCompound()) {
                output.setTagCompound(new NBTTagCompound());
            }
            output.getTagCompound().setInteger(DyableElytra.TAG_ELYTRA_DYE, elytra.getTagCompound().getInteger(DyableElytra.TAG_ELYTRA_DYE));
        }
    }
}
