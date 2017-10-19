/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.network;

import c4.colytra.core.util.ColytraUtil;
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
                    ItemStack colytra = ColytraUtil.findAnyColytra(serverPlayer);

                    if (colytra != ItemStack.EMPTY && ColytraUtil.isUsable(colytra) && colytra.getItem() != Items.ELYTRA)
                    {
                        serverPlayer.setElytraFlying();
                    }
                }
                else
                {
                    serverPlayer.clearElytraFlying();
                }
            });

            return null;
        }
    }
}
