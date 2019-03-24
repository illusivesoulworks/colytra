/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.util;

import baubles.api.BaublesApi;
import c4.colytra.common.config.ConfigHandler;
import c4.colytra.common.items.ItemElytraBauble;
import c4.colytra.proxy.CommonProxy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import java.util.ArrayList;
import java.util.List;

public class ColytraUtil {

    public static List<Item> itemList;

    public static ItemStack wornElytra(EntityLivingBase entityLivingBase) {
        ItemStack chestplate = wornColytra(entityLivingBase);

        if (chestplate != ItemStack.EMPTY) {
            return chestplate;
        } else if (CommonProxy.baublesLoaded) {
            ItemStack bauble = wornElytraBauble(entityLivingBase);
            if (bauble != ItemStack.EMPTY) {
                return bauble;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack wornColytra(EntityLivingBase entityLivingBase) {
        ItemStack chestplate = entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return hasElytraUpgrade(chestplate) ? chestplate : ItemStack.EMPTY;
    }

    public static boolean isUsable(ItemStack itemstack) {

        if (hasElytraUpgrade(itemstack)) {
            NBTTagCompound compound = itemstack.getSubCompound("Elytra Upgrade");

            if (compound == null) {
                return false;
            }
            int durability = compound.getInteger("Durability");
            boolean isActive = compound.getInteger("Active") == 1;

            switch(ConfigHandler.durabilityMode) {
                case INFINITE: return isActive;
                case CHESTPLATE: {
                    IEnergyStorage energyStorage = itemstack.getCapability(CapabilityEnergy.ENERGY, null);
                    if (energyStorage != null && energyStorage.getEnergyStored() > 0) {
                        return isActive;
                    } else {
                        return itemstack.isItemStackDamageable() && itemstack.getItemDamage() < itemstack.getMaxDamage() - 1 && isActive;
                    }
                }
                default: return durability > 1 && isActive;
            }

        } else if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Active")) {
            return itemstack.getTagCompound().getInteger("Active") == 1 && itemstack.getItemDamage() < itemstack.getMaxDamage() - 1;
        } else {
            return itemstack.getItemDamage() < itemstack.getMaxDamage() - 1;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean hasElytraUpgrade(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("Elytra Upgrade");
    }

    public static void initConfigItemList() {
        itemList = new ArrayList<>();

        if (ConfigHandler.itemList.length > 0) {

            for (String s : ConfigHandler.itemList) {
                Item item = Item.getByNameOrId(s);

                if (item != null) {
                    itemList.add(item);
                }
            }
        }
    }

    @Optional.Method(modid = "baubles")
    public static ItemStack wornElytraBauble(EntityLivingBase entity) {

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
