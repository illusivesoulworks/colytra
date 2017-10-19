/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.network;

import c4.colytra.Colytra;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Colytra.MODID);

    private static int id = 0;

    public static void init() {
        NetworkHandler.registerMessage(CPacketFallFlying.FallFlyingHandler.class, CPacketFallFlying.class, Side.SERVER);
        NetworkHandler.registerMessage(CPacketToggleColytra.ToggleHandler.class, CPacketToggleColytra.class, Side.SERVER);
    }

    private static void registerMessage(Class messageHandler, Class requestMessageType, Side side) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, id++, side);
    }
}
