package ru.fewizz.neid.asm.group.block;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import ru.fewizz.neid.asm.AsmUtil;
import ru.fewizz.neid.asm.Name;
import ru.fewizz.neid.asm.TransformerGroup;

public class TransformerGroupWorldEdit extends TransformerGroup {
	@Override
	public Name[] getRequiredClassesInternal() {
		return new Name[] {Name.we_baseBlock};
	}

	@Override
	public void transform(ClassNode cn, Name clazz) {
		MethodNode mn = AsmUtil.findMethod(cn, "internalSetId");
		AsmUtil.transformInlinedSizeMethod(cn, mn, 0xFFF, 0xFFFF);
		
		mn = AsmUtil.findMethod(cn, "hashCode");
		AsmUtil.transformInlinedSizeMethod(cn, mn, 3, 4); // why 3, if meta is 4 bit?
	}

}
