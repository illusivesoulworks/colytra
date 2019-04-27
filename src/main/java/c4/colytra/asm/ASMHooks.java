/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.asm;

import c4.colytra.Colytra;
import c4.colytra.common.capabilities.CapabilityColytraFlying;
import c4.colytra.common.config.ConfigHandler;
import c4.colytra.util.ColytraUtil;
import c4.colytra.network.CPacketFallFlying;
import c4.colytra.network.NetworkHandler;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.Random;

public class ASMHooks {

    private static final Field TICKS_FLYING = ReflectionHelper.findField(EntityLivingBase.class, "ticksElytraFlying", "field_184629_bo");
    private static Random rand = new Random();

    public static void updateColytra(EntityLivingBase entityLivingBase) {

        if (entityLivingBase.isElytraFlying() || !(entityLivingBase instanceof EntityPlayer)) {
            return;
        }

        CapabilityColytraFlying.IColytraFlying flying = CapabilityColytraFlying.getColytraCap(entityLivingBase);
        if (flying == null) return;
        boolean flag = flying.getColytraFlying();

        if (flag && !entityLivingBase.onGround && !entityLivingBase.isRiding()) {
            ItemStack colytra = ColytraUtil.wornElytra(entityLivingBase);

            if (colytra != ItemStack.EMPTY && colytra.getItem() != Items.ELYTRA && ColytraUtil.isUsable(colytra)) {
                flag = true;

                if (!entityLivingBase.world.isRemote && (getTicksElytraFlying(entityLivingBase) + 1) % 20 == 0) {

                    if (!((EntityPlayer) entityLivingBase).capabilities.isCreativeMode) {

                        if (!(colytra.getItem() instanceof ItemElytra)) {

                            if (ConfigHandler.durabilityMode == ConfigHandler.DurabilityMode.NORMAL) {
                                colytraDamageElytra(colytra);
                            } else if (ConfigHandler.durabilityMode == ConfigHandler.DurabilityMode.CHESTPLATE) {
                                colytraDamageChest(colytra, entityLivingBase);
                            }
                        } else {
                            colytra.damageItem(1, entityLivingBase);
                        }
                    }
                }
            }
            else {
                flag = false;
            }
        }
        else {
            flag = false;
        }

        if (!entityLivingBase.world.isRemote) {
            EntityPlayerMP playerMP = (EntityPlayerMP) entityLivingBase;

            if (flag) {
                flying.setColytraFlying();
                playerMP.setElytraFlying();
            } else {
                flying.clearColytraFlying();
            }
        }
    }

    public static void updateClientColytra(EntityLivingBase entityLivingBase) {
        ItemStack colytra = ColytraUtil.wornElytra(entityLivingBase);

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

    private static void colytraDamageElytra(ItemStack stack) {
        int amount = 1;
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

        if (!EnchantmentDurability.negateDamage(stack, i, rand)) {
            NBTTagCompound compound = stack.getSubCompound("Elytra Upgrade");

            if (compound == null) {
                return;
            }
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
