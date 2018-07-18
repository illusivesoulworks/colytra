/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Colytra mod for Minecraft.
 * Colytra is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Colytra
 */

package c4.colytra.network;

import c4.colytra.common.capabilities.CapabilityColytraFlying;
import c4.colytra.util.ColytraUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketFallFlying implements IMessage {

    public CPacketFallFlying() {
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    public static class FallFlyingHandler implements IMessageHandler<CPacketFallFlying, IMessage> {

        @Override
        public IMessage onMessage(CPacketFallFlying message, MessageContext ctx) {
            IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
            mainThread.addScheduledTask(() -> {

                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

                if (!serverPlayer.onGround && serverPlayer.motionY < 0.0D && !serverPlayer.isElytraFlying() && !serverPlayer.isInWater())
                {
                    ItemStack colytra = ColytraUtil.wornElytra(serverPlayer);

                    if (colytra != ItemStack.EMPTY && ColytraUtil.isUsable(colytra) && colytra.getItem() != Items.ELYTRA)
                    {
                        CapabilityColytraFlying.IColytraFlying flying = CapabilityColytraFlying.getColytraCap(serverPlayer);
                        if (flying != null) {
                            flying.setColytraFlying();
                        }
                    }
                }
                else
                {
                    CapabilityColytraFlying.IColytraFlying flying = CapabilityColytraFlying.getColytraCap(serverPlayer);
                    if (flying != null) {
                        flying.clearColytraFlying();
                    }
                }
            });

            return null;
        }
    }
}
