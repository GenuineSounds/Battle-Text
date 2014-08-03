package com.genuineminecraft.battletext.asm;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Transformer implements IClassTransformer {

	public static int changeCount = 0;

	@Override
	public byte[] transform(String name, String newName, byte[] bytecode) {
		if (name.equals("net.minecraft.entity.EntityLivingBase"))
			return patchClassASM(name, bytecode, false);
		else if (name.equals("sv"))
			return patchClassASM(name, bytecode, true);
		else
			return bytecode;
	}

	public byte[] patchClassASM(String name, byte[] bytes, boolean obfuscated) {
		String targetMethodName = "";
		String targetClassName = "";
		if (obfuscated == true) {
			targetMethodName = "f";
			targetClassName = "sv";
		} else {
			targetMethodName = "heal";
			targetClassName = "net/minecraft/entity/EntityLivingBase";
		}
		//set up ASM class manipulation stuff. Consult the ASM docs for details
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode method = methods.next();
			int fdiv_index = -1;
			if ((method.name.equals(targetMethodName) && method.desc.equals("(F)V"))) {
				if (method.instructions.size() > 30)
					continue;
				InsnList inj = new InsnList();
				// 0: aload_0
				inj.add(new VarInsnNode(Opcodes.ALOAD, 0));
				// 1: fload_1
				inj.add(new VarInsnNode(Opcodes.FLOAD, 1));
				// 2: invokestatic	#20  // Method com/genuineminecraft/battletext/Hooks.onLivingHeal:(Lnet/minecraft/entity/EntityLivingBase;F)F
				inj.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/genuineminecraft/battletext/Hooks", "onLivingHeal", "(L" + targetClassName + ";F)F"));
				// 5: fstore_1
				inj.add(new VarInsnNode(Opcodes.FSTORE, 1));
				// 6: fload_1
				inj.add(new VarInsnNode(Opcodes.FLOAD, 1));
				// 7: fconst_0
				inj.add(new InsnNode(Opcodes.FCONST_0));
				// 8: fcmpg
				inj.add(new InsnNode(Opcodes.FCMPG));
				// 9: ifgt				13
				LabelNode node = new LabelNode();
				inj.add(new JumpInsnNode(Opcodes.IFGT, node));
				// 12: return
				inj.add(new InsnNode(Opcodes.RETURN));
				// Add Label for if jump.
				inj.add(node);
				method.instructions.insert(inj);
				System.out.println("LivingHealEvent Patched! Still waiting for this to become a forge standard.");
				break;
			}
		}
		// ASM specific for cleaning up and returning the final bytes for JVM processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
