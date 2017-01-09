package ru.fewizz.neid.asm.group;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;

import ru.fewizz.neid.asm.AsmTransformException;
import ru.fewizz.neid.asm.AsmUtil;
import ru.fewizz.neid.asm.Name;
import ru.fewizz.neid.asm.TransformerGroup;

public class TransformerGroupAnvilChunkLoader extends TransformerGroup {

	@Override
	public Name[] getRequiredClasses() {
		return new Name[] { Name.acl };
	}

	@Override
	public void transform(ClassNode cn, Name clazz, byte[] bytes) {
		MethodNode mn = AsmUtil.findMethod(cn, Name.acl_writeChunkToNBT);
		boolean found = false;

		for (ListIterator<AbstractInsnNode> it = mn.instructions.iterator(); it.hasNext();) {
			AbstractInsnNode insn = it.next();

			if (insn instanceof MethodInsnNode && Name.ebs_getData.matches((MethodInsnNode) insn)) {
				found = true;
				it.set(new VarInsnNode(ALOAD, 11));
				insn = it.next();
				insn = it.next();
				insn = it.next();
				it.set(Name.hooks_blockStateContainer_getDataForNBT.staticInvocation());
				break;
			}
		}

		if (!found) {
			throw new AsmTransformException("Something wrong");
		}
		found = false;

		mn = AsmUtil.findMethod(cn, Name.acl_readChunkFromNBT);

		for (ListIterator<AbstractInsnNode> it = mn.instructions.iterator(); it.hasNext();) {
			AbstractInsnNode insn = it.next();

			if (insn instanceof MethodInsnNode && Name.ebs_getData.matches((MethodInsnNode) insn)) {
				found = true;
				//it.add(new VarInsnNode(ALOAD, 13));
				//it.set(new VarInsnNode(ALOAD, 11));
				it.set(new VarInsnNode(ALOAD, 11));
				it.next();
				it.next();
				it.next();
				it.next();
				it.set(Name.hooks_blockStateContainer_setDataFromNBT.staticInvocation());
				break;
			}
		}

		if (!found) {
			throw new AsmTransformException("Something wrong");
		}
	}

}
