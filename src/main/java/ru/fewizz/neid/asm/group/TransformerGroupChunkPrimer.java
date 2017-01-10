package ru.fewizz.neid.asm.group;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import ru.fewizz.neid.asm.AsmTransformException;
import ru.fewizz.neid.asm.AsmUtil;
import ru.fewizz.neid.asm.Name;
import ru.fewizz.neid.asm.TransformerGroup;

public class TransformerGroupChunkPrimer extends TransformerGroup {

	@Override
	public Name[] getRequiredClasses() {
		return new Name[] { Name.chunkPrimer, Name.hooks };
	}

	@Override
	public void transform(ClassNode cn, Name clazz) {
		MethodNode mn;

		switch (clazz) {
			case chunkPrimer:
				cn.fields.add(new FieldNode(ACC_FINAL + ACC_PUBLIC, "add", "[B", null, null));

				mn = AsmUtil.findMethod(cn, "<init>");
				boolean found = false;

				for (ListIterator<AbstractInsnNode> it = mn.instructions.iterator(); it.hasNext();) {
					AbstractInsnNode insn = it.next();

					if (insn.getOpcode() == PUTFIELD && insn.getNext().getOpcode() == RETURN) { // data field initialisation
						found = true;
						it.add(new VarInsnNode(ALOAD, 0));
						it.add(new LdcInsnNode(new Integer(16 * 16 * 256)));
						it.add(new IntInsnNode(NEWARRAY, T_BYTE));
						it.add(new FieldInsnNode(PUTFIELD, "net/minecraft/world/chunk/ChunkPrimer", "add", "[B"));
						break;
					}
				}
				if (!found) {
					throw new AsmTransformException("Something wrong");
				}

				mn = AsmUtil.findMethod(cn, Name.chunkPrimer_getBlockState);
				mn.instructions.clear();
				mn.instructions.add(new VarInsnNode(ALOAD, 0)); // this
				mn.instructions.add(new VarInsnNode(ILOAD, 1)); // x
				mn.instructions.add(new VarInsnNode(ILOAD, 2)); // y
				mn.instructions.add(new VarInsnNode(ILOAD, 3)); // z
				mn.instructions.add(Name.hooks_chunkPrimer_getBlockState.staticInvocation());
				mn.instructions.add(new InsnNode(ARETURN));

				mn = AsmUtil.findMethod(cn, Name.chunkPrimer_setBlockState);
				mn.instructions.clear();
				mn.instructions.add(new VarInsnNode(ALOAD, 0)); // this
				mn.instructions.add(new VarInsnNode(ILOAD, 1)); // x
				mn.instructions.add(new VarInsnNode(ILOAD, 2)); // y
				mn.instructions.add(new VarInsnNode(ILOAD, 3)); // z
				mn.instructions.add(new VarInsnNode(ALOAD, 4)); // IBlockState
				mn.instructions.add(Name.hooks_chunkPrimer_setBlockState.staticInvocation());
				mn.instructions.add(new InsnNode(RETURN));
				
				mn = AsmUtil.findMethod(cn, Name.chunkPrimer_findGroundBlockIdx);
				mn.instructions.clear();
				mn.instructions.add(new VarInsnNode(ALOAD, 0)); // this
				mn.instructions.add(new VarInsnNode(ILOAD, 1)); // x
				mn.instructions.add(new VarInsnNode(ILOAD, 2)); // z
				mn.instructions.add(Name.hooks_chunkPrimer_findGroundBlockIdx.staticInvocation());
				mn.instructions.add(new InsnNode(IRETURN));
				break;

			case hooks:
				mn = AsmUtil.findMethod(cn, "chunkPrimer_getAdditionalData");
				mn.instructions.clear();
				mn.instructions.add(new VarInsnNode(ALOAD, 0));
				mn.instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/world/chunk/ChunkPrimer", "add", "[B"));
				mn.instructions.add(new InsnNode(ARETURN));
				break;

			default:
				break;
		}
	}

}
