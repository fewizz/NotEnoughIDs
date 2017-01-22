package ru.fewizz.neid.asm;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Hooks {
	static IBlockState AIR_BLOCK_STATE = Blocks.AIR.getDefaultState();
	
	public static byte[] chunkPrimer_getAdditionalData(ChunkPrimer cp) {
		return null; // Changes by transformer
	}

	static int chunkPrimer_getBlockIndex(int x, int y, int z) {
		return x << 12 | z << 8 | y;
	}

	public static IBlockState chunkPrimer_getBlockState(ChunkPrimer cp, int x, int y, int z) {
		int index = chunkPrimer_getBlockIndex(x, y, z);

		int id = cp.data[index];
		id = id | (chunkPrimer_getAdditionalData(cp)[index] << 16);

		IBlockState iblockstate = (IBlockState) Block.BLOCK_STATE_IDS.getByValue(id);
		return iblockstate == null ? AIR_BLOCK_STATE : iblockstate;
	}

	public static void chunkPrimer_setBlockState(ChunkPrimer cp, int x, int y, int z, IBlockState state) {
		int id = Block.BLOCK_STATE_IDS.get(state);

		int index = chunkPrimer_getBlockIndex(x, y, z);

		cp.data[index] = (char) (id & 0xFFFF);
		chunkPrimer_getAdditionalData(cp)[index] = (byte) (id >>> 16);
	}
	
	public static int chunkPrimer_findGroundBlockIdx(ChunkPrimer cp, int x, int z) {
		int i = (x << 12 | z << 8) + 256 - 1;
		
		char[] data = cp.data;
		byte[] add = chunkPrimer_getAdditionalData(cp);

		for (int j = 255; j >= 0; --j) {
			IBlockState iblockstate = (IBlockState) Block.BLOCK_STATE_IDS.getByValue(data[i + j] | (add[i + j] << 16));

			if (iblockstate != null && iblockstate != AIR_BLOCK_STATE) {
				return j;
			}
		}

		return 0;
	}
	
	public static NibbleArray blockStateContainer_getDataForNBT(ExtendedBlockStorage ebs, NBTTagCompound nbt, byte[] ids, NibbleArray metaNib) {
		NibbleArray add = null;
		NibbleArray add2 = null;

		for (int block = 0; block < 4096; block++) {
			IBlockState s = ebs.getData().get(block);
			
			if(s == AIR_BLOCK_STATE) {
				ids[block] = 0;
				metaNib.setIndex(block, 0);
				continue;
			}
			
			int id = Block.BLOCK_STATE_IDS.get(s);

			int in1 = (id >> 12) & 0xF;
			int in2 = (id >> 16) & 0xF;

			if (in1 != 0) {
				if (add == null) {
					add = new NibbleArray();
				}

				add.setIndex(block, in1);
			}
			if (in2 != 0) {
				if (add2 == null) {
					add2 = new NibbleArray();
				}

				add2.setIndex(block, in2);
			}

			ids[block] = (byte) ((id >> 4) & 0xFF);
			metaNib.setIndex(block, id & 0xF);
		}

		if(add2 != null) {
			nbt.setByteArray("Add2", add2.getData());
		}
		
		return add;
	}
	
	public static void blockStateContainer_setDataFromNBT(ExtendedBlockStorage ebs, NBTTagCompound nbt, byte[] ids, NibbleArray meta, @Nullable NibbleArray add1) {
		NibbleArray add2 = nbt.hasKey("Add2", 7) ? new NibbleArray(nbt.getByteArray("Add2")) : null;
		
		for (int block = 0; block < 4096; block++) {
			int toAdd = add1 == null ? 0 : add1.getFromIndex(block);
			if(add2 != null) {
				toAdd = (toAdd & 0xF) | (add2.getFromIndex(block) << 4);
			}
			int id = (toAdd << 12) | ((ids[block] & 0xFF) << 4) | meta.getFromIndex(block);
			ebs.getData().set(block, id == 0 ? AIR_BLOCK_STATE : (IBlockState) Block.BLOCK_STATE_IDS.getByValue(id));
		}
	}
}
