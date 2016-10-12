package ru.fewizz.neid.interfaces;

import javax.annotation.Nullable;

import net.minecraft.world.chunk.NibbleArray;

public interface IBlockStateContainer {
	void setBits(int bits);
	NibbleArray[] getDataForNBT2(byte[] ids, NibbleArray meta);
	void setDataFromNBT2(byte[] ids, NibbleArray meta, @Nullable NibbleArray add1, @Nullable NibbleArray add2);
}
