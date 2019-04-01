package top.theillusivec4.colytra.common.crafting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeHidden;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.colytra.Colytra;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class RecipesElytraAttachment extends IRecipeHidden {

    private static final ResourceLocation ID = new ResourceLocation(Colytra.MODID, "elytra_attachment");

    public static final RecipeSerializers.SimpleSerializer<RecipesElytraAttachment> CRAFTING_ATTACH_ELYTRA =
            new RecipeSerializers.SimpleSerializer<>(ID.toString(), RecipesElytraAttachment::new);

    public RecipesElytraAttachment(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(@Nonnull IInventory inv, @Nonnull World worldIn) {

        if (!(inv instanceof InventoryCrafting)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.EMPTY;
            ItemStack elytra = ItemStack.EMPTY;

            for(int i = 0; i < inv.getSizeInventory(); ++i) {
                ItemStack itemstack1 = inv.getStackInSlot(i);

                if (!itemstack1.isEmpty()) {

                    if (CapabilityElytra.getCapability(itemstack1).isPresent()) {

                        if (!itemstack.isEmpty()) {
                            return false;
                        }
                        itemstack = itemstack1;
                    } else {

                        if (!elytra.isEmpty() || !(itemstack1.getItem() instanceof ItemElytra)) {
                            return false;
                        }
                        elytra = itemstack1;
                    }
                }
            }

            return !itemstack.isEmpty() && !elytra.isEmpty();
        }
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull IInventory inv) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack elytra = ItemStack.EMPTY;

        for(int k = 0; k < inv.getSizeInventory(); ++k) {
            ItemStack itemstack1 = inv.getStackInSlot(k);

            if (!itemstack1.isEmpty()) {
                LazyOptional<CapabilityElytra.IElytra> capability = CapabilityElytra.getCapability(itemstack1);

                if (capability.isPresent()) {

                    if (!itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    itemstack = itemstack1.copy();
                    itemstack.setCount(1);
                } else {

                    if (!(itemstack1.getItem() instanceof ItemElytra)) {
                        return ItemStack.EMPTY;
                    }
                    elytra = itemstack1.copy();
                }
            }
        }

        if (!itemstack.isEmpty() && !elytra.isEmpty()) {
            final ItemStack elytraStack = elytra;

            if (ColytraConfig.SERVER.colytraMode.get() != ColytraConfig.ColytraMode.NORMAL) {
                mergeEnchantments(elytraStack, itemstack);
                itemstack.setRepairCost(elytraStack.getRepairCost() + itemstack.getRepairCost());
            }
            CapabilityElytra.getCapability(itemstack).ifPresent(ielytra -> ielytra.setElytra(elytraStack));
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

    public static void mergeEnchantments(ItemStack source, ItemStack destination) {
        Map<Enchantment, Integer> mapSource = EnchantmentHelper.getEnchantments(source);
        Map<Enchantment, Integer> mapDestination = EnchantmentHelper.getEnchantments(destination);

        for (Enchantment srcEnch : mapSource.keySet()) {

            if (srcEnch != null) {
                int destLevel = mapDestination.getOrDefault(srcEnch, 0);
                int srcLevel = mapSource.get(srcEnch);
                srcLevel = destLevel == srcLevel ? srcLevel + 1 : Math.max(srcLevel, destLevel);

                if (!srcEnch.canApply(destination)) {
                    return;
                }

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
        }
        EnchantmentHelper.setEnchantments(mapDestination, destination);
        EnchantmentHelper.setEnchantments(new HashMap<>(), source);
    }
}
