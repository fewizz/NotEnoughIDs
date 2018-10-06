package ru.fewizz.neid.asm.group.item;

import java.util.ListIterator;

import org.objectweb.asm.tree.*;

import ru.fewizz.neid.asm.*;

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

			if (insn.getNext().getOpcode() == ISTORE && ((VarInsnNode)insn.getNext()).var == 1) { // data field initialisation
				// Yeah, short's max value now indicates invalid item, bcs of '&0xFFFF'.
				it.add(new LdcInsnNode(new Integer(0xFFFF)));
				it.add(new InsnNode(IAND));
				
				insn = it.next();
				insn = it.next();
				insn = it.next();
				insn = it.next();
				
				it.add(new LdcInsnNode(new Integer(0xFFFF)));
				insn = it.next();
				it.set(new JumpInsnNode(IF_ICMPNE, ((JumpInsnNode)insn).label));
				return;
			}
		}
		
		throw new Error("Something is wrong");
	}

}
