package top.theillusivec4.colytra.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.colytra.Colytra;

import java.util.ArrayList;
import java.util.List;

public class ColytraConfig {

    private static final String CONFIG_PREFIX = "gui." + Colytra.MODID + ".config.";

    public static class Server {

        public final ForgeConfigSpec.EnumValue<PermissionMode> permissionMode;
        public final ForgeConfigSpec.ConfigValue<List<String>> permissionList;
        public final ForgeConfigSpec.EnumValue<ColytraMode> colytraMode;
        public final ForgeConfigSpec.IntValue energyUsage;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            permissionMode = builder
                    .comment("Sets whether the permission list is a blacklist or whitelist")
                    .translation(CONFIG_PREFIX + "permissionMode")
                    .defineEnum("permissionMode", PermissionMode.BLACKLIST);

            permissionList = builder
                    .comment("List of items by registry name to be blacklisted/whitelisted based on Permission Mode")
                    .translation(CONFIG_PREFIX + "permissionList")
                    .define("permissionList", new ArrayList<>());

            colytraMode = builder
                    .comment("Sets how the elytra chestplates will behave\n" +
                            "NORMAL: Elytras will exist separately from the chestplate, able to be separated later\n" +
                            "UNISON: Elytras will fuse completely with the chestplate, unable to be separated\n" +
                            "PERFECT: Elytras will fuse completely with the chestplate and flying will not use durability")
                    .translation(CONFIG_PREFIX + "colytraMode")
                    .defineEnum("colytraMode", ColytraMode.NORMAL);

            energyUsage = builder
                    .comment("How much energy per second elytra flight uses if Unison mode is active and the chestplate uses energy")
                    .translation(CONFIG_PREFIX + "energyUsage")
                    .defineInRange("energyUsage", 1000, 0, Integer.MAX_VALUE);
        }
    }

    public enum PermissionMode {
        BLACKLIST,
        WHITELIST
    }

    public enum ColytraMode {
        NORMAL,
        UNISON,
        PERFECT
    }

    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}
