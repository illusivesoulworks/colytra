package top.theillusivec4.colytra.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.colytra.Colytra;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {

  private static final String        PTC_VERSION = "1";
  public static final  SimpleChannel INSTANCE    =
      NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Colytra.MODID, "main"))
                                    .networkProtocolVersion(() -> PTC_VERSION)
                                    .clientAcceptedVersions(PTC_VERSION::equals)
                                    .serverAcceptedVersions(PTC_VERSION::equals)
                                    .simpleChannel();

  private static int id = 0;

  public static void register() {

    registerMessage(SPacketSyncColytra.class, SPacketSyncColytra::encode,
                    SPacketSyncColytra::decode, SPacketSyncColytra::handle);
  }

  private static <MSG> void registerMessage(Class<MSG> messageType,
                                            BiConsumer<MSG, PacketBuffer> encoder,
                                            Function<PacketBuffer, MSG> decoder,
                                            BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {

    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
