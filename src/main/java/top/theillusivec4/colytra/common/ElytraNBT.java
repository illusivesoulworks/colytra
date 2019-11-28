package top.theillusivec4.colytra.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import top.theillusivec4.colytra.Colytra;

public class ElytraNBT {

  public static final String ELYTRA_TAG = Colytra.MODID + ":ElytraUpgrade";

  public static boolean hasUpgrade(ItemStack stack) {
    return stack.getChildTag(ELYTRA_TAG) != null;
  }

  public static ItemStack getElytra(ItemStack stack) {
    CompoundNBT tag = stack.getChildTag(ELYTRA_TAG);
    return tag != null ? ItemStack.read(tag) : ItemStack.EMPTY;
  }

  public static void setElytra(ItemStack chestStack, ItemStack elytraStack) {
    chestStack.getOrCreateTag().put(ELYTRA_TAG, elytraStack.write(new CompoundNBT()));
  }

  public static void damageElytra(LivingEntity livingEntity, ItemStack chestStack,
      ItemStack elytraStack, int amount) {
    ColytraConfig.ColytraMode colytraMode = ColytraConfig.getColytraMode();

    if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
      elytraStack.damageItem(amount, livingEntity,
          damager -> damager.sendBreakAnimation(EquipmentSlotType.CHEST));
    } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {
      LazyOptional<IEnergyStorage> energyStorage = chestStack
          .getCapability(CapabilityEnergy.ENERGY);

      energyStorage
          .ifPresent(energy -> energy.extractEnergy(ColytraConfig.getEnergyUsage(), false));

      if (!energyStorage.isPresent()) {
        elytraStack.damageItem(amount, livingEntity,
            damager -> damager.sendBreakAnimation(EquipmentSlotType.CHEST));
      }
    }
    setElytra(chestStack, elytraStack);
  }

  public static boolean isUseable(ItemStack chestStack, ItemStack elytraStack) {

    if (elytraStack.isEmpty()) {
      return false;
    }
    ColytraConfig.ColytraMode colytraMode = ColytraConfig.getColytraMode();

    if (colytraMode == ColytraConfig.ColytraMode.NORMAL) {
      return elytraStack.getItem() instanceof ElytraItem && ElytraItem.isUsable(elytraStack);
    } else if (colytraMode == ColytraConfig.ColytraMode.UNISON) {
      LazyOptional<IEnergyStorage> energyStorage = chestStack
          .getCapability(CapabilityEnergy.ENERGY);

      if (energyStorage.isPresent()) {
        return energyStorage.map(
            energy -> energy.canExtract() && energy.getEnergyStored() > ColytraConfig
                .getEnergyUsage()).orElse(false);
      } else {
        return !chestStack.isDamageable() || (chestStack.getDamage()
            < chestStack.getMaxDamage() - 1);
      }
    }
    return true;
  }
}
