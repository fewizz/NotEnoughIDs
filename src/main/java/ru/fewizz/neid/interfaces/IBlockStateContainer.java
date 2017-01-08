package ru.fewizz.neid.interfaces;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.NibbleArray;

public interface IBlockStateContainer {
	void setBits(int bits);
	NibbleArray getDataForNBT2(NBTTagCompound nbt, byte[] ids, NibbleArray meta);
	void setDataFromNBT2(NBTTagCompound nbt, byte[] ids, NibbleArray meta, @Nullable NibbleArray add1);
}
