package top.theillusivec4.colytra.common.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.colytra.Colytra;
import top.theillusivec4.colytra.common.ColytraConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CapabilityElytra {

  @CapabilityInject(IElytra.class)
  public static final Capability<IElytra> ELYTRA_CAPABILITY = null;

  public static final ResourceLocation ID =
      new ResourceLocation(Colytra.MODID, "elytra_attachment");

  public static void register() {

    MinecraftForge.EVENT_BUS.register(new CapabilityEvents());
    CapabilityManager.INSTANCE.register(IElytra.class, new Capability.IStorage<IElytra>() {

      @Override
      public INBT writeNBT(Capability<IElytra> capability, IElytra instance, Direction side) {

        return instance.getElytra().write(new CompoundNBT());
      }

      @Override
      public void readNBT(Capability<IElytra> capability, IElytra instance, Direction side,
                          INBT nbt) {

        instance.setElytra(ItemStack.read((CompoundNBT) nbt));
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

    void damageElytra(LivingEntity livingEntity, int amount);

    boolean isUseable();
  }

  public static class ElytraWrapper implements IElytra {

    ItemStack elytra = ItemStack.EMPTY;
    ItemStack stack  = ItemStack.EMPTY;

    ElytraWrapper() {

    }

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
    public void damageElytra(LivingEntity livingEntity, int amount) {

      ColytraConfig.ColytraMode colytraMode = ColytraConfig.getColytraMode();

      if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
        this.elytra.damageItem(amount, livingEntity, damager -> {
          damager.sendBreakAnimation(EquipmentSlotType.CHEST);
        });
      } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {
        LazyOptional<IEnergyStorage> energyStorage = elytra.getCapability(CapabilityEnergy.ENERGY);

        if (energyStorage.isPresent()) {
          energyStorage.ifPresent(
              energy -> energy.extractEnergy(ColytraConfig.getEnergyUsage(), false));
        } else {
          this.stack.damageItem(amount, livingEntity, damager -> {
            damager.sendBreakAnimation(EquipmentSlotType.CHEST);
          });
        }
      }
    }

    @Override
    public boolean isUseable() {

      if (elytra.isEmpty()) {
        return false;
      }

      ColytraConfig.ColytraMode colytraMode = ColytraConfig.getColytraMode();

      if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
        return elytra.getItem() instanceof ElytraItem && ElytraItem.isUsable(elytra);
      } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {
        LazyOptional<IEnergyStorage> energyStorage = elytra.getCapability(CapabilityEnergy.ENERGY);

        if (energyStorage.isPresent()) {
          return energyStorage.map(energy -> energy.canExtract() && energy.getEnergyStored() >
                                                                    ColytraConfig.getEnergyUsage())
                              .orElse(false);
        } else {
          return !this.stack.isDamageable() ||
                 (this.stack.getDamage() < this.stack.getMaxDamage() - 1);
        }
      }
      return true;
    }
  }

  public static class Provider implements ICapabilitySerializable<INBT> {

    final LazyOptional<IElytra> optional;
    final IElytra               elytra;

    Provider(ItemStack stack) {

      this.elytra = new ElytraWrapper(stack);
      this.optional = LazyOptional.of(() -> elytra);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, Direction side) {

      return ELYTRA_CAPABILITY.orEmpty(capability, optional);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public INBT serializeNBT() {

      return ELYTRA_CAPABILITY.writeNBT(elytra, null);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void deserializeNBT(INBT nbt) {

      ELYTRA_CAPABILITY.readNBT(elytra, null, nbt);
    }
  }

  public static class CapabilityEvents {

    @SubscribeEvent
    public void attachCapabilities(final AttachCapabilitiesEvent<ItemStack> evt) {

      ItemStack stack = evt.getObject();
      Item item = stack.getItem();

      if (item instanceof ElytraItem) {
        return;
      }

      if (MobEntity.getSlotForItemStack(stack) != EquipmentSlotType.CHEST) {
        return;
      }

      if (!isValid(item)) {
        return;
      }

      evt.addCapability(CapabilityElytra.ID, createProvider(stack));
    }

    private static boolean isValid(Item item) {

      ColytraConfig.PermissionMode permissionMode = ColytraConfig.getPermissionMode();
      List<String> permissionList = ColytraConfig.getPermissionList();
      ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(item);

      if (resourceLocation == null) {
        return false;
      }

      String key = resourceLocation.toString();
      boolean isBlacklist = permissionMode == ColytraConfig.PermissionMode.BLACKLIST;

      return isBlacklist != permissionList.contains(key);
    }
  }
}
