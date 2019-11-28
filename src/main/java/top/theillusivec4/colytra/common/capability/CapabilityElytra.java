package top.theillusivec4.colytra.common.capability;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.colytra.Colytra;
import top.theillusivec4.colytra.common.ColytraConfig;
import top.theillusivec4.colytra.common.ElytraNBT;

//todo: remove in 1.15
@Deprecated
public class CapabilityElytra {

  @CapabilityInject(IElytra.class)
  public static final Capability<IElytra> ELYTRA_CAPABILITY;

  public static final ResourceLocation ID = new ResourceLocation(Colytra.MODID,
      "elytra_attachment");

  static {
    ELYTRA_CAPABILITY = null;
  }

  public static void register() {
    MinecraftForge.EVENT_BUS.register(new CapabilityEvents());
    CapabilityManager.INSTANCE.register(IElytra.class, new Capability.IStorage<IElytra>() {

      @Override
      public INBT writeNBT(Capability<IElytra> capability, IElytra instance, Direction side) {
        return new CompoundNBT();
      }

      @Override
      public void readNBT(Capability<IElytra> capability, IElytra instance, Direction side,
          INBT nbt) {
        //Handles forward-compatibility with nbt elytra tag
        ItemStack stack = instance.getStack();
        ItemStack elytraStack = ItemStack.read((CompoundNBT) nbt);

        if (!elytraStack.isEmpty() && !ElytraNBT.hasUpgrade(stack)) {
          ElytraNBT.setElytra(instance.getStack(), ItemStack.read((CompoundNBT) nbt));
        }
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

    ItemStack getStack();

    ItemStack getElytra();
  }

  public static class ElytraWrapper implements IElytra {

    ItemStack elytra = ItemStack.EMPTY;
    ItemStack stack = ItemStack.EMPTY;

    ElytraWrapper() {

    }

    ElytraWrapper(ItemStack stack) {
      this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
      return stack;
    }

    @Override
    public ItemStack getElytra() {
      return elytra;
    }
  }

  public static class Provider implements ICapabilitySerializable<INBT> {

    final LazyOptional<IElytra> optional;
    final IElytra elytra;

    Provider(ItemStack stack) {
      this.elytra = new ElytraWrapper(stack);
      this.optional = LazyOptional.of(() -> elytra);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, Direction side) {
      return ELYTRA_CAPABILITY.orEmpty(capability, optional);
    }

    @Override
    public INBT serializeNBT() {
      return ELYTRA_CAPABILITY.writeNBT(elytra, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
      ELYTRA_CAPABILITY.readNBT(elytra, null, nbt);
    }
  }

  public static class CapabilityEvents {

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

    @SubscribeEvent
    public void attachCapabilities(final AttachCapabilitiesEvent<ItemStack> evt) {

      ItemStack stack = evt.getObject();
      Item item = stack.getItem();
      boolean isChestplate = MobEntity.getSlotForItemStack(stack) == EquipmentSlotType.CHEST;

      if (item instanceof ElytraItem || !isChestplate || !isValid(item)) {
        return;
      }

      evt.addCapability(CapabilityElytra.ID, createProvider(stack));
    }
  }
}
