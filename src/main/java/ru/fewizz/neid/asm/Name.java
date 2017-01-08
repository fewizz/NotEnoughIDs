package ru.fewizz.neid.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public enum Name {
	// vanilla
	acl("net/minecraft/world/chunk/storage/AnvilChunkLoader", "atj"),
	chunk("net/minecraft/world/chunk/Chunk", "asv"),
	world("net/minecraft/world/World", "aid"),
	nbtTagCompound("net/minecraft/nbt/NBTTagCompound", "dr"),
	ebs("net/minecraft/world/chunk/storage/ExtendedBlockStorage", "asw"),
	bsc("net/minecraft/world/chunk/BlockStateContainer", "aso"),
	nibbleArray("net/minecraft/world/chunk/NibbleArray", "asr"),
	block("net/minecraft/block/Block", "akf"),
	iBlockState("net/minecraft/block/state/IBlockState", "ars"),
	renderGlobal("net/minecraft/client/renderer/RenderGlobal", "boh"),
	entityPlayer("net/minecraft/entity/player/EntityPlayer", "zs"),
	blockPos("net/minecraft/util/math/BlockPos", "cm"),
	packet("net/minecraft/network/Packet", "fj"),
	packetBlockAction("net/minecraft/network/play/server/SPacketBlockAction", "fx"),
	packetBuffer("net/minecraft/network/PacketBuffer", "eq"),
	statList("net/minecraft/stats/StatList", "nw"),
	
	// self
	iBlockStateContainer("ru/fewizz/neid/interfaces/IBlockStateContainer"),
	
	fmlGameData("net/minecraftforge/fml/common/registry/GameData"),
	
	// methods
	acl_writeChunkToNBT(acl, "writeChunkToNBT", "a", "func_75820_a", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)V"),
	acl_readChunkFromNBT(acl, "readChunkFromNBT", "a", "func_75823_a", "(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;"),
	ebs_getData(ebs, "getData", "g", "func_186049_g", "()Lnet/minecraft/world/chunk/BlockStateContainer;"),
	bsc_getDataForNBT(bsc, "getDataForNBT", "a", "func_186017_a", "([BLnet/minecraft/world/chunk/NibbleArray;)Lnet/minecraft/world/chunk/NibbleArray;"),
	bsc_setDataFromNBT(bsc, "setDataFromNBT", "a", "func_186019_a", "([BLnet/minecraft/world/chunk/NibbleArray;Lnet/minecraft/world/chunk/NibbleArray;)V"),
	block_getStateId(block, "getStateId", "j", "func_176210_f", "(Lnet/minecraft/block/state/IBlockState;)I"),
	block_getStateById(block, "getStateById", "c", "func_176220_d", "(I)Lnet/minecraft/block/state/IBlockState;"),
	renderGlobal_playEvent(renderGlobal, "playEvent", "a", "func_180439_a", "(Lnet/minecraft/entity/player/EntityPlayer;ILnet/minecraft/util/math/BlockPos;I)V"),
	packet_readPacketData(packet, "readPacketData", "a", "func_148837_a", "(Lnet/minecraft/network/PacketBuffer;)V"),
	packet_writePacketData(packet, "writePacketData", "b", "func_148840_b", "(Lnet/minecraft/network/PacketBuffer;)V"),
	
	iBlockStateContainer_getDataForNBT2(iBlockStateContainer, "getDataForNBT2", null, null, "(Lnet/minecraft/nbt/NBTTagCompound;[BLnet/minecraft/world/chunk/NibbleArray;)Lnet/minecraft/world/chunk/NibbleArray;"),
	iBlockStateContainer_setDataFromNBT2(iBlockStateContainer, "setDataFromNBT2", null, null, "(Lnet/minecraft/nbt/NBTTagCompound;[BLnet/minecraft/world/chunk/NibbleArray;Lnet/minecraft/world/chunk/NibbleArray;)V");

	// for non-mc classes
	private Name(String deobf) {
		this(deobf, deobf);
	}

	// for mc classes
	private Name(String deobf, String obf) {
		this.clazz = null;
		this.deobf = deobf;
		this.obf = obf;
		this.srg = deobf;
		this.desc = null;
	}

	// for fields and methods
	private Name(Name clazz, String deobf, String obf, String srg, String desc) {
		this.clazz = clazz;
		this.deobf = deobf;
		this.obf = obf != null ? obf : deobf;
		this.srg = srg != null ? srg : deobf;
		this.desc = desc;
	}

	public boolean matches(MethodNode x) {
		assert desc.startsWith("(");
		return obf.equals(x.name) && obfDesc.equals(x.desc) || srg.equals(x.name) && desc.equals(x.desc) || deobf.equals(x.name) && desc.equals(x.desc);
	}

	public boolean matches(FieldNode x) {
		assert !desc.startsWith("(");
		return obf.equals(x.name) && obfDesc.equals(x.desc) || srg.equals(x.name) && desc.equals(x.desc) || deobf.equals(x.name) && desc.equals(x.desc);
	}

	public boolean matches(MethodInsnNode x) {
		return matches(x, !Transformer.envDeobfuscated);
	}
	
	public boolean matches(MethodInsnNode x, boolean obfuscated) {
		assert desc.startsWith("(");
		if (obfuscated) {
			return clazz.obf.equals(x.owner) && obf.equals(x.name) && obfDesc.equals(x.desc) || clazz.srg.equals(x.owner) && srg.equals(x.name) && desc.equals(x.desc);
		}
		else {
			return clazz.deobf.equals(x.owner) && deobf.equals(x.name) && desc.equals(x.desc);
		}
	}

	public boolean matches(FieldInsnNode x, boolean obfuscated) {
		assert !desc.startsWith("(");
		if (obfuscated) {
			return clazz.obf.equals(x.owner) && obf.equals(x.name) && obfDesc.equals(x.desc) || clazz.srg.equals(x.owner) && srg.equals(x.name) && desc.equals(x.desc);
		}
		else {
			return clazz.deobf.equals(x.owner) && deobf.equals(x.name) && desc.equals(x.desc);
		}
	}

	public MethodInsnNode staticInvocation(boolean obfuscated) {
		// static interface methods aren't supported by this, they'd need itf=true
		assert desc.startsWith("(");
		if (obfuscated) { // srg invocation
			return new MethodInsnNode(Opcodes.INVOKESTATIC, clazz.srg, srg, desc, false);
		}
		else {
			return new MethodInsnNode(Opcodes.INVOKESTATIC, clazz.deobf, deobf, desc, false);
		}
	}

	public MethodInsnNode virtualInvocation(boolean obfuscated) {
		assert desc.startsWith("(");
		if (obfuscated) { // srg invocation
			return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, clazz.srg, srg, desc, false);
		}
		else {
			return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, clazz.deobf, deobf, desc, false);
		}
	}
	
	public MethodInsnNode interfaceInvocation() {
		return interfaceInvocation(!Transformer.envDeobfuscated);
	}
	
	public MethodInsnNode interfaceInvocation(boolean obfuscated) {
		assert desc.startsWith("(");
		if (obfuscated) { // srg invocation
			return new MethodInsnNode(Opcodes.INVOKEINTERFACE, clazz.srg, srg, desc, true);
		}
		else {
			return new MethodInsnNode(Opcodes.INVOKEINTERFACE, clazz.deobf, deobf, desc, true);
		}
	}

	public FieldInsnNode staticGet(boolean obfuscated) {
		assert !desc.startsWith("(");
		if (obfuscated) { // srg access
			return new FieldInsnNode(Opcodes.GETSTATIC, clazz.srg, srg, desc);
		}
		else {
			return new FieldInsnNode(Opcodes.GETSTATIC, clazz.deobf, deobf, desc);
		}
	}

	public FieldInsnNode virtualGet(boolean obfuscated) {
		assert !desc.startsWith("(");
		if (obfuscated) { // srg access
			return new FieldInsnNode(Opcodes.GETFIELD, clazz.srg, srg, desc);
		}
		else {
			return new FieldInsnNode(Opcodes.GETFIELD, clazz.deobf, deobf, desc);
		}
	}

	public FieldInsnNode staticSet(boolean obfuscated) {
		assert !desc.startsWith("(");
		if (obfuscated) { // srg access
			return new FieldInsnNode(Opcodes.PUTSTATIC, clazz.srg, srg, desc);
		}
		else {
			return new FieldInsnNode(Opcodes.PUTSTATIC, clazz.deobf, deobf, desc);
		}
	}

	public FieldInsnNode virtualSet(boolean obfuscated) {
		assert !desc.startsWith("(");
		if (obfuscated) { // srg access
			return new FieldInsnNode(Opcodes.PUTFIELD, clazz.srg, srg, desc);
		}
		else {
			return new FieldInsnNode(Opcodes.PUTFIELD, clazz.deobf, deobf, desc);
		}
	}

	private static void translateDescs() {
		StringBuilder sb = new StringBuilder();
		for (Name name : Name.values()) {
			if (name.desc == null)
				continue;
			int pos = 0;
			int endPos = -1;
			while ((pos = name.desc.indexOf('L', pos)) != -1) {
				sb.append(name.desc, endPos + 1, pos);
				endPos = name.desc.indexOf(';', pos + 1);
				String cName = name.desc.substring(pos + 1, endPos);
				for (Name name2 : Name.values()) {
					if (name2.deobf.equals(cName)) {
						cName = name2.obf;
						break;
					}
				}
				sb.append('L');
				sb.append(cName);
				sb.append(';');
				pos = endPos + 1;
			}
			sb.append(name.desc, endPos + 1, name.desc.length());
			name.obfDesc = sb.toString();
			sb.setLength(0);
			//System.out.printf("deobf: %s, obf: %s, desc: %s, obfDesc: %s%n", name.deobf, name.obf, name.desc, name.obfDesc);
		}
	}

	public final Name clazz;
	public final String deobf;
	public String deobfDotted;
	public final String obf;
	public final String srg;
	public final String desc;
	public String obfDesc;

	static {
		translateDescs();
		
		for (Name name : Name.values()) {
			name.deobfDotted = name.deobf.replace('/', '.');
		}
	}
}
