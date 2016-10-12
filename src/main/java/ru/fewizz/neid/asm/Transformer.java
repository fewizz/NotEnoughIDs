package ru.fewizz.neid.asm;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer {
	ClassNode cn;
	ClassReader reader;
	ClassWriter writer;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (transformedName.equals("net.minecraftforge.fml.common.registry.GameData")) {
			start(bytes, transformedName);
			
			MethodNode mn = AsmUtil.findMethod(cn, "<init>");
			AsmUtil.transformInlinedSizeMethod(cn, mn, 4095, 65535);

			end();
			return writer.toByteArray();
		}
		
		if (transformedName.equals("net.minecraft.stats.StatList")) {
			start(bytes, transformedName);
			
			MethodNode mn = AsmUtil.findMethod(cn, "<clinit>");
			AsmUtil.transformInlinedSizeMethod(cn, mn, 4096, 65536);
			
			end();
			return writer.toByteArray();
		}
		
		if(transformedName.equals("net.minecraft.client.renderer.RenderGlobal")) {
			start(bytes, transformedName);
			
			MethodNode mn = AsmUtil.findMethod(cn, "playEvent", "a", "(Lzs;ILcm;I)V");
			AsmUtil.transformInlinedSizeMethod(cn, mn, 12, 16);
			
			end();
			return writer.toByteArray();
		}
		
		if(transformedName.equals("net.minecraft.block.Block")) {
			start(bytes, transformedName);
			
			MethodNode mn = AsmUtil.findMethod(cn, "getStateId", "j", "(Lars;)I");
			AsmUtil.transformInlinedSizeMethod(cn, mn, 12, 16);
			
			mn = AsmUtil.findMethod(cn, "getStateById", "c", "(I)Lars;");
			AsmUtil.transformInlinedSizeMethod(cn, mn, 4095, 0xFFFF);
			AsmUtil.transformInlinedSizeMethod(cn, mn, 12, 16);
			
			end();
			return writer.toByteArray();
		}
		
		if(transformedName.equals("net.minecraft.network.play.server.SPacketBlockAction")) {
			start(bytes, transformedName);
			
			MethodNode mn = AsmUtil.findMethod(cn, "readPacketData", "a", "(Leq;)V");
			AsmUtil.transformInlinedSizeMethod(cn, mn, 4095, 0xFFFF);
			
			mn = AsmUtil.findMethod(cn, "writePacketData", "b", "(Leq;)V");
			AsmUtil.transformInlinedSizeMethod(cn, mn, 4095, 0xFFFF);
			
			end();
			return writer.toByteArray();
		}

		return bytes;
	}
	
	void start(byte[] bytes, String name) {
		System.out.println("PATCHING: " + name);
		cn = new ClassNode(Opcodes.ASM5);
		reader = new ClassReader(bytes);
		reader.accept(cn, 0);
	}
	
	void end() {
		writer = new ClassWriter(0);
		cn.accept(writer);
	}

}
