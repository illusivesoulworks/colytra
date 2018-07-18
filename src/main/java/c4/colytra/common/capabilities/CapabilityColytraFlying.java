/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.common.capabilities;

import c4.colytra.Colytra;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class CapabilityColytraFlying {

    @CapabilityInject(IColytraFlying.class)
    public static final Capability<IColytraFlying> COLYTRA_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Colytra.MODID, "colytraFlying");

    private static final String FLYING_TAG = "colytra_flying";

    public static void register() {
        CapabilityManager.INSTANCE.register(IColytraFlying.class, new Capability.IStorage<IColytraFlying>() {
            @Override
            public NBTBase writeNBT(Capability<IColytraFlying> capability, IColytraFlying instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setBoolean(FLYING_TAG, instance.getColytraFlying());
                return compound;
            }

            @Override
            public void readNBT(Capability<IColytraFlying> capability, IColytraFlying instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                boolean flag = compound.getBoolean(FLYING_TAG);
                if (flag) {
                    instance.setColytraFlying();
                } else {
                    instance.clearColytraFlying();
                }
            }
        }, ColytraFlying::new);
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static IColytraFlying getColytraCap(final EntityLivingBase entityIn) {

        if (entityIn != null && entityIn.hasCapability(COLYTRA_CAP, DEFAULT_FACING)) {
            return entityIn.getCapability(COLYTRA_CAP, DEFAULT_FACING);
        }

        return null;
    }

    public static ICapabilityProvider createProvider(final IColytraFlying colytra) {
        return new Provider(colytra, COLYTRA_CAP, DEFAULT_FACING);
    }

    public interface IColytraFlying {

        boolean getColytraFlying();

        void setColytraFlying();

        void clearColytraFlying();
    }

    public static class ColytraFlying implements IColytraFlying {

        boolean colytraFlying = false;

        @Override
        public boolean getColytraFlying() {
            return colytraFlying;
        }

        @Override
        public void setColytraFlying() {
            colytraFlying = true;
        }

        @Override
        public void clearColytraFlying() {
            colytraFlying = false;
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<IColytraFlying> capability;
        final EnumFacing facing;
        final IColytraFlying instance;

        Provider(final IColytraFlying instance, final Capability<IColytraFlying> capability, @Nullable final EnumFacing facing) {
            this.instance = instance;
            this.capability = capability;
            this.facing = facing;
        }

        @Override
        public boolean hasCapability(@Nullable final Capability<?> capability, final EnumFacing facing) {
            return capability == getCapability();
        }

        @Override
        public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return capability == getCapability() ? getCapability().cast(this.instance) : null;
        }

        final Capability<IColytraFlying> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final IColytraFlying getInstance() {
            return instance;
        }

        @Override
        public NBTBase serializeNBT() {
            return getCapability().writeNBT(getInstance(), getFacing());
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            getCapability().readNBT(getInstance(), getFacing(), nbt);
        }
    }

    @Mod.EventBusSubscriber
    private static class EventHandler {

        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
            if (evt.getObject() instanceof EntityLivingBase) {
                evt.addCapability(ID, createProvider(new ColytraFlying()));
            }
        }
    }
}
