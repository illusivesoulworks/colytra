package top.theillusivec4.colytra.common.capability;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.colytra.Colytra;
import top.theillusivec4.colytra.common.ColytraConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityElytra {

    @CapabilityInject(IElytra.class)
    public static final Capability<IElytra> ELYTRA_CAPABILITY = null;

    public static final ResourceLocation ID = new ResourceLocation(Colytra.MODID, "elytra_attachment");

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CapabilityEvents());
        CapabilityManager.INSTANCE.register(IElytra.class, new Capability.IStorage<IElytra>() {

            @Override
            public INBTBase writeNBT(Capability<IElytra> capability, IElytra instance, EnumFacing side) {
                return instance.getElytra().write(new NBTTagCompound());
            }

            @Override
            public void readNBT(Capability<IElytra> capability, IElytra instance, EnumFacing side, INBTBase nbt) {
                instance.setElytra(ItemStack.read((NBTTagCompound)nbt));
            }
        }, ElytraWrapper::new);
    }

    public static LazyOptional<IElytra> getCapability(ItemStack stack) {
        return stack.getCapability(ELYTRA_CAPABILITY);
    }

    public static ICapabilityProvider createProvider(final ItemStack stack) {
        return new Provider(stack);
    }

    public interface IElytra {

        ItemStack getElytra();

        void setElytra(ItemStack stack);

        void damageElytra(EntityLivingBase entityLivingBase, int amount);

        boolean isUseable();
    }

    public static class ElytraWrapper implements IElytra {

        ItemStack elytra = ItemStack.EMPTY;
        ItemStack stack = ItemStack.EMPTY;

        ElytraWrapper() {}

        ElytraWrapper(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack getElytra() {
            return elytra;
        }

        @Override
        public void setElytra(ItemStack stack) {
            this.elytra = stack.copy();
        }

        @Override
        public void damageElytra(EntityLivingBase entityLivingBase, int amount) {
            ColytraConfig.ColytraMode colytraMode = ColytraConfig.SERVER.colytraMode.get();

            if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
                this.elytra.damageItem(amount, entityLivingBase);
            } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {
                LazyOptional<IEnergyStorage> energyStorage = elytra.getCapability(CapabilityEnergy.ENERGY);

                if (energyStorage.isPresent()) {
                    energyStorage.ifPresent(energy -> energy.extractEnergy(ColytraConfig.SERVER.energyUsage.get(), false));
                } else {
                    this.stack.damageItem(amount, entityLivingBase);
                }
            }
        }

        @Override
        public boolean isUseable() {
            ColytraConfig.ColytraMode colytraMode = ColytraConfig.SERVER.colytraMode.get();

            if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
                return elytra.getItem() instanceof ItemElytra && ItemElytra.isUsable(elytra);
            } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {
                LazyOptional<IEnergyStorage> energyStorage = elytra.getCapability(CapabilityEnergy.ENERGY);

                if (energyStorage.isPresent()) {
                    return energyStorage.map(energy -> energy.canExtract() && energy.getEnergyStored() >
                            ColytraConfig.SERVER.energyUsage.get()).orElse(false);
                } else {
                    return !this.stack.isDamageable() || (this.stack.getDamage() < this.stack.getMaxDamage() - 1);
                }
            }
            return true;
        }
    }

    public static class Provider implements ICapabilitySerializable<INBTBase> {

        final LazyOptional<IElytra> optional;
        final IElytra elytra;

        Provider(ItemStack stack) {
            this.elytra = new ElytraWrapper(stack);
            this.optional = LazyOptional.of(() -> elytra);
        }

        @SuppressWarnings("ConstantConditions")
        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return ELYTRA_CAPABILITY.orEmpty(capability, optional);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public INBTBase serializeNBT() {
            return ELYTRA_CAPABILITY.writeNBT(elytra, null);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void deserializeNBT(INBTBase nbt) {
            ELYTRA_CAPABILITY.readNBT(elytra, null, nbt);
        }
    }

    public static class CapabilityEvents {

        @SubscribeEvent
        public void attachCapabilities(final AttachCapabilitiesEvent<ItemStack> evt) {
            ItemStack stack = evt.getObject();

            if (!(stack.getItem() instanceof ItemElytra) && EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST) {
                evt.addCapability(CapabilityElytra.ID, createProvider(stack));
            }
        }
    }
}
