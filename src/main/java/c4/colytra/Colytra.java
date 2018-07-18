/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra;

import c4.colytra.proxy.CommonProxy;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Colytra.MODID,
        name = Colytra.MODNAME,
        version = Colytra.MODVER,
        dependencies = "required-after:forge@[14.23.4.2705,);after:baubles;after:quark",
        acceptedMinecraftVersions = "[1.12.1, 1.13)",
        certificateFingerprint = "5d5b8aee896a4f5ea3f3114784742662a67ad32f")
public class Colytra {
    public static final String MODID = "colytra";
    public static final String MODNAME = "Colytra";
    public static final String MODVER = "1.1.0";

    @SidedProxy(clientSide = "c4.colytra.proxy.ClientProxy", serverSide = "c4.colytra.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static Colytra instance;
    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        proxy.preInit(evt);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        proxy.init(evt);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        proxy.postInit(evt);
    }

    @Mod.EventHandler
    public void onFingerPrintViolation(FMLFingerprintViolationEvent evt) {
        FMLLog.log.log(Level.ERROR, "Invalid fingerprint detected! The file " + evt.getSource().getName()
                + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
