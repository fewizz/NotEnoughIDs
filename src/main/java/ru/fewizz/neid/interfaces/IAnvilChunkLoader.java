package ru.fewizz.neid.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IAnvilChunkLoader {
	void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound compound);
	Chunk readChunkFromNBT(World worldIn, NBTTagCompound compound);
}
