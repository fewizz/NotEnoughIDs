package ru.fewizz.neid.asm.group.item;

import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import ru.fewizz.neid.asm.AsmUtil;
import ru.fewizz.neid.asm.Name;
import ru.fewizz.neid.asm.TransformerGroup;

public class TransformerGroupPacketBuffer extends TransformerGroup {
	@Override
	protected Name[] getRequiredClassesInternal() {
		return new Name[] { Name.packetBuffer };
	}

	@Override
	public void transform(ClassNode cn, Name clazz) {
		MethodNode mn = AsmUtil.findMethod(cn, Name.packetBuffer_readItemStackFromBuffer);

		for (ListIterator<AbstractInsnNode> it = mn.instructions.iterator(); it.hasNext();) {
			AbstractInsnNode insn = it.next();

			if (insn.getNext().getOpcode() == ISTORE && ((VarInsnNode)insn.getNext()).var == 2) { // data field initialisation
				// Yeah, short's max value now indicates ivalid item, bcs of '&0xFFFF'.
				it.add(new LdcInsnNode(new Integer(0xFFFF)));
				it.add(new InsnNode(IAND));
				
				insn = it.next();
				insn = it.next();
				insn = it.next();
				insn = it.next();
				
				it.add(new LdcInsnNode(new Integer(0xFFFF)));
				insn = it.next();
				it.set(new JumpInsnNode(IF_ICMPGE, ((JumpInsnNode)insn).label));
				return;
			}
		}
		
		throw new Error("Something is wrong");
	}

}
