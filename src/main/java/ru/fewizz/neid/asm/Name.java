package ru.fewizz.neid.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public enum Name {
	// vanilla
	acl("net/minecraft/world/chunk/storage/AnvilChunkLoader"),
	chunk("net/minecraft/world/chunk/Chunk"),
	chunkPrimer("net/minecraft/world/chunk/ChunkPrimer"),
	world("net/minecraft/world/World"),
	nbtTagCompound("net/minecraft/nbt/NBTTagCompound"),
	ebs("net/minecraft/world/chunk/storage/ExtendedBlockStorage"),
	bsc("net/minecraft/world/chunk/BlockStateContainer"),
	nibbleArray("net/minecraft/world/chunk/NibbleArray"),
	block("net/minecraft/block/Block"),
	iBlockState("net/minecraft/block/state/IBlockState"),
	renderGlobal("net/minecraft/client/renderer/RenderGlobal"),
	entityPlayer("net/minecraft/entity/player/EntityPlayer"),
	blockPos("net/minecraft/util/math/BlockPos"),
	packet("net/minecraft/network/Packet"),
	packetBlockAction("net/minecraft/network/play/server/SPacketBlockAction"),
	packetBuffer("net/minecraft/network/PacketBuffer"),
	statList("net/minecraft/stats/StatList"),
	hooks("ru/fewizz/neid/asm/Hooks"),
	fmlGameData("net/minecraftforge/registries/GameData"),
	
	// not-vanilla
	we_baseBlock("com/sk89q/worldedit/blocks/BaseBlock"),

	// methods
	hooks_chunkPrimer_getBlockState(hooks, "chunkPrimer_getBlockState", null, "(Lnet/minecraft/world/chunk/ChunkPrimer;III)Lnet/minecraft/block/state/IBlockState;"),
	hooks_chunkPrimer_setBlockState(hooks, "chunkPrimer_setBlockState", null, "(Lnet/minecraft/world/chunk/ChunkPrimer;IIILnet/minecraft/block/state/IBlockState;)V"),
	hooks_blockStateContainer_getDataForNBT(hooks, "blockStateContainer_getDataForNBT", null, "(Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;Lnet/minecraft/nbt/NBTTagCompound;[BLnet/minecraft/world/chunk/NibbleArray;)Lnet/minecraft/world/chunk/NibbleArray;"),
	hooks_blockStateContainer_setDataFromNBT(hooks, "blockStateContainer_setDataFromNBT", null, "(Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;Lnet/minecraft/nbt/NBTTagCompound;[BLnet/minecraft/world/chunk/NibbleArray;Lnet/minecraft/world/chunk/NibbleArray;)V"),
	hooks_chunkPrimer_findGroundBlockIdx(hooks, "chunkPrimer_findGroundBlockIdx", null, "(Lnet/minecraft/world/chunk/ChunkPrimer;II)I"),
	chunkPrimer_setBlockState(chunkPrimer, "setBlockState", "func_177855_a", "(IIILnet/minecraft/block/state/IBlockState;)V"),
	chunkPrimer_getBlockState(chunkPrimer, "getBlockState", "func_177856_a", "(III)Lnet/minecraft/block/state/IBlockState;"),
	chunkPrimer_findGroundBlockIdx(chunkPrimer, "findGroundBlockIdx", "func_186138_a", "(II)I"),
	acl_writeChunkToNBT(acl, "writeChunkToNBT", "func_75820_a", "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)V"),
	acl_readChunkFromNBT(acl, "readChunkFromNBT", "func_75823_a", "(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;"),
	ebs_getData(ebs, "getData", "func_186049_g", "()Lnet/minecraft/world/chunk/BlockStateContainer;"),
	bsc_getDataForNBT(bsc, "getDataForNBT", "func_186017_a", "([BLnet/minecraft/world/chunk/NibbleArray;)Lnet/minecraft/world/chunk/NibbleArray;"),
	bsc_setDataFromNBT(bsc, "setDataFromNBT", "func_186019_a", "([BLnet/minecraft/world/chunk/NibbleArray;Lnet/minecraft/world/chunk/NibbleArray;)V"),
	block_getStateId(block, "getStateId", "func_176210_f", "(Lnet/minecraft/block/state/IBlockState;)I"),
	block_getStateById(block, "getStateById", "func_176220_d", "(I)Lnet/minecraft/block/state/IBlockState;"),
	renderGlobal_playEvent(renderGlobal, "playEvent", "func_180439_a", "(Lnet/minecraft/entity/player/EntityPlayer;ILnet/minecraft/util/math/BlockPos;I)V"),
	packet_readPacketData(packet, "readPacketData", "func_148837_a", "(Lnet/minecraft/network/PacketBuffer;)V"),
	packet_writePacketData(packet, "writePacketData", "func_148840_b", "(Lnet/minecraft/network/PacketBuffer;)V"),
	packetBuffer_readItemStackFromBuffer(packetBuffer, "readItemStack", "func_150791_c", "()Lnet/minecraft/item/ItemStack;"),
	
	// fields
	chunkPrimer_data(chunkPrimer, "data", "field_177860_a", "[B");

	// for mc classes
	private Name(String deobf) {
		this.clazz = null;
		this.deobf = deobf;
		this.srg = deobf;
		this.desc = null;
	}

	// for fields and methods
	private Name(Name clazz, String deobf, String srg, String desc) {
		this.clazz = clazz;
		this.deobf = deobf;
		this.srg = srg != null ? srg : deobf;
		this.desc = desc;
	}

	public boolean matches(MethodNode x) {
		assert desc.startsWith("(");
		return srg.equals(x.name) && desc.equals(x.desc) || deobf.equals(x.name) && desc.equals(x.desc);
	}

	public boolean matches(FieldNode x) {
		assert !desc.startsWith("(");
		return srg.equals(x.name) && desc.equals(x.desc) || deobf.equals(x.name) && desc.equals(x.desc);
	}

	public boolean matches(MethodInsnNode x) {
		return matches(x, !Transformer.envDeobfuscated);
	}

	public boolean matches(MethodInsnNode x, boolean obfuscated) {
		assert desc.startsWith("(");
		if (obfuscated) {
			return clazz.srg.equals(x.owner) && srg.equals(x.name) && desc.equals(x.desc);
		}
		else {
			return clazz.deobf.equals(x.owner) && deobf.equals(x.name) && desc.equals(x.desc);
		}
	}

	public boolean matches(FieldInsnNode x, boolean obfuscated) {
		assert !desc.startsWith("(");
		if (obfuscated) {
			return clazz.srg.equals(x.owner) && srg.equals(x.name) && desc.equals(x.desc);
		}
		else {
			return clazz.deobf.equals(x.owner) && deobf.equals(x.name) && desc.equals(x.desc);
		}
	}

	public MethodInsnNode staticInvocation() {
		return staticInvocation(!Transformer.envDeobfuscated);
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

	public final Name clazz;
	public final String deobf;
	public String deobfDotted;
	public final String srg;
	public final String desc;

	static {
		for (Name name : Name.values()) {
			name.deobfDotted = name.deobf.replace('/', '.');
		}
	}
}
