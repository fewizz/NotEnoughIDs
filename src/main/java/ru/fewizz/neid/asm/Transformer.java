package ru.fewizz.neid.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import ru.fewizz.neid.asm.group.block.TransformerGroupAnvilChunkLoader;
import ru.fewizz.neid.asm.group.block.TransformerGroupBlockHardcoredConstants;
import ru.fewizz.neid.asm.group.block.TransformerGroupChunkPrimer;
import ru.fewizz.neid.asm.group.block.TransformerGroupWorldEdit;
import ru.fewizz.neid.asm.group.item.TransformerGroupItemHardcoredConstants;
import ru.fewizz.neid.asm.group.item.TransformerGroupPacketBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transformer implements IClassTransformer {
	public static final Logger LOGGER = LogManager.getLogger("neid");
	public static boolean envDeobfuscated;

	private static List<TransformerGroup> transformerGroups;

	static {
		transformerGroups = new ArrayList<>();

		// Block
		addTransformerGroup(new TransformerGroupBlockHardcoredConstants());
		addTransformerGroup(new TransformerGroupAnvilChunkLoader());
		addTransformerGroup(new TransformerGroupChunkPrimer());
		addTransformerGroup(new TransformerGroupWorldEdit());
		
		// Item
		addTransformerGroup(new TransformerGroupItemHardcoredConstants());
		addTransformerGroup(new TransformerGroupPacketBuffer());

		transformerGroups = Collections.unmodifiableList(transformerGroups);
	}

	private static void addTransformerGroup(TransformerGroup group) {
		transformerGroups.add(group);
	}

	@Override
	public byte[] transform(String name, String deobfName, byte[] bytes) {

		for (TransformerGroup tg : transformerGroups) {
			for (Name clazz : tg.getRequiredClasses()) {
				if (clazz.deobfDotted.equals(deobfName)) {
					ClassNode cn = new ClassNode(Opcodes.ASM5);

					ClassReader cr = new ClassReader(bytes);
					cr.accept(cn, 0);

					LOGGER.info("Patching class: \"" + deobfName + "\" with Transformer Group: \"" + tg.getClass().getSimpleName() + "\"");
					tg.startTransform(cn, clazz);

					ClassWriter cw = new ClassWriter(0);
					cn.accept(cw);

					return cw.toByteArray();
				}
			}
		}

		return bytes;
	}

}
