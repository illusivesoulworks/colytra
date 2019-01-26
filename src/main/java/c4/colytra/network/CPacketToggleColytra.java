/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.network;

import c4.colytra.util.ColytraUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketToggleColytra implements IMessage {

    private int status = 0;

    public CPacketToggleColytra() {
    }

    public CPacketToggleColytra(int status) {
        this.status = status;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(status);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        status = buf.readInt();
    }

    public static class ToggleHandler implements IMessageHandler<CPacketToggleColytra, IMessage> {

        @Override
        public IMessage onMessage(CPacketToggleColytra message, MessageContext ctx) {
            IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
            mainThread.addScheduledTask(() -> {

                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

                ItemStack stack = serverPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

                if (ColytraUtil.hasElytraUpgrade(stack)) {

                    NBTTagCompound compound = stack.getSubCompound("Elytra Upgrade");
                    int isActive = compound.getInteger("Active");

                    if (isActive == 1) {
                        serverPlayer.sendStatusMessage(new TextComponentTranslation("toggle.colytra.off"), true);
                        compound.setInteger("Active", 0);
                    } else if (isActive == 0) {
                        serverPlayer.sendStatusMessage(new TextComponentTranslation("toggle.colytra.on"), true);
                        compound.setInteger("Active", 1);
                    }
                }
            });

            return null;
        }
    }
}
