/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/*
 * Boilerplate code taken from Quark by <Vazkii>
 * Get the Source Code in github: https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 */

public class ElytraTransformer implements IClassTransformer {

    public static final ClassnameMap CLASS_MAPPINGS = new ClassnameMap(
            "net/minecraft/entity/EntityLivingBase", "vn",
            "net/minecraft/client/entity/EntityPlayerSP", "bub",
            "net/minecraft/inventory/EntityEquipmentSlot", "vl",
            "net/minecraft/item/ItemStack", "aip");

    private static final Map<String, Transformer> transformers = new HashMap<>();

    static {
        transformers.put("net.minecraft.entity.EntityLivingBase", ElytraTransformer::transformElytraUpdate);
        transformers.put("net.minecraft.client.entity.EntityPlayerSP", ElytraTransformer::transformClientElytraUpdate);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (transformers.containsKey(transformedName)) {
            return transformers.get(transformedName).apply(basicClass);
        }

        return basicClass;
    }

    private static byte[] transformElytraUpdate(byte[] basicClass) {
        log("Preparing to transform EntityLivingBase");
        MethodSignature sig = new MethodSignature("onLivingUpdate", "func_70636_d", "n", "()V");

        byte[] transClass = transform(basicClass, Pair.of(sig, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.INVOKESPECIAL,
                (MethodNode method, AbstractInsnNode node) -> {

                    InsnList toInject = new InsnList();
                    toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "c4/colytra/asm/ASMHooks", "updateColytra", "(Lnet/minecraft/entity/EntityLivingBase;)V"));

                    method.instructions.insert(node, toInject);

                    return true;
                })));

        return transClass;
    }

    private static byte[] transformClientElytraUpdate(byte[] basicClass) {
        log("Preparing to transform EntityPlayerSP");
        MethodSignature sig = new MethodSignature("onLivingUpdate", "func_70636_d", "n", "()V");

        byte[] transClass = transform(basicClass, Pair.of(sig, combine(
                (AbstractInsnNode node) -> node.getOpcode() == Opcodes.INVOKEVIRTUAL && checkDesc(((MethodInsnNode) node).desc, "(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"),
                (MethodNode method, AbstractInsnNode node) -> {

                    InsnList toInject = new InsnList();
                    toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "c4/colytra/asm/ASMHooks", "updateClientColytra", "(Lnet/minecraft/entity/EntityLivingBase;)V"));

                    method.instructions.insert(node, toInject);

                    return true;
                })));

        return transClass;
    }


    // BOILERPLATE BELOW ==========================================================================================================================================

    private static byte[] transform(byte[] basicClass, Pair<MethodSignature, MethodAction>... methods) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        boolean didAnything = false;

        for (Pair<MethodSignature, MethodAction> pair : methods) {
            log("Initiating transformation to method (" + pair.getLeft() + ")");
            didAnything |= findMethodAndTransform(node, pair.getLeft(), pair.getRight());
        }

        if (didAnything) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    private static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction action) {

        String funcName = sig.funcName;

        if (ColytraLoadingPlugin.runtimeDeobf) {
            funcName = sig.srgName;
        }

        for (MethodNode method : node.methods) {
            if ((method.name.equals(funcName) || method.name.equals(sig.obfName) || method.name.equals(sig.srgName)) && (method.desc.equals(sig.funcDesc) || method.desc.equals(sig.obfDesc))) {

                log("Found method, initiating patch...");
                boolean finish = action.test(method);
                log("Patch result: " + (finish ? "Success!" : "Failure"));

                return finish;
            }
        }

        log("Failed to find method!");
        return false;
    }

    private static MethodAction combine(NodeFilter filter, NodeAction action) {
        return (MethodNode mnode) -> applyOnNode(mnode, filter, action);
    }

    private static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
        Iterator<AbstractInsnNode> itr = method.instructions.iterator();

        boolean didAnything = false;
        while (itr.hasNext()) {
            AbstractInsnNode node = itr.next();
            if (filter.test(node)) {
                log("Found target node for patching: " + getNodeString(node));
                didAnything = true;
                if (action.test(method, node)) {
                    break;
                }
            }
        }

        return didAnything;
    }

    private static String getNodeString(AbstractInsnNode node) {
        Printer print = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(print);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        print.print(new PrintWriter(sw));
        print.getText().clear();

        return sw.toString().replaceAll("\n","").trim();
    }

    private static boolean checkDesc(String desc, String expected) {
        return desc.equals(expected) || desc.equals(MethodSignature.obfuscate(expected));
    }

    public static class ClassnameMap extends HashMap<String, String> {

        ClassnameMap(String... s) {
            for(int i = 0; i < s.length / 2; i++)
                put(s[i * 2], s[i * 2 + 1]);
        }

        @Override
        public String put(String key, String value) {
            return super.put("L" + key + ";", "L" + value + ";");
        }
    }

    private static class MethodSignature{
        String funcName, srgName, obfName, funcDesc, obfDesc;

        MethodSignature(String funcName, String srgName, String obfName, String funcDesc) {
            this.funcName = funcName;
            this.srgName = srgName;
            this.obfName = obfName;
            this.funcDesc = funcDesc;
            this.obfDesc = obfuscate(funcDesc);
        }

        @Override
        public String toString() {
            return "Names [" + funcName + ", " + srgName + ", " + obfName + "] Descriptor " + funcDesc + " / " + obfDesc;
        }

        private static String obfuscate(String desc) {
            for (String s : CLASS_MAPPINGS.keySet()) {
                if (desc.contains(s)) {
                    desc = desc.replaceAll(s, CLASS_MAPPINGS.get(s));
                }
            }

            return desc;
        }
    }

    private static void log(String str) {
        FMLLog.info("[Colytra ASM] %s", str);
    }

    private interface Transformer extends Function<byte[], byte[]> { }
    private interface MethodAction extends Predicate<MethodNode> { }
    private interface NodeFilter extends Predicate<AbstractInsnNode> { }
    private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> { }
}
