package top.theillusivec4.colytra.common.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeHidden;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.colytra.Colytra;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

import javax.annotation.Nonnull;

public class RecipesElytraDetachment extends IRecipeHidden {

    private static final ResourceLocation ID = new ResourceLocation(Colytra.MODID, "elytra_detachment");

    public static final RecipeSerializers.SimpleSerializer<RecipesElytraDetachment> CRAFTING_DETACH_ELYTRA =
            new RecipeSerializers.SimpleSerializer<>(ID.toString(), RecipesElytraDetachment::new);

    public RecipesElytraDetachment(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(@Nonnull IInventory inv, @Nonnull World worldIn) {

        if (ColytraConfig.SERVER.colytraMode.get() != ColytraConfig.ColytraMode.NORMAL) {
            return false;
        }

        if (!(inv instanceof InventoryCrafting)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.EMPTY;

            for(int i = 0; i < inv.getSizeInventory(); ++i) {
                ItemStack itemstack1 = inv.getStackInSlot(i);

                if (!itemstack1.isEmpty()) {

                    if (CapabilityElytra.getCapability(itemstack1).isPresent()) {

                        if (!itemstack.isEmpty()) {
                            return false;
                        }
                        itemstack = itemstack1;
                    } else {
                        return false;
                    }
                }
            }

            return !itemstack.isEmpty();
        }
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull IInventory inv) {
        ItemStack itemstack = ItemStack.EMPTY;

        for(int k = 0; k < inv.getSizeInventory(); ++k) {
            ItemStack itemstack1 = inv.getStackInSlot(k);

            if (!itemstack1.isEmpty()) {

                if (!itemstack.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                LazyOptional<CapabilityElytra.IElytra> capability = CapabilityElytra.getCapability(itemstack1);
                itemstack = capability.map(ielytra -> ielytra.getElytra().copy()).orElse(ItemStack.EMPTY);

                if (itemstack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
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
    public NonNullList<ItemStack> getRemainingItems(IInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                LazyOptional<CapabilityElytra.IElytra> capability = CapabilityElytra.getCapability(itemstack);
                capability.ifPresent(ielytra -> ielytra.setElytra(ItemStack.EMPTY));
                nonnulllist.set(i, itemstack.copy());
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
