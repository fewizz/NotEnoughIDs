package ru.fewizz.neid.asm;

import java.util.*;

import org.apache.logging.log4j.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.fewizz.neid.asm.group.block.*;
import ru.fewizz.neid.asm.group.item.*;

public class Transformer implements IClassTransformer {
	public static final Logger LOGGER = LogManager.getLogger("neid");
	public static boolean envDeobfuscated;
	private ClassNode cn;
	private ClassReader cr;
	private ClassWriter cw;
	private List<TransformerGroup> transformerGroups;

	public Transformer() {
		transformerGroups = new ArrayList<>();

		// Block
		addTransformerGroup(new TransformerGroupBlockHardcoredConstants());
		addTransformerGroup(new TransformerGroupAnvilChunkLoader());
		addTransformerGroup(new TransformerGroupChunkPrimer());
		addTransformerGroup(new TransformerGroupWorldEdit());
		
		// Item
		addTransformerGroup(new TransformerGroupItemHardcoredConstants());
		addTransformerGroup(new TransformerGroupPacketBuffer());
	}

	private void addTransformerGroup(TransformerGroup group) {
		transformerGroups.add(group);
	}

	@Override
	public byte[] transform(String name, String deobfName, byte[] bytes) {
		boolean transformed = false;

		for (Iterator<TransformerGroup> it = transformerGroups.iterator(); it.hasNext();) {
			TransformerGroup tg = it.next();

			for (Name clazz : tg.getRequiredClasses()) {
				if (clazz.deobfDotted.equals(deobfName)) {
					if (!transformed) {
						transformed = true;
						start(bytes, deobfName, tg);
					}

					LOGGER.info("Patching class: \"" + deobfName + "\" with Transformer Group: \"" + tg.getClass().getSimpleName() + "\"");
					tg.startTransform(cn, clazz);

					if (tg.isPatchedAllClasses()) {
						it.remove();
						break;
					}
				}
			}
		}

		if (transformed) {
			end();
			return cw.toByteArray();
		}

		return bytes;
	}

	void start(byte[] bytes, String name, TransformerGroup tg) {
		cn = new ClassNode(Opcodes.ASM5);
		cr = new ClassReader(bytes);
		cr.accept(cn, 0);
	}

	void end() {
		cw = new ClassWriter(0);
		cn.accept(cw);
	}

}
