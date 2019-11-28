package top.theillusivec4.colytra.common.network;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

public class SPacketSyncColytra {

  private int entityId;
  private ItemStack stack;

  public SPacketSyncColytra(int entityId, ItemStack stack) {

    this.entityId = entityId;
    this.stack = stack.copy();
  }

  public static void encode(SPacketSyncColytra msg, PacketBuffer buf) {

    buf.writeInt(msg.entityId);
    buf.writeItemStack(msg.stack);
  }

  public static SPacketSyncColytra decode(PacketBuffer buf) {

    return new SPacketSyncColytra(buf.readInt(), buf.readItemStack());
  }

  public static void handle(SPacketSyncColytra msg, Supplier<NetworkEvent.Context> ctx) {

    ctx.get().enqueueWork(() -> {
      Entity entity = Minecraft.getInstance().world.getEntityByID(msg.entityId);

      if (!(entity instanceof LivingEntity)) {
        return;
      }

      ItemStack stack = ((LivingEntity) entity).getItemStackFromSlot(EquipmentSlotType.CHEST);
//      CapabilityElytra.getCapability(stack)
//          .ifPresent(elytraHolder -> elytraHolder.setElytra(msg.stack));
    });
    ctx.get().setPacketHandled(true);
  }
}
