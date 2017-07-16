package ru.fewizz.neid.asm.group.block;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import ru.fewizz.neid.asm.AsmUtil;
import ru.fewizz.neid.asm.Name;
import ru.fewizz.neid.asm.TransformerGroup;

public class TransformerGroupBlockHardcoredConstants extends TransformerGroup {

	@Override
	public Name[] getRequiredClassesInternal() {
		return new Name[] {Name.fmlGameData, Name.statList, Name.renderGlobal, Name.block, Name.packetBlockAction};
	}

	@Override
	public void transform(ClassNode cn, Name clazz) {
		if (clazz == Name.fmlGameData) {
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, "init"), 4095, 65535);
		}
		
		if (clazz == Name.statList) {		
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, "<clinit>"), 4096, 65536);
		}
		
		if(clazz == Name.renderGlobal) {
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, Name.renderGlobal_playEvent), 4095, 65535);
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, Name.renderGlobal_playEvent), 12, 16);
		}
		
		if(clazz == Name.block) {
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, Name.block_getStateId), 12, 16);
			
			MethodNode mn = AsmUtil.findMethod(cn, Name.block_getStateById);
			AsmUtil.transformInlinedSizeMethod(cn, mn, 4095, 0xFFFF);
			AsmUtil.transformInlinedSizeMethod(cn, mn, 12, 16);
		}
		
		if(clazz == Name.packetBlockAction) {
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, Name.packet_readPacketData), 4095, 0xFFFF);
			
			AsmUtil.transformInlinedSizeMethod(cn, AsmUtil.findMethod(cn, Name.packet_writePacketData), 4095, 0xFFFF);
		}
		
	}

}
