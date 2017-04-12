package ru.fewizz.neid.asm.group.item;

import org.objectweb.asm.tree.ClassNode;

import ru.fewizz.neid.asm.AsmUtil;
import ru.fewizz.neid.asm.Name;
import ru.fewizz.neid.asm.TransformerGroup;

public class TransformerGroupItemHardcoredConstants extends TransformerGroup {

	@Override
	protected Name[] getRequiredClassesInternal() {
		return new Name[] { Name.statList, Name.fmlGameData };
	}

	@Override
	public void transform(ClassNode cn, Name clazz) {
		if (clazz == Name.statList) {
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, "<clinit>"), 32000, 65535);
		}
		if (clazz == Name.fmlGameData) {
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, "<init>"), 31999, 65534);
		}
	}

}
