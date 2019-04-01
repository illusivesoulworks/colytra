package top.theillusivec4.colytra.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.colytra.common.capability.CapabilityElytra;

import java.util.function.Supplier;

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

            if (entity instanceof EntityLivingBase) {
                ItemStack stack = ((EntityLivingBase) entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                CapabilityElytra.getCapability(stack).ifPresent(ielytra -> ielytra.setElytra(msg.stack));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
