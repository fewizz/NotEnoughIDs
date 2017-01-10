package ru.fewizz.neid.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.fewizz.neid.asm.group.TransformerGroupAnvilChunkLoader;
import ru.fewizz.neid.asm.group.TransformerGroupChunkPrimer;
import ru.fewizz.neid.asm.group.TransformerGroupHardcoredConstants;

public class Transformer implements IClassTransformer {
	public static final Logger LOGGER = LogManager.getLogger("neid");
	public static boolean envDeobfuscated;
	private ClassNode cn;
	private ClassReader cr;
	private ClassWriter cw;
	private List<TransformerGroup> groups;
	private Map<TransformerGroup, Name[]> namesToTransform;

	public Transformer() {
		groups = new ArrayList<TransformerGroup>();
		namesToTransform = new HashMap<TransformerGroup, Name[]>();

		addTransformerGroup(new TransformerGroupHardcoredConstants());
		addTransformerGroup(new TransformerGroupAnvilChunkLoader());
		addTransformerGroup(new TransformerGroupChunkPrimer());
	}

	private void addTransformerGroup(TransformerGroup group) {
		groups.add(group);
		namesToTransform.put(group, group.getRequiredClasses());
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {

		boolean transformed = false;

		for (Entry<TransformerGroup, Name[]> entry : namesToTransform.entrySet()) {

			for (Name clazz : entry.getValue()) {
				if (clazz.deobfDotted.equals(transformedName)) {
					if (!transformed) {
						transformed = true;
						start(bytes, transformedName);
					}

					entry.getKey().transform(cn, clazz);
				}
			}
		}

		if (transformed) {
			end();
			return cw.toByteArray();
		}

		return bytes;
	}

	void start(byte[] bytes, String name) {
		LOGGER.info("PATCHING: " + name);
		cn = new ClassNode(Opcodes.ASM5);
		cr = new ClassReader(bytes);
		cr.accept(cn, 0);
	}

	void end() {
		cw = new ClassWriter(0);
		cn.accept(cw);
	}

}
