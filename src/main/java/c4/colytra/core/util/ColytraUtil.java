/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.core.util;

import baubles.api.BaublesApi;
import c4.colytra.common.items.ItemElytraBauble;
import c4.colytra.proxy.CommonProxy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

public class ColytraUtil {

    public static ItemStack findAnyColytra(EntityLivingBase entityLivingBase) {

        ItemStack chestplate = findColytraChest(entityLivingBase);

        if (chestplate != ItemStack.EMPTY) {
            return chestplate;
        } else if (CommonProxy.baublesLoaded) {
            ItemStack bauble = findColytraBauble(entityLivingBase);
            if (bauble != ItemStack.EMPTY) {
                return bauble;
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack findColytraChest(EntityLivingBase entityLivingBase) {

        ItemStack chestplate = entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (chestplate.getItem() instanceof ItemElytra || (chestplate.hasTagCompound() && chestplate.getTagCompound().hasKey("Elytra Upgrade"))) {
            return chestplate;
        }

        return ItemStack.EMPTY;
    }

    public static boolean isUsable(ItemStack itemstack) {

        if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Elytra Upgrade")) {

            NBTTagCompound compound = itemstack.getSubCompound("Elytra Upgrade");
            int durability = compound.getInteger("Durability");
            boolean isActive = compound.getInteger("Active") == 1;

            switch(ConfigHandler.durabilityMode) {
                case "Infinite": return isActive;
                case "Chestplate": {
                    IEnergyStorage energyStorage = itemstack.getCapability(CapabilityEnergy.ENERGY, null);
                    if (energyStorage != null) {
                        return energyStorage.getEnergyStored() > 0 && isActive;
                    } else {
                        return itemstack.isItemStackDamageable() && itemstack.getItemDamage() < itemstack.getMaxDamage() - 1 && isActive;
                    }
                }
                default: return durability > 1 && isActive;
            }

        } else {

            return itemstack.getItemDamage() < itemstack.getMaxDamage() - 1;
        }
    }

    @Optional.Method(modid = "baubles")
    public static ItemStack findColytraBauble(EntityLivingBase entity) {

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(5);
            if (stack.getItem() instanceof ItemElytraBauble) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
