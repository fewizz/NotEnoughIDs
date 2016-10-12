package ru.fewizz.neid.interfaces;

import net.minecraft.block.state.IBlockState;

public interface IChunkPrimer {
	public IBlockState getBlockState(int x, int y, int z);
	public void setBlockState(int x, int y, int z, IBlockState state);
	public int findGroundBlockIdx(int x, int z);
}
