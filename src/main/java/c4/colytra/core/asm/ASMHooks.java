/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.core.asm;

import c4.colytra.Colytra;
import c4.colytra.core.util.ColytraUtil;
import c4.colytra.core.util.ConfigHandler;
import c4.colytra.network.CPacketFallFlying;
import c4.colytra.network.NetworkHandler;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.Random;

public class ASMHooks {

    private static final Field TICKS_FLYING = ReflectionHelper.findField(EntityLivingBase.class, "ticksElytraFlying", "field_184629_bo");
    private static Random rand = new Random();

    public static void updateColytra(EntityLivingBase entityLivingBase) {

        boolean flag = entityLivingBase.isElytraFlying();

        if (flag && !entityLivingBase.onGround && !entityLivingBase.isRiding())
        {
            ItemStack colytra = ColytraUtil.findAnyColytra(entityLivingBase);

            if (colytra != ItemStack.EMPTY && ColytraUtil.isUsable(colytra))
            {
                flag = true;

                if (!entityLivingBase.world.isRemote && (getTicksElytraFlying(entityLivingBase) + 1) % 20 == 0)
                {
                    if (!(colytra.getItem() instanceof ItemElytra)) {
                        if (ConfigHandler.durabilityMode.equals("Normal")) {
                            colytraChestDrain(colytra);
                        } else if (ConfigHandler.durabilityMode.equals("Chestplate")) {
                            colytraDamageChest(colytra, entityLivingBase);
                        }
                    } else {
                        colytra.damageItem(1, entityLivingBase);
                    }
                }
            }
            else
            {
                flag = false;
            }
        }
        else
        {
            flag = false;
        }

        if (!entityLivingBase.world.isRemote && entityLivingBase instanceof EntityPlayer)
        {
            EntityPlayerMP playerMP = (EntityPlayerMP) entityLivingBase;

            if (flag) {
                playerMP.setElytraFlying();
            } else {
                playerMP.clearElytraFlying();
            }
        }
    }

    public static void updateClientColytra(EntityLivingBase entityLivingBase) {

        ItemStack colytra = ColytraUtil.findAnyColytra(entityLivingBase);

        if (colytra != ItemStack.EMPTY && colytra.getItem() != Items.ELYTRA && ColytraUtil.isUsable(colytra)) {
            NetworkHandler.INSTANCE.sendToServer(new CPacketFallFlying());
        }

    }

    private static void colytraDamageChest(ItemStack stack, EntityLivingBase entityLivingBase) {
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage != null && energyStorage.getEnergyStored() > 0) {
            if (entityLivingBase instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityLivingBase;
                if (!player.capabilities.isCreativeMode) {
                    energyStorage.extractEnergy(1000, false);
                }
            }
        } else {
            stack.damageItem(1, entityLivingBase);
        }
    }

    private static void colytraChestDrain(ItemStack stack) {

        int amount = 1;
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

        if (!EnchantmentDurability.negateDamage(stack, i, rand)) {
            NBTTagCompound compound = stack.getSubCompound("Elytra Upgrade");
            int durability = compound.getInteger("Durability");
            compound.setInteger("Durability", durability - amount);
        }
    }

    private static int getTicksElytraFlying(EntityLivingBase entity) {
        try {
            return (int) TICKS_FLYING.get(entity);
        } catch (IllegalAccessException e) {
            Colytra.logger.log(Level.INFO, "Cannot get ticksElytraFlying in EntityLivingBase!");
            return 0;
        }
    }
}
